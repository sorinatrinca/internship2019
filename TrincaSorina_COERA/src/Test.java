import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args)
    {
        User user = new User();

        Card silverCard = new Card(0.2f,4500f, LocalDate.of(2020,5,23),20000, CardType.SILVER);
        Card goldCard = new Card(0.1f,3000f, LocalDate.of(2018,8,15),25000, CardType.GOLD);
        Card platinumCard = new Card(0.0f,4000f, LocalDate.of(2019,3,20),3000, CardType.PLATINUM);

        user.addCard(silverCard);
        user.addCard(goldCard);
        user.addCard(platinumCard);

        Atm atm1 = new Atm("ATM 1",LocalTime.of(12,0),LocalTime.of(18,0),5000);
        Atm atm2 = new Atm("ATM 2",LocalTime.of(10,0),LocalTime.of(17,0),5000);
        Atm atm3 = new Atm("ATM 3",LocalTime.of(22,0),LocalTime.of(12,0),5000);
        Atm atm4 = new Atm("ATM 4",LocalTime.of(17,0),LocalTime.of(01,0),5000);


        List<Atm> atms = new ArrayList<>();
        atms.add(atm1);
        atms.add(atm2);
        atms.add(atm3);
        atms.add(atm4);

        user.addDuration(atm1,5);
        user.addDuration(atm2,60);
        user.addDuration(atm3,30);
        user.addDuration(atm4,45);

        atm1.addDuration(atm2,40);
        atm1.addDuration(atm4,45);

        atm2.addDuration(atm3,15);

        atm3.addDuration(atm4,40);
        atm3.addDuration(atm4,15);

        atm4.addDuration(atm2,30);

        Planner planner = new Planner(
                user,
                atms,7500,
                LocalDateTime.of(2019,3,19,14,00),
                LocalDateTime.of(2019,3,19,11,30),
                LocationType.USER_STARTING_POINT);


        List<Atm> route = planner.getAtmsRoute();

        System.out.println("ATMS ROUTE:");
        for (Atm a : route)
            System.out.println(a.getName());

    }
}
