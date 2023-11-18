package com.example.socialmediasmall.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostImageModel {

    private String imageUrl, id, description , uId;
    @ServerTimestamp
    private Date timestamp;

    public PostImageModel() {

    }
    public PostImageModel(String imageUrl, String id, Date timestamp, String description, String uId) {
        this.imageUrl = imageUrl;
        this.id = id;
        this.timestamp = timestamp;
        this.description = description;
        this.uId = uId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
