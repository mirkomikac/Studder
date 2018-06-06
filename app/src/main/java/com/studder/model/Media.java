package com.studder.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Media implements Serializable {

    private static final long serialVersionUID = 6244895049508882158L;

    private Long id;
    private String path;
    private String description;
    private Date timeAdded;
    private Boolean isPrivate;
    private User user;
    private Bitmap bitmap;
    private String encodedImage;

    public Media() {
    }

    public Media(String path, String description, Date timeAdded, Boolean isPrivate, User user) {
        this.path = path;
        this.description = description;
        this.timeAdded = timeAdded;
        this.isPrivate = isPrivate;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Date timeAdded) {
        this.timeAdded = timeAdded;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }
}

