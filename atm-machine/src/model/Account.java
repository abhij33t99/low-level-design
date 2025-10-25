package model;

public class Account {
    private final String name;
    private final Long accountNumber;
    private int balance;

    public Account(String name, Long accountNumber, int balance) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void withdraw(int amount) {
        this.balance -= amount;
    }
}
