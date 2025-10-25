package factory;

import enums.State;
import state.ATMState;
import state.CardInsertedState;
import state.NewState;
import state.TransactionState;
import state.CompletedState;

public class AtmStateFactory {

    public static ATMState getState(State state) {
        return switch (state) {
            case NEW -> new NewState();
            case CARD_INSERTED -> new CardInsertedState();
            case TRANSACTION -> new TransactionState();
            case COMPLETED -> new CompletedState();
            default -> throw new RuntimeException("Wrong state");
        };
    }
}
