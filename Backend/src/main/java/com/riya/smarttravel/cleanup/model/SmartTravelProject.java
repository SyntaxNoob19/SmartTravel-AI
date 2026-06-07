package com.riya.smarttravel.cleanup.model;

import java.util.List;

/**
 * Represents the complete SmartTravel project structure and metadata.
 * Contains references to frontend, backend, database, and deployment configurations.
 */
public class SmartTravelProject {
    private String projectRoot;
    private Frontend frontend;
    private Backend backend;
    private Database database;
    private Documentation documentation;
    private DeploymentConfig deployment;
    private BuildConfig buildConfig;

    public SmartTravelProject() {
    }

    public SmartTravelProject(String projectRoot, Frontend frontend, Backend backend,
                              Database database, Documentation documentation,
                              DeploymentConfig deployment, BuildConfig buildConfig) {
        this.projectRoot = projectRoot;
        this.frontend = frontend;
        this.backend = backend;
        this.database = database;
        this.documentation = documentation;
        this.deployment = deployment;
        this.buildConfig = buildConfig;
    }

    // Getters and Setters
    public String getProjectRoot() {
        return projectRoot;
    }

    public void setProjectRoot(String projectRoot) {
        this.projectRoot = projectRoot;
    }

    public Frontend getFrontend() {
        return frontend;
    }

    public void setFrontend(Frontend frontend) {
        this.frontend = frontend;
    }

    public Backend getBackend() {
        return backend;
    }

