package com.riya.smarttravel.cleanup.model;

/**
 * Represents a proposed file operation during cleanup.
 * All operations require approval before execution.
 */
public class FileOperation {
    private OperationType operationType;
    private String targetFile;
    private String destinationPath;
    private String mergeIntoFile;
    private String reason;
    private ImpactAssessment impactAssessment;
    private Boolean requiresApproval;
    private ApprovalStatus approvalStatus;

    public FileOperation() {
    }

    public FileOperation(OperationType operationType, String targetFile,
                        String destinationPath, String mergeIntoFile,
                        String reason, ImpactAssessment impactAssessment,
                        Boolean requiresApproval, ApprovalStatus approvalStatus) {
        this.operationType = operationType;
        this.targetFile = targetFile;
        this.destinationPath = destinationPath;
        this.mergeIntoFile = mergeIntoFile;
        this.reason = reason;
        this.impactAssessment = impactAssessment;
        this.requiresApproval = requiresApproval;
        this.approvalStatus = approvalStatus;
    }

    // Getters and Setters
    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getMergeIntoFile() {
        return mergeIntoFile;
    }

    public void setMergeIntoFile(String mergeIntoFile) {
        this.mergeIntoFile = mergeIntoFile;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ImpactAssessment getImpactAssessment() {
        return impactAssessment;
    }

    public void setImpactAssessment(ImpactAssessment impactAssessment) {
        this.impactAssessment = impactAssessment;
    }

    public Boolean getRequiresApproval() {
        return requiresApproval;
    }

    public void setRequiresApproval(Boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    /**
     * Validates that destinationPath is required for MOVE/RENAME operations
     */
    public boolean isValidDestinationPath() {
        if (operationType == OperationType.MOVE || operationType == OperationType.RENAME) {
            return destinationPath != null && !destinationPath.trim().isEmpty();
        }
        return true;
    }

    /**
     * Validates that mergeIntoFile is required for MERGE operations
     */
    public boolean isValidMergeIntoFile() {
        if (operationType == OperationType.MERGE) {
            return mergeIntoFile != null && !mergeIntoFile.trim().isEmpty();
        }
        return true;
    }

    /**
     * Validates that reason is non-empty
     */
    public boolean hasReason() {
        return reason != null && !reason.trim().isEmpty();
    }

    /**
     * Validates that requiresApproval is true for DELETE/MOVE/RENAME/MERGE
     */
    public boolean hasCorrectApprovalRequirement() {
        if (operationType == OperationType.DELETE || operationType == OperationType.MOVE
            || operationType == OperationType.RENAME || operationType == OperationType.MERGE) {
            return Boolean.TRUE.equals(requiresApproval);
        }
        return true;
    }

    /**
     * ImpactAssessment represents the assessed impact of the file operation
     */
    public static class ImpactAssessment {
        private RiskLevel deploymentRisk;
        private RiskLevel buildRisk;
        private Integer affectedFiles;
        private String impactDescription;

        public ImpactAssessment() {
        }

        public ImpactAssessment(RiskLevel deploymentRisk, RiskLevel buildRisk,
                               Integer affectedFiles, String impactDescription) {
            this.deploymentRisk = deploymentRisk;
            this.buildRisk = buildRisk;
            this.affectedFiles = affectedFiles;
            this.impactDescription = impactDescription;
        }

        public RiskLevel getDeploymentRisk() {
            return deploymentRisk;
        }

        public void setDeploymentRisk(RiskLevel deploymentRisk) {
            this.deploymentRisk = deploymentRisk;
        }

        public RiskLevel getBuildRisk() {
            return buildRisk;
        }

        public void setBuildRisk(RiskLevel buildRisk) {
            this.buildRisk = buildRisk;
        }

        public Integer getAffectedFiles() {
            return affectedFiles;
        }

        public void setAffectedFiles(Integer affectedFiles) {
            this.affectedFiles = affectedFiles;
        }

        public String getImpactDescription() {
            return impactDescription;
        }

        public void setImpactDescription(String impactDescription) {
            this.impactDescription = impactDescription;
        }
    }
}
