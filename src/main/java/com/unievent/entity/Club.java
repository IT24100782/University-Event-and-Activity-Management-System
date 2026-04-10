package com.unievent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category; // e.g., Sports, Cultural, Academic

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "president_email")
    private String presidentEmail;

    private String email; // Official club email

    private String logoUrl;

    private String coverUrl;

    private boolean active = true;

    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    // ── New Registration Fields ──

    @Column(columnDefinition = "TEXT")
    private String plannedActivities; // comma-separated: "Workshops,Hackathons,Seminars"

    private String eventFrequency; // "Weekly", "Bi-Weekly", "Monthly", "Quarterly"

    @Column(columnDefinition = "TEXT")
    private String membershipType; // comma-separated: "Free,Paid - Semester"

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    public Club() {
    }

    public Club(Long id, String name, String category, String description, String presidentEmail, String email, String logoUrl, String coverUrl, boolean active) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.presidentEmail = presidentEmail;
        this.email = email;
        this.logoUrl = logoUrl;
        this.coverUrl = coverUrl;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPresidentEmail() { return presidentEmail; }
    public void setPresidentEmail(String presidentEmail) { this.presidentEmail = presidentEmail; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getPlannedActivities() { return plannedActivities; }
    public void setPlannedActivities(String plannedActivities) { this.plannedActivities = plannedActivities; }

    public String getEventFrequency() { return eventFrequency; }
    public void setEventFrequency(String eventFrequency) { this.eventFrequency = eventFrequency; }

    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }
}
