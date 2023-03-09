package com.example.letschat.Models;

import java.util.ArrayList;

public class UserStatus {

    private String userName;
    private String profileImage;
    private String lastUpdated;
    private ArrayList<Status> statusArrayList;



    public UserStatus(String userName, String profileImage, String lastUpdated, ArrayList<Status> statusArrayList) {
        this.userName = userName;
        this.profileImage = profileImage;
        this.lastUpdated = lastUpdated;
        this.statusArrayList = statusArrayList;
    }

    public UserStatus() {
    }

    public ArrayList<Status> getStatusArrayList() {
        return statusArrayList;
    }

    public void setStatusArrayList(ArrayList<Status> statusArrayList) {
        this.statusArrayList = statusArrayList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
