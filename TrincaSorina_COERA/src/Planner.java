import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class Planner {
    private User user;
    private List<Atm> atms;
    private float money;
    private LocalDateTime deadline;
    private LocalDateTime currentDateTime;
    private LocationType currentLocation;

    public Planner(User user, List<Atm> atms, float money, LocalDateTime deadline, LocalDateTime currentDateTime, LocationType currentLocation) {
        this.user = user;
        this.atms = atms;
        this.money = money;
        this.deadline = deadline;
        this.currentDateTime = currentDateTime;
        this.currentLocation = currentLocation;
    }

    /**
     *
     * @return lista cu ATM-urile valide: deschise in acest moment, care vor fi deschiSe si dupa ce utilizatorul va ajunge la ele si
     *         care se inchid dupa ce utilizatorul ar ajunge la ele
     */
    private List<Atm> getOpenAtms() {
        List<Atm> openAtms = new ArrayList<>();
        for (Atm atm : atms) {
            int duration = user.getDurationByAtm(atm);
            if ( atm.getOpeningTime().isBefore(currentDateTime.toLocalTime()) || atm.getOpeningTime().isBefore(currentDateTime.toLocalTime().plusMinutes(duration)))
                if( atm.getClosingTime().isAfter(currentDateTime.toLocalTime().plusMinutes(duration)))
                    openAtms.add(atm);
        }
        return openAtms;
    }

    /**
     * Planificarea cardurilor impreuna cu sumele necesare a fi scoase de pe fiecare, astfel incat sa se ajunga la suma target
     * @return un HashMap care contine cardurile de credit care vor fi folosite de catre utilizator, impreuna cu suma care va trebui scoasa de pe fiecare card
     */
    public HashMap<Card, Float> getCardPlan() {
        List<Card> userCards = user.getOnlyValidCards(currentDateTime.toLocalDate());
        Collections.sort(userCards);

        float auxMoney = money;

        HashMap<Card, Float> cardPlannerMap = new HashMap<>();
        int k = 0;
        while (auxMoney > 0 && k < userCards.size()) {
            Card card = userCards.get(k);
            float availableAmount = card.getAmount();
            float withdrawLimit = card.getWithdrawLimit();
            float possibleAmount = 0;
            float fee;
            if (availableAmount > withdrawLimit)
                possibleAmount = withdrawLimit;
            else
                possibleAmount = availableAmount;

            if (possibleAmount > auxMoney) {
                cardPlannerMap.put(card, auxMoney);
                auxMoney =0;
            } else {
                cardPlannerMap.put(card, possibleAmount);
                auxMoney -= possibleAmount;
            }

            k++;

        }

        if (auxMoney > 0)
            return null; //nu e suficient amount pe carduri sau nu pot extrage asa mult de pe ele
        else
            return cardPlannerMap;
    }


    /**
     * Folosim o stiva pentru a crea un efect de backtracking in incercarea mai multor rute
     * Daca prima ruta nu satisface conditiile (nu ne incadram in timpul target sau nu am reusit sa scoatem suficienti bani de la acele ATM uri din ruta)
     *      vom renunta la ultimul ATM vizitat si vom lua ATM ul din varful stivei, avand grija sa restauram starea de dinainte de vizitarea ultimului ATM
     * @return lista de ATM uri care trebuie parcurse in scopul retragerii sumei target de bani
     */
    public List<Atm> getAtmsRoute()
    {
        List<Atm> route = new ArrayList<>();
        float currentExtractedSum = 0;
        LocalDateTime auxLocalDateTime = currentDateTime;

        HashMap<Card, Float> cardPlannerMap = getCardPlan();
        HashMap<Atm, Integer> userDistances = user.getDurations();

        Stack<Atm> s = new Stack<>();


        //sortam ATM-urile la care poate ajunge userul in functie de distanta
        HashMap<Atm,Integer> sortedAtms = userDistances.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect( toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                        LinkedHashMap::new));
        Set<Atm> atmSet = sortedAtms.keySet();

        //initial adaug toate ATM urile la care poate ajunge utilizatorul, in stiva
        for (Atm atm : atmSet)
            s.push(atm);

        List<Atm> visitedAtm = new ArrayList<>();
        boolean finish = false;

        while (!s.isEmpty())
        {
            //scot un ATM din stiva
            Atm currentAtm = s.pop();

            List<Atm> openAtms = getOpenAtms();
            //daca atm e deschis si are bani si nu a fost vizitat si timpul pana acolo nu depaseste deadlineul
            if (openAtms.contains(currentAtm) && currentAtm.getAmount()>0 && !visitedAtm.contains(currentAtm) &&  currentDateTime.plusMinutes(user.getDurationByAtm(currentAtm)).isBefore(deadline))
            {
                auxLocalDateTime = currentDateTime;
                currentDateTime = currentDateTime.plusMinutes(user.getDurationByAtm(currentAtm));
                route.add(currentAtm);
                System.out.println("current ATM : "+currentAtm.getName());

                Set<Card> mySet = cardPlannerMap.keySet();
                List<Card> plannerCards = new ArrayList<>(mySet);
                int n = cardPlannerMap.keySet().size();
                int k = 0;
                float amountToExtract = 0;

                //for backup
                HashMap<Card,Float> removedCards = new HashMap<>();
                float initialAtmAmount = currentAtm.getAmount();
                float initialExtractedSum = currentExtractedSum;

                while ((cardPlannerMap.size() > 0) && (currentAtm.getAmount() > 0) && (k < n)) {
                    System.out.println();
                    //iau un card si scot de pe el
                    Card cardToPerform = plannerCards.get(k);
                    System.out.println("card to perform "+cardToPerform.getType());

                    amountToExtract = cardPlannerMap.get(cardToPerform);
                    System.out.println("amount to extract "+amountToExtract);

                    boolean removeFlag = true;
                    float initialAmountToExtraxt = amountToExtract;
                    if (amountToExtract>currentAtm.getAmount())
                    {
                        amountToExtract = currentAtm.getAmount();
                        System.out.println("new amount to extract due to atm balance "+ amountToExtract);
                        removeFlag = false;
                    }

                    if (removeFlag == true)
                    {

                        removedCards.put(cardToPerform,amountToExtract);
                        //scot cardul din plannerul de carduri
                        cardPlannerMap.remove(cardToPerform);
                    }
                    else
                        cardPlannerMap.put(cardToPerform, initialAmountToExtraxt- amountToExtract);

                    currentExtractedSum += amountToExtract;
                    currentAtm.setAmount(currentAtm.getAmount()-amountToExtract);

                    System.out.println("new ATM balance "+currentAtm.getAmount());


                    k++;
                }
                if (cardPlannerMap.size() == 0)
                {
                    System.out.println("\nFINISH\n");
                    finish = true;
                    break;
                }
                else if (currentAtm.getAmount() == 0)
                {
                    System.out.println("\nCAN'T EXTRACT MORE MONEY FROM "+currentAtm.getName()+"\n");
                    List<Atm> possibleAtms = currentAtm.getSortedAtmsFromThisAtm();
                    boolean fail = true;
                    for (Atm a : possibleAtms)
                    {
                        if (!visitedAtm.contains(a))
                        {
                            fail = false;
                            break;
                        }
                    }

                    //daca nu am mai gasit un atm nevizitat si inca mai avem bani de scos, trecem la urmatorul atm din coada si refacem ce am pagubit cu acest atm
                    if (fail == true)
                    {
                        System.out.println("RESTORING \n");
                        //restore removed cards
                        Set<Card> auxSet = removedCards.keySet();
                        for(Card c : auxSet)
                            cardPlannerMap.put(c,removedCards.get(c));

                        //restore initial amount
                        currentExtractedSum = initialExtractedSum;

                        //restore current atm amount
                        currentAtm.setAmount(initialAtmAmount);

                        //mark atm unvisited
                        visitedAtm.remove(currentAtm);

                        //delete this atm from the route
                        route.remove(currentAtm);

                        //restore time
                        currentDateTime = auxLocalDateTime;
                    }
                    else
                    { //daca exista cel putin inca un ATM la care pot ajunge de la ATM-ul curent, adaug sau le adaug acele ATM uri in stiva pentru a continua traseul
                        Collections.reverse(possibleAtms);
                        for (Atm a : possibleAtms)

                            if (!visitedAtm.contains(a))
                            {
                                s.push(a);
                            }
                    }

                }
            }
        }


        return route;
    }


}
