package com.example.woof.Model;

public class Notification {
    private String ownerCreatedNotification;
    private String dogCreatedNotification;
    private String message;
    private String imgUrl;
    private boolean newNotification;

    public Notification() {
    }

    public Notification(String ownerCreatedNotification, String dogCreatedNotification, String message, String imgUrl, boolean newNotification) {
        this.ownerCreatedNotification = ownerCreatedNotification;
        this.dogCreatedNotification = dogCreatedNotification;
        this.message = message;
        this.imgUrl = imgUrl;
        this.newNotification = newNotification;
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

    public boolean isNewNotification() {
        return newNotification;
    }

    public void setNewNotification(boolean newNotification) {
        this.newNotification = newNotification;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "ownerCreatedNotification='" + ownerCreatedNotification + '\'' +
                ", dogCreatedNotification='" + dogCreatedNotification + '\'' +
                ", message='" + message + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", newNotification=" + newNotification +
                '}';
    }
}