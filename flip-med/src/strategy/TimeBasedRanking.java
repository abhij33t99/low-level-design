package strategy;

import dto.SlotWithDoctor;

import java.util.Comparator;
import java.util.List;

public class TimeBasedRanking implements SlotRankingStrategy {
    @Override
    public List<SlotWithDoctor> sort(List<SlotWithDoctor> slotWithDoctorList) {
        slotWithDoctorList.sort(
                Comparator.comparing((SlotWithDoctor swd) -> swd.getSlot().getStartTime())
                          .thenComparing(swd -> swd.getDoctor().getName())
        );
        return slotWithDoctorList;
    }
}
