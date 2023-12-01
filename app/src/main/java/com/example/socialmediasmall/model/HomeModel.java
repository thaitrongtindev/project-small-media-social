package com.example.socialmediasmall.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class HomeModel {

    private String name;
    @ServerTimestamp
    private Date timestamp;
    private String profileImage;
    private String imageUrl;
    private String uid;
    private int likeCount;
    private String comments, description, id;

    public HomeModel() {
    }

    public HomeModel(String name, Date timestamp, String profileImage, String imageUrl, String uid, int likeCount, String comments, String description, String id) {
        this.name = name;
        this.timestamp = timestamp;
        this.profileImage = profileImage;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.likeCount = likeCount;
        this.comments = comments;
        this.description = description;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String postImage) {
        this.imageUrl = postImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
