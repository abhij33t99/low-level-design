package lms.model;

import lms.enums.UserType;

public class Librarian extends User {
    public Librarian(String userId, String name, LibraryCard card) {
        super(userId, name, UserType.LIBRARIAN, card);
    }
}
