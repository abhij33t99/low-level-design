package model;

public class Card {
    private final Account account;
    private final int pin;

    public Card(Account account, int pin) {
        this.account = account;
        this.pin = pin;
    }

    public Account getAccount() {
        return account;
    }

    public int getPin() {
        return pin;
    }

    public boolean validatePin(int pin) {
        return pin == this.pin;
    }
}