    public void setBackend(Backend backend) {
        this.backend = backend;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Documentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(Documentation documentation) {
        this.documentation = documentation;
    }

    public DeploymentConfig getDeployment() {
        return deployment;
    }

    public void setDeployment(DeploymentConfig deployment) {
        this.deployment = deployment;
    }

    public BuildConfig getBuildConfig() {
        return buildConfig;
    }

    public void setBuildConfig(BuildConfig buildConfig) {
        this.buildConfig = buildConfig;
    }

    /**
     * Validates that projectRoot exists and is non-null
     */
    public boolean isValidProjectRoot() {
        return projectRoot != null && !projectRoot.trim().isEmpty();
    }

    /**
     * Represents the frontend component of SmartTravel
     */
    public static class Frontend {
        private String rootPath;
        private String indexHtml;
        private String assetsFolder;
        private String componentsFolder;
        private String cssFolder;
        private String jsFolder;
        private String pagesFolder;
        private List<Route> routes;

        public Frontend() {
        }

        public Frontend(String rootPath, String indexHtml, String assetsFolder,
                       String componentsFolder, String cssFolder, String jsFolder,
                       String pagesFolder, List<Route> routes) {
            this.rootPath = rootPath;
            this.indexHtml = indexHtml;
            this.assetsFolder = assetsFolder;
            this.componentsFolder = componentsFolder;
            this.cssFolder = cssFolder;
            this.jsFolder = jsFolder;
            this.pagesFolder = pagesFolder;
            this.routes = routes;
        }

        public String getRootPath() {
            return rootPath;
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }

        public String getIndexHtml() {
            return indexHtml;
        }

        public void setIndexHtml(String indexHtml) {
            this.indexHtml = indexHtml;
        }

        public String getAssetsFolder() {
            return assetsFolder;
        }

        public void setAssetsFolder(String assetsFolder) {
            this.assetsFolder = assetsFolder;
        }

        public String getComponentsFolder() {
            return componentsFolder;
        }

        public void setComponentsFolder(String componentsFolder) {
            this.componentsFolder = componentsFolder;
        }

        public String getCssFolder() {
            return cssFolder;
        }

        public void setCssFolder(String cssFolder) {
            this.cssFolder = cssFolder;
        }

        public String getJsFolder() {
            return jsFolder;
        }

        public void setJsFolder(String jsFolder) {
            this.jsFolder = jsFolder;
        }

        public String getPagesFolder() {
            return pagesFolder;
        }

        public void setPagesFolder(String pagesFolder) {
            this.pagesFolder = pagesFolder;
        }

        public List<Route> getRoutes() {
            return routes;
        }

        public void setRoutes(List<Route> routes) {
            this.routes = routes;
        }
    }

    /**
     * Represents a frontend route
     */
    public static class Route {
        private String path;
        private String handler;
        private String description;

        public Route() {
        }

        public Route(String path, String handler, String description) {
            this.path = path;
            this.handler = handler;
            this.description = description;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getHandler() {
            return handler;
        }

        public void setHandler(String handler) {
            this.handler = handler;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * Represents the backend component of SmartTravel
     */
    public static class Backend {
        private String rootPath;
        private String sourceRoot;
        private String configFolder;
        private List<String> controllerClasses;
        private List<String> serviceClasses;
        private List<String> repositoryClasses;
        private List<String> entityClasses;
        private List<String> dtoClasses;
        private String securityConfig;
        private String utilitiesFolder;
        private String testsFolder;

        public Backend() {
        }

        public Backend(String rootPath, String sourceRoot, String configFolder,
                      List<String> controllerClasses, List<String> serviceClasses,
                      List<String> repositoryClasses, List<String> entityClasses,
                      List<String> dtoClasses, String securityConfig,
                      String utilitiesFolder, String testsFolder) {
            this.rootPath = rootPath;
            this.sourceRoot = sourceRoot;
            this.configFolder = configFolder;
            this.controllerClasses = controllerClasses;
            this.serviceClasses = serviceClasses;
            this.repositoryClasses = repositoryClasses;
            this.entityClasses = entityClasses;
            this.dtoClasses = dtoClasses;
            this.securityConfig = securityConfig;
            this.utilitiesFolder = utilitiesFolder;
            this.testsFolder = testsFolder;
        }

        public String getRootPath() {
            return rootPath;
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }

        public String getSourceRoot() {
            return sourceRoot;
        }

        public void setSourceRoot(String sourceRoot) {
            this.sourceRoot = sourceRoot;
        }

        public String getConfigFolder() {
            return configFolder;
        }

        public void setConfigFolder(String configFolder) {
            this.configFolder = configFolder;
        }

        public List<String> getControllerClasses() {
            return controllerClasses;
        }

        public void setControllerClasses(List<String> controllerClasses) {
            this.controllerClasses = controllerClasses;
        }

        public List<String> getServiceClasses() {
            return serviceClasses;
        }

        public void setServiceClasses(List<String> serviceClasses) {
            this.serviceClasses = serviceClasses;
        }

        public List<String> getRepositoryClasses() {
            return repositoryClasses;
        }

        public void setRepositoryClasses(List<String> repositoryClasses) {
            this.repositoryClasses = repositoryClasses;
        }

        public List<String> getEntityClasses() {
            return entityClasses;
        }

        public void setEntityClasses(List<String> entityClasses) {
            this.entityClasses = entityClasses;
        }

        public List<String> getDtoClasses() {
            return dtoClasses;
        }

        public void setDtoClasses(List<String> dtoClasses) {
            this.dtoClasses = dtoClasses;
        }

        public String getSecurityConfig() {
            return securityConfig;
        }

        public void setSecurityConfig(String securityConfig) {
            this.securityConfig = securityConfig;
        }

        public String getUtilitiesFolder() {
            return utilitiesFolder;
        }

        public void setUtilitiesFolder(String utilitiesFolder) {
            this.utilitiesFolder = utilitiesFolder;
        }

        public String getTestsFolder() {
            return testsFolder;
        }

        public void setTestsFolder(String testsFolder) {
            this.testsFolder = testsFolder;
        }
    }

    /**
     * Represents the database configuration of SmartTravel
     */
    public static class Database {
        private String databaseType;
        private String url;
        private String username;
        private String schema;
        private List<String> entityMappings;
        private List<String> relationships;

        public Database() {
        }

        public Database(String databaseType, String url, String username, String schema,
                       List<String> entityMappings, List<String> relationships) {
            this.databaseType = databaseType;
            this.url = url;
            this.username = username;
            this.schema = schema;
            this.entityMappings = entityMappings;
            this.relationships = relationships;
        }

        public String getDatabaseType() {
            return databaseType;
        }

        public void setDatabaseType(String databaseType) {
            this.databaseType = databaseType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public List<String> getEntityMappings() {
            return entityMappings;
        }

        public void setEntityMappings(List<String> entityMappings) {
            this.entityMappings = entityMappings;
        }

        public List<String> getRelationships() {
            return relationships;
        }

        public void setRelationships(List<String> relationships) {
            this.relationships = relationships;
        }
    }

    /**
     * Represents the documentation of SmartTravel project
     */
    public static class Documentation {
        private String rootPath;
        private List<String> generatedDocuments;
        private List<String> generatedDiagrams;
        private String lastUpdated;

        public Documentation() {
        }

        public Documentation(String rootPath, List<String> generatedDocuments,
                            List<String> generatedDiagrams, String lastUpdated) {
            this.rootPath = rootPath;
            this.generatedDocuments = generatedDocuments;
            this.generatedDiagrams = generatedDiagrams;
            this.lastUpdated = lastUpdated;
        }

        public String getRootPath() {
            return rootPath;
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }

        public List<String> getGeneratedDocuments() {
            return generatedDocuments;
        }

        public void setGeneratedDocuments(List<String> generatedDocuments) {
            this.generatedDocuments = generatedDocuments;
        }

        public List<String> getGeneratedDiagrams() {
            return generatedDiagrams;
        }

        public void setGeneratedDiagrams(List<String> generatedDiagrams) {
            this.generatedDiagrams = generatedDiagrams;
        }

        public String getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
        }
    }

    /**
     * Represents the deployment configuration of SmartTravel
     */
    public static class DeploymentConfig {
        private String deploymentType;
        private String targetEnvironment;
        private String deploymentScript;
        private List<String> preDeploymentChecks;
        private List<String> postDeploymentValidation;

        public DeploymentConfig() {
        }

        public DeploymentConfig(String deploymentType, String targetEnvironment,
                               String deploymentScript, List<String> preDeploymentChecks,
                               List<String> postDeploymentValidation) {
            this.deploymentType = deploymentType;
            this.targetEnvironment = targetEnvironment;
            this.deploymentScript = deploymentScript;
            this.preDeploymentChecks = preDeploymentChecks;
            this.postDeploymentValidation = postDeploymentValidation;
        }

        public String getDeploymentType() {
            return deploymentType;
        }

        public void setDeploymentType(String deploymentType) {
            this.deploymentType = deploymentType;
        }

        public String getTargetEnvironment() {
            return targetEnvironment;
        }

        public void setTargetEnvironment(String targetEnvironment) {
            this.targetEnvironment = targetEnvironment;
        }

        public String getDeploymentScript() {
            return deploymentScript;
        }

        public void setDeploymentScript(String deploymentScript) {
            this.deploymentScript = deploymentScript;
        }

        public List<String> getPreDeploymentChecks() {
            return preDeploymentChecks;
        }

        public void setPreDeploymentChecks(List<String> preDeploymentChecks) {
            this.preDeploymentChecks = preDeploymentChecks;
        }

        public List<String> getPostDeploymentValidation() {
            return postDeploymentValidation;
        }

        public void setPostDeploymentValidation(List<String> postDeploymentValidation) {
            this.postDeploymentValidation = postDeploymentValidation;
        }
    }

    /**
     * Represents the build configuration of SmartTravel
     */
    public static class BuildConfig {
        private String buildTool;
        private String buildScript;
        private List<String> requiredDependencies;
        private List<String> excludedFiles;
        private String outputDirectory;

        public BuildConfig() {
        }

        public BuildConfig(String buildTool, String buildScript,
                          List<String> requiredDependencies, List<String> excludedFiles,
                          String outputDirectory) {
            this.buildTool = buildTool;
            this.buildScript = buildScript;
            this.requiredDependencies = requiredDependencies;
            this.excludedFiles = excludedFiles;
            this.outputDirectory = outputDirectory;
        }

        public String getBuildTool() {
            return buildTool;
        }

        public void setBuildTool(String buildTool) {
            this.buildTool = buildTool;
        }

        public String getBuildScript() {
            return buildScript;
        }

        public void setBuildScript(String buildScript) {
            this.buildScript = buildScript;
        }

        public List<String> getRequiredDependencies() {
            return requiredDependencies;
        }

        public void setRequiredDependencies(List<String> requiredDependencies) {
            this.requiredDependencies = requiredDependencies;
        }

        public List<String> getExcludedFiles() {
            return excludedFiles;
        }

        public void setExcludedFiles(List<String> excludedFiles) {
            this.excludedFiles = excludedFiles;
        }

        public String getOutputDirectory() {
            return outputDirectory;
        }

        public void setOutputDirectory(String outputDirectory) {
            this.outputDirectory = outputDirectory;
        }
    }
}
