package com.example.socialmediasmall.model;

public class CommentModel {

    private String comment, commentID, postID;

    public CommentModel(String comment, String commentID, String postID) {
        this.comment = comment;
        this.commentID = commentID;
        this.postID = postID;
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
}
