package strategy;

import dto.SlotWithDoctor;

import java.util.Comparator;
import java.util.List;

public class RatingBasedRanking implements SlotRankingStrategy {
    @Override
    public List<SlotWithDoctor> sort(List<SlotWithDoctor> slotWithDoctorList) {
        slotWithDoctorList.sort(
                Comparator.comparingDouble((SlotWithDoctor swd) -> swd.getDoctor().getRating())
                          .reversed()
                          .thenComparing(swd -> swd.getSlot().getStartTime())
                          .thenComparing(swd -> swd.getDoctor().getName())
        );
        return slotWithDoctorList;
    }
}
