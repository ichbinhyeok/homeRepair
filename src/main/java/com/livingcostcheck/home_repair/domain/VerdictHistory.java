package com.livingcostcheck.home_repair.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verdict_history")
@Data
@NoArgsConstructor
public class VerdictHistory {

    @Id
    private UUID id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String budget;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private String decade;

    @Column(name = "verdict_code", nullable = false)
    private String verdictCode;

    @Column(name = "verdict_version", nullable = false)
    private String verdictVersion;

    @Column(name = "decision_context_hash", nullable = false)
    private String decisionContextHash;

    @Column(name = "repair_history", columnDefinition = "TEXT")
    private String repairHistory;

    @Column(name = "house_condition")
    private String houseCondition;

    // Phase 4: Forensic Persistance
    @Column(name = "is_fpe_panel")
    private Boolean isFpePanel;

    @Column(name = "is_poly_b")
    private Boolean isPolyB;

    @Column(name = "is_aluminum")
    private Boolean isAluminum;

    @Column(name = "is_chinese_drywall")
    private Boolean isChineseDrywall;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public VerdictHistory(String zipCode, String budget, String purpose, String decade, String verdictCode,
            String verdictVersion, String decisionContextHash) {
        this.id = UUID.randomUUID();
        this.zipCode = zipCode;
        this.budget = budget;
        this.purpose = purpose;
        this.decade = decade;
        this.verdictCode = verdictCode;
        this.verdictVersion = verdictVersion;
        this.decisionContextHash = decisionContextHash;
    }

    public void setRepairContext(String repairHistory, String houseCondition) {
        this.repairHistory = repairHistory;
        this.houseCondition = houseCondition;
    }

    public void setForensicClues(Boolean isFpePanel, Boolean isPolyB, Boolean isAluminum, Boolean isChineseDrywall) {
        this.isFpePanel = isFpePanel;
        this.isPolyB = isPolyB;
        this.isAluminum = isAluminum;
        this.isChineseDrywall = isChineseDrywall;
    }

}
