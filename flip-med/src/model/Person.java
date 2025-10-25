package model;

import java.util.UUID;

public abstract class Person {
    private final UUID id;
    private String name;

    protected Person( String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
