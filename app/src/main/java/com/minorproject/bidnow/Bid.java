package com.minorproject.bidnow;

public class Bid {
    private String bidId; // Unique identifier for the bid
    private String bidderId; // User ID of the bidder
    private double bidAmount; // Amount of the bid
    private long bidTimestamp; // Timestamp when the bid was made (in milliseconds)
    private String auctionId;

    // Constructors (including one for Firebase)
    public Bid() {
        // Default constructor required for Firebase
    }

    public Bid(String bidId, String bidderId, double bidAmount, long bidTimestamp, String auctionId) {
        this.bidId = bidId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.bidTimestamp = bidTimestamp;
        this.auctionId = auctionId;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    // Getter and Setter methods
    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getBidderId() {
        return bidderId;
    }

    public void setBidderId(String bidderId) {
        this.bidderId = bidderId;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public long getBidTimestamp() {
        return bidTimestamp;
    }

    public void setBidTimestamp(long bidTimestamp) {
        this.bidTimestamp = bidTimestamp;
    }

    // Other utility methods can be added here based on your application's needs.
}
