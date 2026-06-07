package com.riya.smarttravel.cleanup.model;

import java.util.List;

/**
 * Comprehensive analysis report for a cleanup phase.
 * Contains results from structure, dependency, reference, duplicate, and dead code analysis.
 */
public class AnalysisReport {
    private Integer phaseNumber;
    private String targetFolder;
    private StructureAnalysisResult structureAnalysis;
    private DependencyAnalysisResult dependencyAnalysis;
    private ReferenceAnalysisResult referenceAnalysis;
    private DuplicateDetectionResult duplicateDetection;
    private DeadCodeDetectionResult deadCodeDetection;
    private DeploymentImpactResult deploymentImpactAssessment;
    private BuildImpactResult buildImpactAssessment;
    private RiskAssessmentResult riskAssessment;
    private List<Recommendation> recommendations;

    public AnalysisReport() {
    }

    public AnalysisReport(Integer phaseNumber, String targetFolder,
                          StructureAnalysisResult structureAnalysis,
                          DependencyAnalysisResult dependencyAnalysis,
                          ReferenceAnalysisResult referenceAnalysis,
                          DuplicateDetectionResult duplicateDetection,
                          DeadCodeDetectionResult deadCodeDetection,
                          DeploymentImpactResult deploymentImpactAssessment,
                          BuildImpactResult buildImpactAssessment,
                          RiskAssessmentResult riskAssessment,
                          List<Recommendation> recommendations) {
        this.phaseNumber = phaseNumber;
        this.targetFolder = targetFolder;
        this.structureAnalysis = structureAnalysis;
        this.dependencyAnalysis = dependencyAnalysis;
        this.referenceAnalysis = referenceAnalysis;
        this.duplicateDetection = duplicateDetection;
        this.deadCodeDetection = deadCodeDetection;
        this.deploymentImpactAssessment = deploymentImpactAssessment;
        this.buildImpactAssessment = buildImpactAssessment;
        this.riskAssessment = riskAssessment;
        this.recommendations = recommendations;
    }

    // Getters and Setters
    public Integer getPhaseNumber() {
        return phaseNumber;
    }

    public void setPhaseNumber(Integer phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    public String getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
    }

    public StructureAnalysisResult getStructureAnalysis() {
        return structureAnalysis;
    }

    public void setStructureAnalysis(StructureAnalysisResult structureAnalysis) {
        this.structureAnalysis = structureAnalysis;
    }

    public DependencyAnalysisResult getDependencyAnalysis() {
        return dependencyAnalysis;
    }

    public void setDependencyAnalysis(DependencyAnalysisResult dependencyAnalysis) {
        this.dependencyAnalysis = dependencyAnalysis;
    }

    public ReferenceAnalysisResult getReferenceAnalysis() {
        return referenceAnalysis;
    }

    public void setReferenceAnalysis(ReferenceAnalysisResult referenceAnalysis) {
        this.referenceAnalysis = referenceAnalysis;
    }

    public DuplicateDetectionResult getDuplicateDetection() {
        return duplicateDetection;
    }

    public void setDuplicateDetection(DuplicateDetectionResult duplicateDetection) {
        this.duplicateDetection = duplicateDetection;
    }

    public DeadCodeDetectionResult getDeadCodeDetection() {
        return deadCodeDetection;
    }

    public void setDeadCodeDetection(DeadCodeDetectionResult deadCodeDetection) {
        this.deadCodeDetection = deadCodeDetection;
    }

    public DeploymentImpactResult getDeploymentImpactAssessment() {
        return deploymentImpactAssessment;
    }

    public void setDeploymentImpactAssessment(DeploymentImpactResult deploymentImpactAssessment) {
        this.deploymentImpactAssessment = deploymentImpactAssessment;
    }

    public BuildImpactResult getBuildImpactAssessment() {
        return buildImpactAssessment;
    }

    public void setBuildImpactAssessment(BuildImpactResult buildImpactAssessment) {
        this.buildImpactAssessment = buildImpactAssessment;
    }

