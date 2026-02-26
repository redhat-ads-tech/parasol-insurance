package org.parasol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Entity
@JsonNaming(SnakeCaseStrategy.class)
public class Claim extends PanacheEntity {
    private String claimNumber;
    private String category;
    private String policyNumber;
    @Column(columnDefinition="DATETIME")
    @Temporal(TemporalType.DATE)
    private LocalDate inceptionDate;
    private String clientName;
    private String subject;
    @Column(length = 5000)
    private String body;
    @Column(length = 5000)
    private String summary;
    private String location;
    @Column(name = "claim_time")
    private String time;
    @Column(length = 5000)
    private String sentiment;
    private String emailAddress;
    private String status;

    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public LocalDate getInceptionDate() { return inceptionDate; }
    public void setInceptionDate(LocalDate inceptionDate) { this.inceptionDate = inceptionDate; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
