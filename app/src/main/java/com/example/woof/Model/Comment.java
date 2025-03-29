package com.example.woof.Model;

public class Comment {
    private String dogId;
    private String comment;

    public Comment() {
    }

    public String getDogId() {
        return dogId;
    }

    public void setDogId(String dogId) {
        this.dogId = dogId;
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
                "dogId='" + dogId + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
