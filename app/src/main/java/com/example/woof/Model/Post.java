package com.example.woof.Model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class Post implements Serializable {
    private Long id;
    private String dogOwner;
    private String dogName;
    private String description;
    private String pictureUrl;
    private List<String> dogsLiked;
    private String date;
    private List<Comment> comments;

    public Post() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDogOwner() {
        return dogOwner;
    }

    public void setDogOwner(String dogOwner) {
        this.dogOwner = dogOwner;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<String> getDogsLiked() {
        return dogsLiked;
    }

    public void setDogsLiked(List<String> dogsLiked) {
        this.dogsLiked = dogsLiked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", dogOwner='" + dogOwner + '\'' +
                ", dogName='" + dogName + '\'' +
                ", description='" + description + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", dogsLiked=" + dogsLiked +
                ", date='" + date + '\'' +
                ", comments=" + comments +
                '}';
    }
}
