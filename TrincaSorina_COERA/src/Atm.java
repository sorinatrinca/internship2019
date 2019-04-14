import java.time.LocalTime;
import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Atm {
    private String name;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private float amount;

    private HashMap<Atm,Integer> durations;

    public Atm(){}

    public Atm(String name, LocalTime openingTime, LocalTime closingTime, float amount) {
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.amount = amount;
        durations = new HashMap<>();
    }

    public List<Atm> getSortedAtmsFromThisAtm()
    {
        HashMap<Atm,Integer> sortedAtms = durations.entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect( toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                        LinkedHashMap::new));
        Set<Atm> atmSet = sortedAtms.keySet();
        return new ArrayList<>(atmSet);
    }

    public void addDuration(Atm atm, int minutes)
    {
        if (!durations.containsKey(atm))
        {
            durations.put(atm,minutes);
            atm.addDuration(this, minutes);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public HashMap<Atm, Integer> getDurations() {
        return durations;
    }

    public void setDurations(HashMap<Atm, Integer> durations) {
        this.durations = durations;
    }
}
