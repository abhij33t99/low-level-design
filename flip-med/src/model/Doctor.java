package model;

import enums.Specialization;

public class Doctor extends Person{

    private final Specialization specialization;
    private double rating;

    public Doctor(String name, Specialization specialization, double rating) {
        super(name);
        this.specialization = specialization;
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Specialization getSpecialization() {
        return specialization;
    }
}
