package com.example.socialmediasmall.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class HomeModel {

    private String username;
    @ServerTimestamp
    private Date timestamp;
    private String profileImage;
    private String imageUrl;
    private String uId;
    private int likeCount;
    private String comments, description, id;

    public HomeModel() {
    }

    public HomeModel(String username, Date timestamp, String profileImage, String imageUrl, String uId, int likeCount, String comments, String description, String id) {
        this.username = username;
        this.timestamp = timestamp;
        this.profileImage = profileImage;
        this.imageUrl = imageUrl;
        this.uId = uId;
        this.likeCount = likeCount;
        this.comments = comments;
        this.description = description;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
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
