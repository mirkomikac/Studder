package com.studder.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

public class Profile {

    private int id;

    //@SerializedName("name")
    //@Expose
    private String name;

    private String imageUri;

    private Integer age;

    private String location;

    private String profileImageFilename;

    private byte [] profileImgBitmap;

    public Profile(int id, String name, String imageUri, Integer age, String location, byte[] profileImgBitmap) {
        this.id = id;
        this.name = name;
        this.imageUri = imageUri;
        this.age = age;
        this.location = location;
        this.profileImgBitmap = profileImgBitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static String getProfileImageFilename() {

        return "IMG_" + "NAME" + ".jpg";
    }

    public static File getPhotoFile(Context context) {

        File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return (externalFilesDir == null) ? null : new File(externalFilesDir, Profile.getProfileImageFilename());

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getProfileImgBitmap() {
        return profileImgBitmap;
    }
}
