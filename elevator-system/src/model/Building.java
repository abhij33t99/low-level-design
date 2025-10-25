package model;

import service.ElevatorService;

public class Building {
    private String name;
    private int numberOfFloors;
    private ElevatorService elevatorService;

    public Building(String name, int numberOfFloors, int noOFElevators) {
        this.name = name;
        this.numberOfFloors = numberOfFloors;
        this.elevatorService = new ElevatorService(noOFElevators, numberOfFloors);
    }

    public String getName() {
        return name;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public ElevatorService getElevatorService() {
        return elevatorService;
    }
}
