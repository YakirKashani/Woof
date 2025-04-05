package com.example.woof.Model;

public class Vaccine {
    private String name;
    private String date;

    public Vaccine() {
    }

    public Vaccine(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RequestVaccine{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
