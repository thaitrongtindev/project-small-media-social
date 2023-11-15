package com.example.socialmediasmall.model;

import android.net.Uri;

public class GalleryImages {
    private Uri picUri;
    private String date;

    public GalleryImages(Uri picUri, String date) {
        this.picUri = picUri;
        this.date = date;
    }

    public GalleryImages() {
    }

    public Uri getPicUri() {
        return picUri;
    }

    public void setPicUri(Uri picUri) {
        this.picUri = picUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
