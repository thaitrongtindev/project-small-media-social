package com.example.socialmediasmall.model;

public class HomeModel {

    private String username;
    private String timestamps;
    private String profileImage;
    private String postImage;
    private String uId;
    private int likeCount;
    private String comments, description;

    public HomeModel() {
    }

    public HomeModel(String username, String timestamps, String profileImage, String postImage, String uId, int likeCount, String comments, String description) {
        this.username = username;
        this.timestamps = timestamps;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.uId = uId;
        this.likeCount = likeCount;
        this.comments = comments;
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(String timestamps) {
        this.timestamps = timestamps;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
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
}
