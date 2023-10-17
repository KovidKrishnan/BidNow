package com.minorproject.bidnow;

import java.util.List;

public class Auction {
    private String auctionId; // Unique identifier for the auction
    private String title;
    private String description;
    private double startingBid;
    private double currentBid;
    private String imageUrl; // Firebase Storage URL for the artifact's image
    private String sellerId; // User ID of the seller
    private String auctionStatus; // Can be "Live," "Upcoming," or "Completed"
    private long startDate; // Start date and time in milliseconds
    private long endDate; // End date and time in milliseconds
    private List<Bid> bids; // List of bids associated with the auction
    private String sellerName;
    private int reports;
    private String winnerId;

    // Constructors (including one for Firebase)
    public Auction() {
        // Default constructor required for Firebase
    }
    public Auction(String auctionId, String title, String description, double startingBid, String imageUrl, String sellerId, String auctionStatus, long startDate, long endDate) {
        this.auctionId = auctionId;
        this.title = title;
        this.description = description;
        this.startingBid = startingBid;
        this.currentBid = startingBid;
        this.imageUrl = imageUrl;
        this.sellerId = sellerId;
        this.auctionStatus = auctionStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStartingBid() {
        return startingBid;
    }

    public void setStartingBid(double startingBid) {
        this.startingBid = startingBid;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(double currentBid) {
        this.currentBid = currentBid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(Bid bid){
        this.bids.add(bid);
    }


    public String getWinnerId() {
        return this.winnerId;
    }
}