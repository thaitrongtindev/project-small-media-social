package com.example.socialmediasmall.interfaceListener;

public interface OnPressed {
    void onLike(int position, String id);
    void onComment(int position, String id, String comment);
}
