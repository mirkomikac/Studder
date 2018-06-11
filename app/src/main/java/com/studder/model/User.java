package com.studder.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private static final long serialVersionUID = -5922614948975123925L;

    private Integer id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String description;
    private Date birthday;
    private Boolean onlineStatus;
    private Date lastOnline;
    private Short radius;
    private Double latitude;
    private Double longitude;
    private Date profileCreated;
    private Boolean shareLocation;
    private Boolean isPrivate;
    private Boolean isDeactivated;
    private String userGender;
    private String swipeThrow;
    private String city;

    private Boolean firstTimeLogin;
    private Boolean firstTime;

    private UserMatch mUserMatch;
    private String profileImageEncoded;
    private String userDeviceToken;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public Short getRadius() {
        return radius;
    }

    public void setRadius(Short radius) {
        this.radius = radius;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getProfileCreated() {
        return profileCreated;
    }

    public void setProfileCreated(Date profileCreated) {
        this.profileCreated = profileCreated;
    }

    public Boolean getShareLocation() {
        return shareLocation;
    }

    public void setShareLocation(Boolean shareLocation) {
        this.shareLocation = shareLocation;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Boolean getIsDeactivated() {
        return isDeactivated;
    }

    public void setIsDeactivated(Boolean isDeactivated) {
        this.isDeactivated = isDeactivated;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Boolean getDeactivated() {
        return isDeactivated;
    }

    public void setDeactivated(Boolean deactivated) {
        isDeactivated = deactivated;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getSwipeThrow() {
        return swipeThrow;
    }

    public void setSwipeThrow(String swipeThrow) {
        this.swipeThrow = swipeThrow;
    }

    public UserMatch getmUserMatch() {
        return mUserMatch;
    }

    public void setmUserMatch(UserMatch mUserMatch) {
        this.mUserMatch = mUserMatch;
    }

    public String getProfileImageEncoded() {
        return profileImageEncoded;
    }

    public void setProfileImageEncoded(String profileImageEncoded) {
        this.profileImageEncoded = profileImageEncoded;
    }

    public String getUserDeviceToken() {
        return userDeviceToken;
    }

    public void setUserDeviceToken(String userDeviceToken) {
        this.userDeviceToken = userDeviceToken;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(Boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

    public Boolean getFirstTIme() {
        return firstTime;
    }

    public void setFirstTIme(Boolean firstTIme) {
        this.firstTime = firstTime;
    }
}
