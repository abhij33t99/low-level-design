package state;

import context.ATMContext;
import enums.State;
import factory.AtmStateFactory;
import model.Card;

public class CompletedState implements ATMState {
    @Override
    public void insertCard(ATMContext atm, Card card) {
        System.out.println("Please eject the current card before inserting a new one.");
    }

    @Override
    public void enterPin(ATMContext atm) {
        System.out.println("Please eject the card first.");
    }

    @Override
    public void executeTransaction(ATMContext atm) {
        System.out.println("Transaction already completed. Please eject the card.");
    }

    @Override
    public void ejectCard(ATMContext atm) {
        System.out.println("Card ejected! Collect your card.");
        atm.setCard(null);
        atm.setState(AtmStateFactory.getState(State.NEW));
    }
}
