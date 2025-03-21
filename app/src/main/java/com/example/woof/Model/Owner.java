package com.example.woof.Model;

public class Owner {

    private String mail;
    private String photoURL;

    public Owner(){

    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "mail='" + mail + '\'' +
                ", photoURL='" + photoURL + '\'' +
                '}';
    }
}
