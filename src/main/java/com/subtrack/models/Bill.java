package com.subtrack.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Bill {
    private int billId;
    private String serviceType;
    private String referenceNumber;
    private LocalDate billDate;
    private double amount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private boolean isPaid;
    private double overdueChargePerDay;
    private String description;
    private LocalDateTime createdAt;

    public Bill() {}

    // Constructor for new bills (before persistence)
    public Bill(String serviceType, String referenceNumber, LocalDate billDate, double amount, LocalDate dueDate, LocalDate paidDate, boolean is_paid, double overdueChargePerDay, String description) {
        this.serviceType = serviceType;
        this.referenceNumber = referenceNumber;
        this.billDate = billDate;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
        this.overdueChargePerDay = overdueChargePerDay; 
        this.description = description;
        this.isPaid = is_paid;
    }

    // Full constructor (from DB)
    public Bill(int billId, String serviceType, String referenceNumber, LocalDate billDate, double amount, LocalDate dueDate, LocalDate paidDate, boolean isPaid, double overdueChargePerDay, String description, LocalDateTime createdAt) {
        this.billId = billId;
        this.serviceType = serviceType;
        this.referenceNumber = referenceNumber;
        this.billDate = billDate;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
        this.isPaid = isPaid;
        this.overdueChargePerDay = overdueChargePerDay;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public double getOverdueChargePerDay() { return overdueChargePerDay; }
    public void setOverdueChargePerDay(double overdueChargePerDay) { this.overdueChargePerDay = overdueChargePerDay; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}