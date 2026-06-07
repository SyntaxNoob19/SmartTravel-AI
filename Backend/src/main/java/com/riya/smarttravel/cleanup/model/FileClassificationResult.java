package com.riya.smarttravel.cleanup.model;

import java.util.List;

/**
 * Represents the classification result for a single file.
 * Contains information about references, imports, exports, and risk assessment.
 */
public class FileClassificationResult {
    private String filePath;
    private FileClassification classification;
    private Integer referenceCount;
    private List<String> referencedBy;
    private List<String> imports;
    private List<String> exports;
    private String reason;
    private RiskLevel riskLevel;
    private Boolean deploymentCritical;
    private Boolean buildCritical;

    public FileClassificationResult() {
    }

    public FileClassificationResult(String filePath, FileClassification classification,
                                    Integer referenceCount, List<String> referencedBy,
                                    List<String> imports, List<String> exports,
                                    String reason, RiskLevel riskLevel,
                                    Boolean deploymentCritical, Boolean buildCritical) {
        this.filePath = filePath;
        this.classification = classification;
        this.referenceCount = referenceCount;
        this.referencedBy = referencedBy;
        this.imports = imports;
        this.exports = exports;
        this.reason = reason;
        this.riskLevel = riskLevel;
        this.deploymentCritical = deploymentCritical;
        this.buildCritical = buildCritical;
    }

    // Getters and Setters
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FileClassification getClassification() {
        return classification;
    }

    public void setClassification(FileClassification classification) {
        this.classification = classification;
    }

    public Integer getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(Integer referenceCount) {
        this.referenceCount = referenceCount;
    }

    public List<String> getReferencedBy() {
        return referencedBy;
    }

    public void setReferencedBy(List<String> referencedBy) {
        this.referencedBy = referencedBy;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public List<String> getExports() {
        return exports;
    }

    public void setExports(List<String> exports) {
        this.exports = exports;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Boolean getDeploymentCritical() {
        return deploymentCritical;
    }

    public void setDeploymentCritical(Boolean deploymentCritical) {
        this.deploymentCritical = deploymentCritical;
    }

    public Boolean getBuildCritical() {
        return buildCritical;
    }

    public void setBuildCritical(Boolean buildCritical) {
        this.buildCritical = buildCritical;
    }

    /**
     * Validates that referenceCount is non-negative
     */
    public boolean isValidReferenceCount() {
        return referenceCount != null && referenceCount >= 0;
    }

    /**
     * Validates that reason is provided for all classifications
     */
    public boolean hasReason() {
        return reason != null && !reason.trim().isEmpty();
    }

    /**
     * Validates that DEPLOYMENT_CRITICAL and BUILD_CRITICAL files have riskLevel = CRITICAL
     */
    public boolean isValidCriticalFileRiskLevel() {
        if (Boolean.TRUE.equals(deploymentCritical) || Boolean.TRUE.equals(buildCritical)) {
            return riskLevel == RiskLevel.CRITICAL;
        }
        return true;
    }
}
