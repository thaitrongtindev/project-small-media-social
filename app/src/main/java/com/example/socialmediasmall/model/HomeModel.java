package com.example.socialmediasmall.model;

public class HomeModel {

    private String username;
    private String timestamps;
    private String profileImage;
    private String postImage;
    private String uId;
    private int likeCount;

    public HomeModel(String username, String timestamps, String profileImage, String postImage, String uId, int likeCount) {
        this.username = username;
        this.timestamps = timestamps;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.uId = uId;
        this.likeCount = likeCount;
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
}
