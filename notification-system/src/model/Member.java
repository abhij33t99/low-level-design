package model;

import enums.MemberType;

public record Member(String id, MemberType type, String name) {
}
