package com.example.woof.Model;

public class Dog {
    private String breed;
    private String ownerEmail;
    private String photoURL;
    private String name;
    private String dob;
    private float weight;
    private String gender;


    public Dog() {
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "breed='" + breed + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", name='" + name + '\'' +
                ", dob='" + dob + '\'' +
                ", weight=" + weight +
                ", gender='" + gender + '\'' +
                '}';
    }
}
