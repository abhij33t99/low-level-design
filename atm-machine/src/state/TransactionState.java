package state;

import context.ATMContext;
import enums.State;
import factory.AtmStateFactory;
import model.Card;

public class TransactionState implements ATMState {
    @Override
    public void insertCard(ATMContext atm, Card card) {
        System.out.println("A transaction is in progress. Please complete or cancel it first.");
    }

    @Override
    public void enterPin(ATMContext atm) {
        System.out.println("PIN has already been entered.");
    }

    @Override
    public void executeTransaction(ATMContext atm) {
        System.out.println("Enter 1 to withdraw or 2 to view balance");
        int choice = -1;
        try {
            choice = Integer.parseInt(atm.getScanner().nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        switch (choice) {
            case 1: {
                System.out.print("Enter amount to withdraw: ");
                int amount = -1;
                try {
                    amount = Integer.parseInt(atm.getScanner().nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount. Please enter a number.");
                    return;
                }

                if (amount <= 0) {
                    System.out.println("Invalid amount.");
                    return;
                }

                if (amount > atm.getCard().getAccount().getBalance()) {
                    System.out.println("Not enough balance in account");
                } else if (amount > atm.getAtmRepository().getBalance()) {
                    System.out.println("Not enough cash in atm");
                } else if (!atm.getCashDispenser().canDispenseCash(amount)) {
                    System.out.println("Cannot dispense the selected amount, choose another amount");
                } else {
                    atm.getCashDispenser().dispenseCash(amount);
                    atm.getCard().getAccount().withdraw(amount);
                    System.out.println("Amount withdrawn successfully!");
                }
                break;
            }
            case 2: {
                System.out.println("Balance: " + atm.getCard().getAccount().getBalance());
                break;
            }
            default: {
                System.out.println("Invalid choice.");
                return;
            }
        }
        atm.setState(AtmStateFactory.getState(State.COMPLETED));
    }

    @Override
    public void ejectCard(ATMContext atm) {
        System.out.println("Card ejected.");
        atm.setCard(null);
        atm.setState(AtmStateFactory.getState(State.NEW));
    }
}
