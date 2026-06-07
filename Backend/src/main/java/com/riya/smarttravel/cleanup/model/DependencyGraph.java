package com.riya.smarttravel.cleanup.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the dependency graph for a set of files.
 * Used for dependency analysis and circular dependency detection.
 */
public class DependencyGraph {
    private List<DependencyNode> nodes;
    private List<DependencyEdge> edges;
    private List<CircularDependency> circularDependencies;
    private List<DependencyNode> rootNodes;
    private List<DependencyNode> leafNodes;

    public DependencyGraph() {
    }

    public DependencyGraph(List<DependencyNode> nodes, List<DependencyEdge> edges,
                          List<CircularDependency> circularDependencies,
                          List<DependencyNode> rootNodes, List<DependencyNode> leafNodes) {
        this.nodes = nodes;
        this.edges = edges;
        this.circularDependencies = circularDependencies;
        this.rootNodes = rootNodes;
        this.leafNodes = leafNodes;
    }

    // Getters and Setters
    public List<DependencyNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<DependencyNode> nodes) {
        this.nodes = nodes;
    }

    public List<DependencyEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<DependencyEdge> edges) {
        this.edges = edges;
    }

    public List<CircularDependency> getCircularDependencies() {
        return circularDependencies;
    }

    public void setCircularDependencies(List<CircularDependency> circularDependencies) {
        this.circularDependencies = circularDependencies;
    }

    public List<DependencyNode> getRootNodes() {
        return rootNodes;
    }

    public void setRootNodes(List<DependencyNode> rootNodes) {
        this.rootNodes = rootNodes;
    }

    public List<DependencyNode> getLeafNodes() {
        return leafNodes;
    }

    public void setLeafNodes(List<DependencyNode> leafNodes) {
        this.leafNodes = leafNodes;
    }

    /**
     * Validates that all edge sources and targets exist in nodes list
     */
    public boolean areEdgesValid() {
        if (nodes == null || edges == null) {
            return false;
        }
        
        List<String> nodeFilePaths = nodes.stream()
            .map(DependencyNode::getFilePath)
            .collect(Collectors.toList());
        
        return edges.stream().allMatch(edge ->
            nodeFilePaths.contains(edge.getSource()) && nodeFilePaths.contains(edge.getTarget())
        );
    }

    /**
     * Validates that circular dependencies reference valid nodes
     */
    public boolean areCircularDependenciesValid() {
        if (nodes == null || circularDependencies == null) {
            return false;
        }
        
        List<String> nodeFilePaths = nodes.stream()
            .map(DependencyNode::getFilePath)
            .collect(Collectors.toList());
        
        return circularDependencies.stream().allMatch(cd ->
            cd.getInvolvedFiles().stream().allMatch(nodeFilePaths::contains)
        );
    }

    /**
     * DependencyNode represents a file in the dependency graph
     */
    public static class DependencyNode {
        private String filePath;
        private String fileType;
        private List<String> dependsOn;
        private List<String> dependedBy;

        public DependencyNode() {
        }

        public DependencyNode(String filePath, String fileType, List<String> dependsOn, List<String> dependedBy) {
            this.filePath = filePath;
            this.fileType = fileType;
            this.dependsOn = dependsOn;
            this.dependedBy = dependedBy;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public List<String> getDependsOn() {
            return dependsOn;
        }

        public void setDependsOn(List<String> dependsOn) {
            this.dependsOn = dependsOn;
        }

        public List<String> getDependedBy() {
            return dependedBy;
        }

        public void setDependedBy(List<String> dependedBy) {
            this.dependedBy = dependedBy;
        }
    }

    /**
     * DependencyEdge represents a dependency relationship between two files
     */
    public static class DependencyEdge {
        private String source;
        private String target;
        private DependencyType edgeType;

        public DependencyEdge() {
        }

        public DependencyEdge(String source, String target, DependencyType edgeType) {
            this.source = source;
            this.target = target;
            this.edgeType = edgeType;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public DependencyType getEdgeType() {
            return edgeType;
        }

        public void setEdgeType(DependencyType edgeType) {
            this.edgeType = edgeType;
        }
    }

    /**
     * CircularDependency represents a circular dependency cycle
     */
    public static class CircularDependency {
        private List<String> involvedFiles;
        private String description;

        public CircularDependency() {
        }

        public CircularDependency(List<String> involvedFiles, String description) {
            this.involvedFiles = involvedFiles;
            this.description = description;
        }

        public List<String> getInvolvedFiles() {
            return involvedFiles;
        }

        public void setInvolvedFiles(List<String> involvedFiles) {
            this.involvedFiles = involvedFiles;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
