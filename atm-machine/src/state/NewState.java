package state;

import context.ATMContext;
import enums.State;
import factory.AtmStateFactory;
import model.Card;

public class NewState implements ATMState {

    @Override
    public void insertCard(ATMContext atm, Card card) {
        System.out.println("Card inserted successfully.");
        atm.setCard(card);
        atm.setState(AtmStateFactory.getState(State.CARD_INSERTED));
    }

    @Override
    public void enterPin(ATMContext atm) {
        System.out.println("Please insert a card first.");
    }

    @Override
    public void executeTransaction(ATMContext atm) {
        System.out.println("Please insert a card and enter your PIN first.");
    }

    @Override
    public void ejectCard(ATMContext atm) {
        System.out.println("No card to eject.");
    }
}
