package lms.model;

import java.util.Objects;

public class LibraryCard {
    private final String cardNumber; // unique

    public LibraryCard(String cardNumber) {
        this.cardNumber = Objects.requireNonNull(cardNumber, "cardNumber");
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
