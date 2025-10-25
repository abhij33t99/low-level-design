package state;

import context.ATMContext;
import enums.State;
import factory.AtmStateFactory;
import model.Card;

public class CardInsertedState implements ATMState {

    @Override
    public void insertCard(ATMContext atm, Card card) {
        System.out.println("A card is already inserted.");
    }

    @Override
    public void enterPin(ATMContext atm) {
        System.out.print("Enter PIN: ");
        try {
            int pin = Integer.parseInt(atm.getScanner().nextLine());
            if (atm.getCard().validatePin(pin)) {
                System.out.println("PIN validation successful.");
                atm.setState(AtmStateFactory.getState(State.TRANSACTION));
            } else {
                System.out.println("Incorrect PIN.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid PIN format. Please enter numbers only.");
        }
    }

    @Override
    public void executeTransaction(ATMContext atm) {
        System.out.println("Please enter your PIN first.");
    }

    @Override
    public void ejectCard(ATMContext atm) {
        System.out.println("Card ejected.");
        atm.setCard(null);
        atm.setState(AtmStateFactory.getState(State.NEW));
    }
}
