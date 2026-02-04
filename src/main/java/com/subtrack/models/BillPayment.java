package com.subtrack.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BillPayment {
    private int paymentId;
    private String serviceType;
    private int billId;
    private String billReferenceNumber;
    private double billAmount;
    private double totalOverdueCharge;
    private double totalAmount;
    private LocalDate paymentDate;
    private LocalDateTime createdAt;

    public BillPayment() {}

    // Constructor for new payment
    public BillPayment(String serviceType,int billId, String billReferenceNumber, double billAmount, double totalOverdueCharge, double totalAmount, LocalDate paymentDate) {
        this.serviceType = serviceType;
        this.billId = billId;
        this.billReferenceNumber = billReferenceNumber;
        this.billAmount = billAmount;
        this.totalOverdueCharge = totalOverdueCharge;
        this.totalAmount = totalAmount;
        this.paymentDate = paymentDate;
    }

    // Full constructor
    public BillPayment(int paymentId, String serviceType, int billId, String billReferenceNumber, double billAmount, double totalOverdueCharge, double totalAmount, LocalDate paymentDate) {
        this.paymentId = paymentId;
        this.serviceType = serviceType; 
        this.billId = billId;
        this.billReferenceNumber = billReferenceNumber;
        this.billAmount = billAmount;
        this.totalOverdueCharge = totalOverdueCharge;
        this.totalAmount = totalAmount;
        this.paymentDate = paymentDate;
    }

    // Getters & setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public String getBillReferenceNumber() { return billReferenceNumber; }
    public void setBillReferenceNumber(String billReferenceNumber) { this.billReferenceNumber = billReferenceNumber; }

    public double getBillAmount() { return billAmount; }
    public void setBillAmount(double billAmount) { this.billAmount = billAmount; }

    public double getTotalOverdueCharge() { return totalOverdueCharge; }
    public void setTotalOverdueCharge(double totalOverdueCharge) { this.totalOverdueCharge = totalOverdueCharge; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}