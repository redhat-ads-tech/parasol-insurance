package com.parasol.model;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;

@Entity
public class Claim extends PanacheEntity {

    @Column(name = "claim_number")
    public String claimNumber;

    public String category;

    @Column(name = "client_name")
    public String clientName;

    @Column(name = "client_email")
    public String clientEmail;

    @Column(name = "client_phone")
    public String clientPhone;

    @Column(name = "policy_number")
    public String policyNumber;

    public String status;

    @Column(name = "date_of_event")
    public String dateOfEvent;

    @Column(name = "location_of_event")
    public String locationOfEvent;

    @Column(length = 4000)
    public String summary;

    @Column(name = "estimated_amount")
    public String estimatedAmount;

    public String adjuster;

    @Convert(converter = StringListConverter.class)
    @Column(length = 1000)
    public List<String> images;
}
