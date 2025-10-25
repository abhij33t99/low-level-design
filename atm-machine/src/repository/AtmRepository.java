package repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtmRepository implements IAtmRespository{

    private final Map<Integer, Integer> denominations;
    private int availableCash;

    public AtmRepository() {
        this.denominations = new HashMap<>();
        availableCash = 0;
    }

    @Override
    public void addCash(List<String> denominations) {
        for(String note : denominations) {
            String[] value = note.split(":");
            this.denominations.merge(Integer.parseInt(value[0]), Integer.parseInt(value[1]), Integer::sum);
            availableCash += Integer.parseInt(value[0]) * Integer.parseInt(value[1]);
        }
    }

    @Override
    public void removeCash(int denomination, int notes) {
        denominations.merge(denomination, -notes, Integer::sum);
    }

    @Override
    public int getBalance() {
        return availableCash;
    }

    @Override
    public Map<Integer, Integer> getDenominations() {
        return denominations;
    }
}
