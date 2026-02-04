package com.subtrack.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Payment {
    private int id;
    private String platformName;
    private String subscriptionName;
    private boolean isAutoPaid;
    private double amount;
    private LocalDate paymentDate;
    private boolean  renewed;
    private LocalDateTime createdAt;

    // Empty constructor
    public Payment() { }

    // Full constructor (for reading from DB)
    public Payment(int id,
                   String platformName,
                   String subscriptionName,
                   boolean isAutoPaid,
                   double amount,
                   LocalDate paymentDate,
                   boolean renewed,
                   LocalDateTime createdAt) {
        this.id = id;
        this.platformName = platformName;
        this.subscriptionName = subscriptionName;
        this.isAutoPaid = isAutoPaid;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.renewed = renewed;
        this.createdAt = createdAt;
    }

    // Constructor for new auto generated Payment
    public Payment(String platformName,
                   String subscriptionName,
                   boolean isAutoPaid,
                   double amount,
                   LocalDate paymentDate,
                   boolean renewed) {
        this.platformName = platformName;
        this.subscriptionName = subscriptionName;
        this.isAutoPaid = isAutoPaid;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.renewed = renewed;
    }
    // Constructor for new manually generated Payment
    public Payment(String platformName,
                   String subscriptionName,
                   double amount,
                   LocalDate paymentDate,
                   boolean renewed) {
        this.platformName = platformName;
        this.subscriptionName = subscriptionName;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.renewed = renewed;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public boolean isAutoPaid() {
        return isAutoPaid;
    }
    
    public void setAutoPaid(boolean autoPaid) {
        this.isAutoPaid = autoPaid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public boolean getRenewed() {
        return renewed;
    }

    public void setRenewed(boolean renewed) {
        this.renewed = renewed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", platformName='" + platformName + '\'' +
                ", subscriptionName='" + subscriptionName + '\'' +
                ", isAutoPaid=" + isAutoPaid +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
