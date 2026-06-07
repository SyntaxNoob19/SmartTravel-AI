package com.riya.smarttravel.cleanup.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents the result of executing a cleanup phase.
 * Contains analysis results, generated documentation, and phase status.
 */
public class PhaseResult {
    private Integer phaseNumber;
    private String phaseName;
    private PhaseStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AnalysisReport analysisReport;
    private List<String> documentationGenerated;
    private List<String> diagramsGenerated;
    private Integer filesAnalyzed;
    private Map<FileClassification, Integer> filesClassified;
    private Boolean approvalRequired;
    private ApprovalStatus approvalStatus;

    public PhaseResult() {
    }

    public PhaseResult(Integer phaseNumber, String phaseName, PhaseStatus status,
                       LocalDateTime startTime, LocalDateTime endTime,
                       AnalysisReport analysisReport, List<String> documentationGenerated,
                       List<String> diagramsGenerated, Integer filesAnalyzed,
                       Map<FileClassification, Integer> filesClassified,
                       Boolean approvalRequired, ApprovalStatus approvalStatus) {
        this.phaseNumber = phaseNumber;
        this.phaseName = phaseName;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.analysisReport = analysisReport;
        this.documentationGenerated = documentationGenerated;
        this.diagramsGenerated = diagramsGenerated;
        this.filesAnalyzed = filesAnalyzed;
        this.filesClassified = filesClassified;
        this.approvalRequired = approvalRequired;
        this.approvalStatus = approvalStatus;
    }

    // Getters and Setters
    public Integer getPhaseNumber() {
        return phaseNumber;
    }

    public void setPhaseNumber(Integer phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public PhaseStatus getStatus() {
        return status;
    }

    public void setStatus(PhaseStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AnalysisReport getAnalysisReport() {
        return analysisReport;
    }

    public void setAnalysisReport(AnalysisReport analysisReport) {
        this.analysisReport = analysisReport;
    }

    public List<String> getDocumentationGenerated() {
        return documentationGenerated;
    }

    public void setDocumentationGenerated(List<String> documentationGenerated) {
        this.documentationGenerated = documentationGenerated;
    }

    public List<String> getDiagramsGenerated() {
        return diagramsGenerated;
    }

    public void setDiagramsGenerated(List<String> diagramsGenerated) {
        this.diagramsGenerated = diagramsGenerated;
    }

    public Integer getFilesAnalyzed() {
        return filesAnalyzed;
    }

    public void setFilesAnalyzed(Integer filesAnalyzed) {
        this.filesAnalyzed = filesAnalyzed;
    }

    public Map<FileClassification, Integer> getFilesClassified() {
        return filesClassified;
    }

    public void setFilesClassified(Map<FileClassification, Integer> filesClassified) {
        this.filesClassified = filesClassified;
    }

    public Boolean getApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(Boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    /**
     * Validates that phaseNumber is between 1 and 6
     */
    public boolean isValidPhaseNumber() {
        return phaseNumber != null && phaseNumber >= 1 && phaseNumber <= 6;
    }

    /**
     * Validates that endTime is after startTime
     */
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) {
            return false;
        }
        return endTime.isAfter(startTime);
    }

    /**
     * Validates that filesAnalyzed is non-negative
     */
    public boolean isValidFilesAnalyzed() {
        return filesAnalyzed != null && filesAnalyzed >= 0;
    }
}
