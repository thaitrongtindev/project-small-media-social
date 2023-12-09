package com.example.socialmediasmall.model;

public class CommentModel {

    private String comment, commentID, postID, name, profileImageUrl;

    public CommentModel() {
    }

    public CommentModel(String comment, String commentID, String postID, String name, String profileImageUrl) {
        this.comment = comment;
        this.commentID = commentID;
        this.postID = postID;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
