package com.subtrack.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Subscription { 
    private int id;
    private String platform;
    private String name;
    private double cost;
    private String billingCycle;
    private int duration; // in months
    private LocalDate nextDueDate;
    private boolean autoPay;
    private LocalDate startDate;
    private boolean isActive;
    private LocalDateTime createdAt;

    public Subscription () {}

    public Subscription(int id,String platform, String name, double cost, String billingCycle, int duration, LocalDate nextDueDate, boolean autoPay, LocalDate startDate, boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.platform = platform;
        this.name = name;
        this.cost = cost;
        this.billingCycle = billingCycle;
        this.duration = duration;
        this.nextDueDate = nextDueDate;
        this.autoPay = autoPay;
        this.startDate = startDate;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // for adding a new subscription
    public Subscription(String platform, String name, double cost,boolean autoPay, String billingCycle, int duration, LocalDate startDate, LocalDate nextDueDate) {
        this.platform = platform;
        this.name = name;
        this.cost = cost;
        this.billingCycle = billingCycle;
        this.duration = duration;
        this.nextDueDate = nextDueDate;
        this.autoPay = autoPay;
        this.startDate = startDate;
    }

    //for updation 
    public Subscription(String platform, String name, double cost,boolean autoPay, String billingCycle, int duration, LocalDate startDate, LocalDate nextDueDate, boolean isActive) {
        this.platform = platform;
        this.name = name;
        this.cost = cost;
        this.billingCycle = billingCycle;
        this.duration = duration;
        this.nextDueDate = nextDueDate;
        this.autoPay = autoPay;
        this.startDate = startDate;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getID() { return id; }
    public void setID(int id) { this.id = id; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public boolean isAutoPay() { return autoPay; }
    public void setAutoPay(boolean autoPay) { this.autoPay = autoPay; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", platform='" + platform + '\'' +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                ", billingCycle='" + billingCycle + '\'' +
                ", duration=" + duration +
                ", nextDueDate=" + nextDueDate +
                ", autoPay=" + autoPay +
                ", startDate=" + startDate +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
