package lms.model;

import lms.enums.UserType;

public class Member extends User {
    public Member(String userId, String name, LibraryCard card) {
        super(userId, name, UserType.MEMBER, card);
    }
}
