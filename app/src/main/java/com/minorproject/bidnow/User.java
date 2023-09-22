package com.minorproject.bidnow;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId; // Unique identifier for the user
    private String username;
    private String email;
    private String password;
    private Integer creditValue;
    private Integer reportCount;
    private List<String> createdAuctionIds; // List of auction IDs created by the user
    private List<String> participatedAuctionIds; // List of auction IDs in which the user has participated

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.creditValue = 0;
        this.reportCount = 0;
        createdAuctionIds = new ArrayList<>();
        participatedAuctionIds = new ArrayList<>();
    }

    // Getter and Setter methods for userId, username, email, password, creditValue, and reportCount

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(Integer creditValue) {
        this.creditValue = creditValue;
    }

    public Integer getReportCount() {
        return reportCount;
    }
    public void reportUser(){
        this.reportCount++;
    }
    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    // Methods to manage created auction IDs

    public List<String> getCreatedAuctionIds() {
        return createdAuctionIds;
    }

    public void addCreatedAuctionId(String auctionId) {
        createdAuctionIds.add(auctionId);
    }

    public void removeCreatedAuctionId(String auctionId) {
        createdAuctionIds.remove(auctionId);
    }

    // Methods to manage participated auction IDs

    public List<String> getParticipatedAuctionIds() {
        return participatedAuctionIds;
    }

    public void addParticipatedAuctionId(String auctionId) {
        participatedAuctionIds.add(auctionId);
    }

    public void removeParticipatedAuctionId(String auctionId) {
        participatedAuctionIds.remove(auctionId);
    }

    // Other utility methods can be added here based on your application's needs.
}
