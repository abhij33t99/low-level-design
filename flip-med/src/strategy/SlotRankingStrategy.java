package strategy;

import dto.SlotWithDoctor;
import model.Slot;

import java.util.List;

public interface SlotRankingStrategy {
    List<SlotWithDoctor> sort(List<SlotWithDoctor> slotWithDoctorList);
}
