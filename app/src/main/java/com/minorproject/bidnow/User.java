package com.minorproject.bidnow;

public class User {
    private String userId; // Unique identifier for the user
    private String username;
    private String email;
    private String password;
    private String phone;
    private Integer creditValue;
    private Integer biddedAuctionsCount;

    public User(String username, String email, String password, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.creditValue = 0;
        this.biddedAuctionsCount = 0;
        this.phone = phone;
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



    // Other utility methods can be added here based on your application's needs.
}
