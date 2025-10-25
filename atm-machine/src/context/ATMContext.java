package context;

import cor.CashDispenser;
import cor.CashDispenserChainBuilder;
import model.Card;
import repository.IAtmRespository;
import state.ATMState;

import java.util.Scanner;

public class ATMContext {
    private Card card;
    private ATMState state;
    private final IAtmRespository atmRepository;
    private final CashDispenser cashDispenser;
    private final Scanner scanner;

    public ATMContext(IAtmRespository atmRepository, Scanner scanner) {
        this.atmRepository = atmRepository;
        this.cashDispenser = CashDispenserChainBuilder.buildChain(atmRepository);
        this.scanner = scanner;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public ATMState getState() {
        return state;
    }

    public void setState(ATMState state) {
        this.state = state;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public IAtmRespository getAtmRepository() {
        return atmRepository;
    }

    public CashDispenser getCashDispenser() {
        return cashDispenser;
    }

    public void insertCard(Card card) {
        this.state.insertCard(this, card);
    }

    public void enterPin() {
        this.state.enterPin(this);
    }

    public void executeTransaction() {
        this.state.executeTransaction(this);
    }

    public void ejectCard() {
        this.state.ejectCard(this);
    }
}
