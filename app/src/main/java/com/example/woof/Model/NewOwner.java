package com.example.woof.Model;

public class NewOwner {
    private String mail;
 //   private String photoBase64;
    private String photoURL;

    public NewOwner(){

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
        return "NewOwner{" +
                "mail='" + mail + '\'' +
                ", photoURL='" + photoURL + '\'' +
                '}';
    }
}