    public RiskAssessmentResult getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(RiskAssessmentResult riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    /**
     * Validates that all analysis results are non-null
     */
    public boolean areAllAnalysisResultsValid() {
        return structureAnalysis != null
                && dependencyAnalysis != null
                && referenceAnalysis != null
                && duplicateDetection != null
                && deadCodeDetection != null
                && deploymentImpactAssessment != null
                && buildImpactAssessment != null
                && riskAssessment != null;
    }

    /**
     * Validates that recommendations list is not null (can be empty)
     */
    public boolean areRecommendationsValid() {
        return recommendations != null;
    }

    // Nested classes for analysis results
    public static class StructureAnalysisResult {
        private Integer totalFiles;
        private Integer totalFolders;
        private List<String> namingIssues;
        private List<String> organizationIssues;

        public StructureAnalysisResult() {
        }

        public StructureAnalysisResult(Integer totalFiles, Integer totalFolders,
                                       List<String> namingIssues, List<String> organizationIssues) {
            this.totalFiles = totalFiles;
            this.totalFolders = totalFolders;
            this.namingIssues = namingIssues;
            this.organizationIssues = organizationIssues;
        }

        public Integer getTotalFiles() {
            return totalFiles;
        }

        public void setTotalFiles(Integer totalFiles) {
            this.totalFiles = totalFiles;
        }

        public Integer getTotalFolders() {
            return totalFolders;
        }

        public void setTotalFolders(Integer totalFolders) {
            this.totalFolders = totalFolders;
        }

        public List<String> getNamingIssues() {
            return namingIssues;
        }

        public void setNamingIssues(List<String> namingIssues) {
            this.namingIssues = namingIssues;
        }

        public List<String> getOrganizationIssues() {
            return organizationIssues;
        }

        public void setOrganizationIssues(List<String> organizationIssues) {
            this.organizationIssues = organizationIssues;
        }
    }

    public static class DependencyAnalysisResult {
        private Integer totalDependencies;
        private List<String> circularDependencies;
        private List<String> unusedDependencies;

        public DependencyAnalysisResult() {
        }

        public DependencyAnalysisResult(Integer totalDependencies, List<String> circularDependencies,
                                        List<String> unusedDependencies) {
            this.totalDependencies = totalDependencies;
            this.circularDependencies = circularDependencies;
            this.unusedDependencies = unusedDependencies;
        }

        public Integer getTotalDependencies() {
            return totalDependencies;
        }

        public void setTotalDependencies(Integer totalDependencies) {
            this.totalDependencies = totalDependencies;
        }

        public List<String> getCircularDependencies() {
            return circularDependencies;
        }

        public void setCircularDependencies(List<String> circularDependencies) {
            this.circularDependencies = circularDependencies;
        }

        public List<String> getUnusedDependencies() {
            return unusedDependencies;
        }

        public void setUnusedDependencies(List<String> unusedDependencies) {
            this.unusedDependencies = unusedDependencies;
        }
    }

    public static class ReferenceAnalysisResult {
        private Integer totalReferences;
        private Integer filesWithNoReferences;

        public ReferenceAnalysisResult() {
        }

        public ReferenceAnalysisResult(Integer totalReferences, Integer filesWithNoReferences) {
            this.totalReferences = totalReferences;
            this.filesWithNoReferences = filesWithNoReferences;
        }

        public Integer getTotalReferences() {
            return totalReferences;
        }

        public void setTotalReferences(Integer totalReferences) {
            this.totalReferences = totalReferences;
        }

        public Integer getFilesWithNoReferences() {
            return filesWithNoReferences;
        }

        public void setFilesWithNoReferences(Integer filesWithNoReferences) {
            this.filesWithNoReferences = filesWithNoReferences;
        }
    }

    public static class DuplicateDetectionResult {
        private Integer duplicateFilePairs;
        private Integer totalDuplicateFiles;

        public DuplicateDetectionResult() {
        }

        public DuplicateDetectionResult(Integer duplicateFilePairs, Integer totalDuplicateFiles) {
            this.duplicateFilePairs = duplicateFilePairs;
            this.totalDuplicateFiles = totalDuplicateFiles;
        }

        public Integer getDuplicateFilePairs() {
            return duplicateFilePairs;
        }

        public void setDuplicateFilePairs(Integer duplicateFilePairs) {
            this.duplicateFilePairs = duplicateFilePairs;
        }

        public Integer getTotalDuplicateFiles() {
            return totalDuplicateFiles;
        }

        public void setTotalDuplicateFiles(Integer totalDuplicateFiles) {
            this.totalDuplicateFiles = totalDuplicateFiles;
        }
    }

    public static class DeadCodeDetectionResult {
        private Integer unusedFiles;
        private Integer unusedFunctions;
        private Integer unusedAssets;

        public DeadCodeDetectionResult() {
        }

        public DeadCodeDetectionResult(Integer unusedFiles, Integer unusedFunctions, Integer unusedAssets) {
            this.unusedFiles = unusedFiles;
            this.unusedFunctions = unusedFunctions;
            this.unusedAssets = unusedAssets;
        }

        public Integer getUnusedFiles() {
            return unusedFiles;
        }

        public void setUnusedFiles(Integer unusedFiles) {
            this.unusedFiles = unusedFiles;
        }

        public Integer getUnusedFunctions() {
            return unusedFunctions;
        }

        public void setUnusedFunctions(Integer unusedFunctions) {
            this.unusedFunctions = unusedFunctions;
        }

        public Integer getUnusedAssets() {
            return unusedAssets;
        }

        public void setUnusedAssets(Integer unusedAssets) {
            this.unusedAssets = unusedAssets;
        }
    }

    public static class DeploymentImpactResult {
        private RiskLevel riskLevel;
        private String impactDescription;

        public DeploymentImpactResult() {
        }

        public DeploymentImpactResult(RiskLevel riskLevel, String impactDescription) {
            this.riskLevel = riskLevel;
            this.impactDescription = impactDescription;
        }

        public RiskLevel getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
        }

        public String getImpactDescription() {
            return impactDescription;
        }

        public void setImpactDescription(String impactDescription) {
            this.impactDescription = impactDescription;
        }
    }

