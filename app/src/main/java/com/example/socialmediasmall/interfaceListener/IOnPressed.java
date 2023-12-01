package com.example.socialmediasmall.interfaceListener;

import java.util.List;

public interface IOnPressed {
    void onLiked(int position, String id, String uid, List<String> mListLikes, boolean isChecked);
    void onComment(int position, String id, String uid, String comment);
}
