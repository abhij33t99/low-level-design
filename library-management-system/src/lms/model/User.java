package lms.model;

import lms.enums.UserType;

import java.util.Objects;

public abstract class User {
    private final String userId; // unique
    private final String name;
    private final UserType type;
    private final LibraryCard card; // R5, R6

    protected User(String userId, String name, UserType type, LibraryCard card) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.card = Objects.requireNonNull(card, "card");
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public UserType getType() { return type; }
    public LibraryCard getCard() { return card; }
}
