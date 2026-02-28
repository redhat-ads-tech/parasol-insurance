package com.parasol.model;

import java.util.List;

public class Claim {
    public String claimNumber;
    public String category;
    public String clientName;
    public String clientEmail;
    public String clientPhone;
    public String policyNumber;
    public String status;
    public String dateOfEvent;
    public String locationOfEvent;
    public String summary;
    public String estimatedAmount;
    public String adjuster;
    public List<String> images;

    public Claim() {}

    public Claim(String claimNumber, String category, String clientName, String clientEmail,
                 String clientPhone, String policyNumber, String status, String dateOfEvent,
                 String locationOfEvent, String summary, String estimatedAmount, String adjuster,
                 List<String> images) {
        this.claimNumber = claimNumber;
        this.category = category;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.policyNumber = policyNumber;
        this.status = status;
        this.dateOfEvent = dateOfEvent;
        this.locationOfEvent = locationOfEvent;
        this.summary = summary;
        this.estimatedAmount = estimatedAmount;
        this.adjuster = adjuster;
        this.images = images;
    }
}
