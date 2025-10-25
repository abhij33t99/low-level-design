package cor;

import repository.IAtmRespository;

public class Cash2000Dispenser extends CashDispenser {
    private static final int DENOMINATION = 2000;

    public Cash2000Dispenser(IAtmRespository respository) {
        super(respository);
    }

    @Override
    public boolean canDispenseCash(int amount) {
        if (amount == 0) {
            return true;
        }
        int availableNotes = respository.getDenominations().getOrDefault(DENOMINATION, 0);
        int notesNeeded = amount / DENOMINATION;
        int notesToUse = Math.min(notesNeeded, availableNotes);

        int remainingAmount = amount - (notesToUse * DENOMINATION);

        if (next != null) {
            return next.canDispenseCash(remainingAmount);
        } else {
            return remainingAmount == 0;
        }
    }

    @Override
    public void dispenseCash(int amount) {
        if (amount == 0) {
            return;
        }
        int availableNotes = respository.getDenominations().getOrDefault(DENOMINATION, 0);
        int notesToDispense = Math.min(amount / DENOMINATION, availableNotes);

        if (notesToDispense > 0) {
            System.out.println("Dispensing " + notesToDispense + " notes of " + DENOMINATION);
            respository.removeCash(DENOMINATION, notesToDispense);
        }
        int remainingAmount = amount - (notesToDispense * DENOMINATION);

        if (next != null) {
            next.dispenseCash(remainingAmount);
        } else if (remainingAmount > 0) {
            System.out.println("Error: Could not dispense exact amount. Remaining: " + remainingAmount);
        }
    }
}
