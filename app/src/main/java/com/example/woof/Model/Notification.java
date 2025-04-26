package com.example.woof.Model;

public class Notification {
    private String ownerCreatedNotification;
    private String dogCreatedNotification;
    private String message;
    private String imgUrl;
    private boolean isNew;

    public Notification() {
    }

    public Notification(String ownerCreatedNotification, String dogCreatedNotification, String message, String imgUrl, boolean isNew) {
        this.ownerCreatedNotification = ownerCreatedNotification;
        this.dogCreatedNotification = dogCreatedNotification;
        this.message = message;
        this.imgUrl = imgUrl;
        this.isNew = isNew;
    }

    public String getOwnerCreatedNotification() {
        return ownerCreatedNotification;
    }

    public void setOwnerCreatedNotification(String ownerCreatedNotification) {
        this.ownerCreatedNotification = ownerCreatedNotification;
    }

    public String getDogCreatedNotification() {
        return dogCreatedNotification;
    }

    public void setDogCreatedNotification(String dogCreatedNotification) {
        this.dogCreatedNotification = dogCreatedNotification;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "ownerCreatedNotification='" + ownerCreatedNotification + '\'' +
                ", dogCreatedNotification='" + dogCreatedNotification + '\'' +
                ", message='" + message + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", isNew=" + isNew +
                '}';
    }
}