package state;

import context.ATMContext;
import model.Card;

import java.util.Scanner;

public interface ATMState {
    void insertCard(ATMContext atm, Card card);
    void enterPin(ATMContext atm);
    void executeTransaction(ATMContext atm);
    void ejectCard(ATMContext atm);
}
