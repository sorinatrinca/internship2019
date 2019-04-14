import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    private List<Card> cards;
    private HashMap<Atm,Integer> durations;

    public User(){
        cards = new ArrayList<>();
        durations = new HashMap<>();
    }

    /**
     *
     * @param currentDate data curenta in care ne aflam
     * @return lista de carduri valide : care nu sunt expirate
     */
    public List<Card> getOnlyValidCards(LocalDate currentDate)
    {
        List<Card> validCards = new ArrayList<>();
        for (Card card : cards)
        {
            if (card.getExpirationDate().isAfter(currentDate))
                validCards.add(card);
        }

        return validCards;
    }

    public int getDurationByAtm(Atm atm)
    {
        return durations.get(atm);
    }

    public User(List<Card> cards) {
        this.cards = cards;
    }

    public void addCard(Card card)
    {
        cards.add(card);
    }

    public void removeVard(Card card)
    {
        cards.remove(card);
    }

    public void addDuration(Atm atm, int minutes)
    {
        durations.put(atm,minutes);
    }


    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public HashMap<Atm, Integer> getDurations() {
        return durations;
    }

    public void setDurations(HashMap<Atm, Integer> durations) {
        this.durations = durations;
    }
}