    public static class BuildImpactResult {
        private RiskLevel riskLevel;
        private String impactDescription;

        public BuildImpactResult() {
        }

        public BuildImpactResult(RiskLevel riskLevel, String impactDescription) {
            this.riskLevel = riskLevel;
            this.impactDescription = impactDescription;
        }

        public RiskLevel getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
        }

        public String getImpactDescription() {
            return impactDescription;
        }

        public void setImpactDescription(String impactDescription) {
            this.impactDescription = impactDescription;
        }
    }

    public static class RiskAssessmentResult {
        private RiskLevel overallRisk;
        private String riskDescription;
        private List<String> mitigationSteps;

        public RiskAssessmentResult() {
        }

        public RiskAssessmentResult(RiskLevel overallRisk, String riskDescription, List<String> mitigationSteps) {
            this.overallRisk = overallRisk;
            this.riskDescription = riskDescription;
            this.mitigationSteps = mitigationSteps;
        }

        public RiskLevel getOverallRisk() {
            return overallRisk;
        }

        public void setOverallRisk(RiskLevel overallRisk) {
            this.overallRisk = overallRisk;
        }

        public String getRiskDescription() {
            return riskDescription;
        }

        public void setRiskDescription(String riskDescription) {
            this.riskDescription = riskDescription;
        }

        public List<String> getMitigationSteps() {
            return mitigationSteps;
        }

        public void setMitigationSteps(List<String> mitigationSteps) {
            this.mitigationSteps = mitigationSteps;
        }
    }

    public static class Recommendation {
        private String action;
        private String targetFile;
        private String reason;
        private RiskLevel riskLevel;

        public Recommendation() {
        }

        public Recommendation(String action, String targetFile, String reason, RiskLevel riskLevel) {
            this.action = action;
            this.targetFile = targetFile;
            this.reason = reason;
            this.riskLevel = riskLevel;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getTargetFile() {
            return targetFile;
        }

        public void setTargetFile(String targetFile) {
            this.targetFile = targetFile;
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
    }
}
