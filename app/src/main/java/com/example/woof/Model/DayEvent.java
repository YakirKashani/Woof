package com.example.woof.Model;

public class DayEvent {
    private boolean hasMedicine;
    private boolean hasVaccine;

    public DayEvent() {
        hasMedicine = false;
        hasVaccine = false;
    }

    public DayEvent(boolean hasMedicine, boolean hasVaccine) {
        this.hasMedicine = hasMedicine;
        this.hasVaccine = hasVaccine;
    }
    public boolean hasMedicine() {
        return hasMedicine;
    }

    public boolean hasVaccine() {
        return hasVaccine;
    }

    public void setHasMedicine(boolean hasMedicine) {
        this.hasMedicine = hasMedicine;
    }

    public void setHasVaccine(boolean hasVaccine) {
        this.hasVaccine = hasVaccine;
    }

}
