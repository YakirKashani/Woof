package com.example.woof.Model;

public class Comment {
    private String ownerMail;
    private String dogName;
    private String comment;

    public Comment() {
    }

    public String getOwnerMail() {
        return ownerMail;
    }

    public void setOwnerMail(String ownerMail) {
        this.ownerMail = ownerMail;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "ownerMail='" + ownerMail + '\'' +
                ", dogName='" + dogName + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
