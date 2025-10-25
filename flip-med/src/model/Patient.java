package model;

import java.util.List;

public class Patient extends Person{

    private List<Appointment> appointments;

    public Patient(String name, List<Appointment> appointments) {
        super(name);
        this.appointments = appointments;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
