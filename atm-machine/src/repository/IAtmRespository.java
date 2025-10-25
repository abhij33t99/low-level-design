package repository;

import java.util.List;
import java.util.Map;

public interface IAtmRespository {
    void addCash(List<String> denominations);
    void removeCash(int denomination, int notes);
    int getBalance();
    Map<Integer, Integer> getDenominations();
}
