# Implementation Plan: Project Cleanup and Deployment Preparation

## Overview

This implementation plan provides a comprehensive, step-by-step approach to building the Project Cleanup and Deployment Preparation system for the SmartTravel application. The system will audit, analyze, document, and safely prepare the project for deployment through 19 consolidated phases while preserving all functionality including authentication, AI itinerary generation, budget calculation, and database connectivity.

The implementation follows a strict "audit-first, verify-all-references, wait-for-approval" protocol where no files are deleted, moved, renamed, or refactored without explicit verification of all dependencies and user approval. The system incorporates Git safety protocols, continuous deployment validation, and progressive UML diagram generation.

## Tasks

- [x] 1. Set up project structure and core data models
  - Create Java package structure: `com.riya.smarttravel.cleanup`
  - Create subpackages: `model`, `service`, `analyzer`, `validator`, `generator`, `controller`
  - Define core data models: `PhaseResult`, `AnalysisReport`, `FileClassificationResult`, `DependencyGraph`, `FileOperation`, `SmartTravelProject`
  - Define enums: `PhaseStatus`, `ApprovalStatus`, `FileClassification`, `OperationType`, `RiskLevel`, `DependencyType`
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 2. Implement Git Safety Protocol
  - [x] 2.1 Create GitSafetyService with branch verification
    - Implement method to check if cleanup branch exists
    - Implement method to create cleanup branch "cleanup-audit"
    - Implement method to verify current branch
    - Implement method to block operations on main/master branch
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

  - [x]* 2.2 Write property test for Git safety enforcement
    - **Property 11: Git Safety on Cleanup Branch**
    - **Validates: Requirement 1.5**
    - Test that all destructive operations are blocked when current branch is main or master
    - Verify cleanup branch is required for all file operations

- [x] 3. Implement PhaseController component
  - [x] 3.1 Create PhaseController class with phase orchestration logic
    - Implement `executePhase(PhaseNumber phase)` method
    - Implement `getCurrentPhase()` method
    - Implement `canProceedToNextPhase()` method
    - Implement `markPhaseComplete(PhaseNumber phase, ApprovalStatus approval)` method
    - Implement `getAllCompletedPhases()` method
    - Enforce sequential phase execution (Phase N+1 cannot start until Phase N is COMPLETED)
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

  - [x]* 3.2 Write property test for phase sequential execution
    - **Property 1: Phase Sequential Execution**
    - **Validates: Requirement 2.4**
    - Test that Phase N+1 cannot start until Phase N is marked COMPLETED
    - Attempt to start phase N+1 while phase N is IN_PROGRESS should fail

- [x] 4. Implement ReferenceAnalyzer component
  - [x] 4.1 Create ReferenceAnalyzer class with reference scanning logic
    - Implement `findReferencesToFile(File file)` to scan JavaScript imports, Java imports, HTML references, CSS references
    - Implement `buildDependencyGraph(Folder folder)` to create complete dependency graph
    - Implement `findAllImports(File file)` to extract all import statements
    - Implement `findAllRoutes(File file)` to extract route definitions
    - Implement `findApiEndpointUsages(String endpoint)` to find API endpoint references
    - Use regex patterns to detect ES6 imports, CommonJS require(), Java import statements, HTML script/link/img tags, CSS @import and url()
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 10.1, 10.2, 10.3_

  - [x]* 4.2 Write property test for reference integrity before classification
    - **Property 2: Reference Integrity Before Classification**
    - **Validates: Requirements 5.1, 5.2, 5.3, 5.4**
    - Test that file classification is blocked when reference analysis fails
    - Verify ReferenceAnalyzer is called before StructureAnalyzer.classifyFiles

  - [x]* 4.3 Write property test for reference count accuracy
    - **Property 8: Reference Count Accuracy**
    - **Validates: Requirements 5.5, 10.1, 10.2**
    - Test that reference count matches actual number of import/reference statements
    - Verify count accuracy across JavaScript, Java, HTML, and CSS files

- [x] 5. Implement SafetyValidator component
  - [x] 5.1 Create SafetyValidator class with operation validation logic
    - Implement `validateFileOperation(FileOperation operation)` to validate all file operations
    - Implement `requiresUserApproval(FileOperation operation)` to determine if approval needed
    - Implement `checkAllReferences(File file)` to verify file references
    - Implement `assessDeploymentImpact(FileOperation operation)` to assess deployment criticality
    - Implement `assessBuildImpact(FileOperation operation)` to assess build criticality
    - Mark deployment-critical files: pom.xml, mvnw, mvnw.cmd, application.properties, application.yml, index.html
    - Mark build-critical files: pom.xml, .mvn/ folder, mvnw, mvnw.cmd, SmarttravelApplication.java, resources/
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 7.1, 7.2, 7.3, 7.4, 7.5, 13.1, 13.2, 13.3, 13.4, 13.5_

  - [x]* 5.2 Write property test for no destructive operations without approval
    - **Property 3: No Destructive Operations Without Approval**
    - **Validates: Requirements 13.1, 13.2, 13.3, 13.4**
    - Test that DELETE, MOVE, RENAME, MERGE operations are blocked without approval
    - Attempt to execute DELETE without approval should throw exception

  - [x]* 5.3 Write property test for deployment-critical file protection
    - **Property 4: Deployment-Critical Files Never Marked SAFE_TO_DELETE**
    - **Validates: Requirements 6.1, 6.2, 6.3, 6.4**
    - Test that pom.xml, index.html, mvnw, application.properties are never marked SAFE_TO_DELETE
    - Verify classification logic excludes deployment-critical files

  - [x]* 5.4 Write property test for build-critical file protection
    - **Property 5: Build-Critical Files Never Marked SAFE_TO_DELETE**
    - **Validates: Requirements 7.1, 7.2, 7.3, 7.4**
    - Test that build files (mvnw, pom.xml, SmarttravelApplication.java) are never marked SAFE_TO_DELETE
    - Verify classification logic excludes build-critical files

- [x] 6. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 7. Implement StructureAnalyzer component
  - [x] 7.1 Create StructureAnalyzer class with structure analysis logic
    - Implement `analyzeFolderStructure(Folder folder)` to list all files and analyze structure
    - Implement `detectNamingIssues(Folder folder)` to detect naming convention violations (Java: PascalCase for classes, camelCase for methods; JavaScript: camelCase for functions; CSS: kebab-case)
    - Implement `detectOrganizationIssues(Folder folder)` to detect misplaced files
    - Implement `classifyFiles(Folder folder)` to classify files into KEEP/SAFE_TO_DELETE/SAFE_TO_MERGE/NEEDS_VERIFICATION/DEPLOYMENT_CRITICAL/BUILD_CRITICAL
    - _Requirements: 21.1, 21.2, 21.3, 21.4, 21.5, 22.1, 22.2, 22.3, 22.4, 22.5_

  - [x]* 7.2 Write unit tests for StructureAnalyzer
    - Test folder structure analysis
    - Test naming convention detection (Java PascalCase, JavaScript camelCase, CSS kebab-case)
    - Test organization issue detection
    - Test file classification logic

- [x] 8. Implement DependencyAnalyzer component
  - [x] 8.1 Create DependencyAnalyzer class with dependency analysis logic
    - Implement `buildDependencyTree(File rootFile)` to build complete dependency tree
    - Implement `detectCircularDependencies(Folder folder)` to identify circular import cycles using graph cycle detection
    - Implement `findUnusedDependencies(Project project)` to scan pom.xml and find unused Maven dependencies
    - Implement `findMissingDependencies(Project project)` to detect missing dependencies
    - _Requirements: 10.3, 10.4, 10.5, 16.1, 16.2, 16.3, 16.4, 16.5, 24.1, 24.2, 24.3, 24.4, 24.5_

  - [x]* 8.2 Write property test for circular dependency detection
    - **Property 7: Circular Dependencies Detected and Reported**
    - **Validates: Requirements 16.1, 16.2, 16.3, 16.4, 16.5**
    - Test that all circular import cycles are detected
    - Verify cycles are documented in analysis report

- [x] 9. Implement DeadCodeDetector component
  - [x] 9.1 Create DeadCodeDetector class with dead code detection logic
    - Implement `findUnusedFiles(Folder folder)` to identify files with zero references
    - Implement `findUnusedFunctions(File file)` to identify unused functions within files
    - Implement `findUnusedClasses(File file)` to identify unused classes
    - Implement `findUnusedAssets(Folder assetFolder)` to identify unused images, fonts, icons
    - Verify unused files are not entry points (index.html, SmarttravelApplication.java)
    - Verify unused files are not deployment-critical or build-critical
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 25.1, 25.2, 25.3, 25.4, 25.5_

  - [x]* 9.2 Write property test for zero-reference file verification
    - **Property 9: Zero-Reference Files Verified Before Deletion**
    - **Validates: Requirements 8.1, 8.2, 8.3, 8.4, 8.5**
    - Test that files with zero references are verified as not entry points or critical files
    - Verify all zero-reference files pass entry point and criticality checks before marking SAFE_TO_DELETE

- [x] 10. Implement DuplicateDetector component
  - [x] 10.1 Create DuplicateDetector class with duplicate detection logic
    - Implement `findDuplicateFiles(Folder folder)` using file hash calculation (MD5 or SHA-256)
    - Implement `findSimilarFiles(Folder folder, double threshold)` using similarity algorithms (85% threshold)
    - Implement `findDuplicateCodeBlocks(File file)` to detect duplicate code within files
    - Generate merge recommendations with diff comparisons
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_

  - [x]* 10.2 Write unit tests for DuplicateDetector
    - Test exact duplicate detection using file hashes
    - Test near-duplicate detection with 85% similarity threshold
    - Test duplicate code block detection
    - Test merge recommendation generation

- [x] 11. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 12. Implement Continuous Deployment Validation
  - [x] 12.1 Create DeploymentValidator class with validation logic
    - Implement `validateBackendCompilation()` to execute "mvn clean compile" and parse output
    - Implement `validateFrontendAccessibility()` to verify index.html exists and all referenced assets are accessible
    - Implement `validateFeatureEndpoints()` to scan controllers for required routes (/login, /register, /explore, /planner, /trips, /budget)
    - Implement `validateDatabase()` to verify entity mappings and database connectivity
    - Implement `executeFullValidation()` to run all validation steps
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 17.1, 17.2, 17.3, 17.4, 17.5, 18.1, 18.2, 18.3, 18.4, 18.5, 19.1, 19.2, 19.3, 19.4, 19.5_

  - [x]* 12.2 Write property test for continuous validation after approval
    - **Property 12: Continuous Validation After Approval**
    - **Validates: Requirements 3.1, 3.5**
    - Test that deployment validation is executed immediately after every approved operation
    - Verify operations are halted if validation fails

  - [x]* 12.3 Write property test for validation failure halts operations
    - **Property 13: Validation Failure Halts Operations**
    - **Validates: Requirement 3.5**
    - Test that when any validation step fails, no further operations are executed
    - Verify failed compilation, missing assets, or missing routes all halt operations

- [x] 13. Implement DocumentationGenerator component
  - [x] 13.1 Create DocumentationGenerator class with documentation generation logic
    - Implement `generateProjectStructure(Project project)` to document folder structure
    - Implement `generateDeploymentGuide(Project project)` to create deployment instructions
    - Implement `generateTechnicalDocumentation(Project project)` to document architecture
    - Implement `generateAPIDocumentation(List<Controller> controllers)` to extract REST endpoints from @RestController, @GetMapping, @PostMapping annotations
    - Implement `generateSetupGuide(Project project)` to document setup instructions
    - Write all documentation to markdown files in docs/ folder
    - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5, 26.1, 26.2, 26.3, 26.4, 26.5, 26.6_

  - [x]* 13.2 Write property test for documentation generation after each phase
    - **Property 6: Documentation Generated After Each Phase**
    - **Validates: Requirements 11.1, 11.2, 11.3, 11.4, 11.5, 30.1, 30.2, 30.3, 30.4**
    - Test that documentation is generated after every completed phase
    - Verify documentation files exist after phase completion

- [x] 14. Implement DiagramGenerator component
  - [x] 14.1 Create DiagramGenerator class with Mermaid diagram generation logic
    - Implement `generateFolderStructureDiagram(Project project)` to create folder tree diagram
    - Implement `generateSystemArchitectureDiagram(Project project)` for overall architecture
    - Implement `generateFrontendArchitectureDiagram(Frontend frontend)` for frontend component architecture
    - Implement `generateBackendArchitectureDiagram(Backend backend)` for backend layer architecture
    - Implement `generateDependencyDiagram(DependencyGraph graph)` for dependency visualization
    - Implement `generateNavigationFlowDiagram(Frontend frontend)` for frontend routes
    - Implement `generateAPIFlowDiagram(List<Controller> controllers)` for API endpoints
    - Implement `generateAuthenticationFlowDiagram(SecurityConfig security)` for auth flows
    - Implement UML diagram generators: `generateUseCaseDiagram`, `generateActivityDiagram`, `generateClassDiagram`, `generateComponentDiagram`, `generateDeploymentDiagram`, `generateSequenceDiagram`
    - All diagrams should use Mermaid syntax
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 12.1, 12.2, 12.3, 12.4, 12.5, 27.4, 28.4_

  - [x]* 14.2 Write unit tests for DiagramGenerator
    - Test Mermaid syntax generation for all diagram types
    - Test folder tree diagram with color coding (red for SAFE_TO_DELETE, green for DEPLOYMENT_CRITICAL)
    - Test UML diagram generation (Use Case, Activity, Class, Component, Deployment, Sequence)
    - Test authentication flow sequence diagram generation

- [x] 15. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 16. Implement Phase Execution Workflow
  - [x] 16.1 Create PhaseExecutor class to orchestrate complete phase execution
    - Implement phase execution workflow: Git branch verification → Structure analysis → Reference analysis → Dependency analysis → Dead code detection → Duplicate detection → File classification → Risk assessment → Documentation generation → Diagram generation → Analysis report creation
    - Integrate PhaseController, SafetyValidator, ReferenceAnalyzer, StructureAnalyzer, DependencyAnalyzer, DeadCodeDetector, DuplicateDetector, DocumentationGenerator, DiagramGenerator
    - Generate phase transition reports
    - Await user approval before proceeding to next phase
    - _Requirements: 2.5, 15.1, 15.2, 15.3, 15.4, 15.5_

  - [x]* 16.2 Write integration tests for phase execution workflow
    - Test complete phase 1 execution (README.md audit)
    - Test phase transition from phase N to phase N+1
    - Test phase failure and recovery
    - Test documentation and diagram generation during phase execution

- [x] 17. Implement Risk Assessment and Reporting
  - [x] 17.1 Create RiskAssessmentService with risk analysis logic
    - Implement `assessDeploymentImpact(FileOperation operation)` returning LOW/MEDIUM/HIGH/CRITICAL
    - Implement `assessBuildImpact(FileOperation operation)` returning LOW/MEDIUM/HIGH/CRITICAL
    - Implement `generateRiskReport(FileOperation operation)` listing all references and dependencies
    - Implement `requiresDoubleConfirmation(FileOperation operation)` for high-risk operations
    - _Requirements: 14.1, 14.2, 14.3, 14.4, 14.5, 34.1, 34.2, 34.3, 34.4, 34.5, 34.6_

  - [x]* 17.2 Write property test for file operations have reason and impact assessment
    - **Property 10: All File Operations Have Reason and Impact Assessment**
    - **Validates: Requirements 14.1, 14.2, 14.3, 14.4, 14.5**
    - Test that all file operations have non-null reason and impact assessment
    - Attempt to create operation without reason or impact assessment should fail

- [x] 18. Implement Analysis Report Persistence
  - [x] 18.1 Create ReportPersistenceService to save analysis reports
    - Implement `saveAnalysisReport(PhaseResult phaseResult)` to write markdown files
    - Save reports to `.kiro/specs/project-cleanup-deployment-prep/reports/phase-N-analysis-report.md`
    - Include phase number, phase name, target folders, timestamp, files analyzed, classification results, recommendations, risk assessments
    - Preserve all previous reports without overwriting
    - _Requirements: 20.1, 20.2, 20.3, 20.4, 20.5_

  - [x]* 18.2 Write unit tests for ReportPersistenceService
    - Test analysis report saving to correct directory
    - Test report format includes all required sections
    - Test that previous reports are not overwritten

- [x] 19. Implement Error Handling and Recovery
  - [x] 19.1 Create ErrorRecoveryService with robust error handling
    - Implement error handling for reference analysis failures (syntax errors, missing files)
    - Implement error handling for phase execution failures (save partial results, mark phase as FAILED)
    - Implement phase restart capability
    - Implement error logging with timestamp, phase number, target file, exception message
    - Mark files with analysis errors as NEEDS_VERIFICATION
    - _Requirements: 29.1, 29.2, 29.3, 29.4, 29.5_

  - [x]* 19.2 Write unit tests for ErrorRecoveryService
    - Test handling of reference analysis failures
    - Test saving partial analysis results on phase failure
    - Test phase restart capability
    - Test error logging format

- [x] 20. Implement Rollback Safety and Backup
  - [x] 20.1 Create BackupService with file backup and rollback logic
    - Implement `createBackup(File file)` to backup files before modification
    - Save backups to `.kiro/specs/project-cleanup-deployment-prep/backups/` with timestamp
    - Implement `rollbackFile(File file, Timestamp timestamp)` to restore previous version
    - Implement `cleanupBackups()` to remove backups after session completion
    - _Requirements: 32.1, 32.2, 32.3, 32.4, 32.5_

  - [x]* 20.2 Write unit tests for BackupService
    - Test backup creation before file modification
    - Test rollback functionality
    - Test backup file naming with timestamp
    - Test backup cleanup after session

- [x] 21. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 22. Implement API Endpoint Documentation Extraction
  - [x] 22.1 Create ApiDocumentationExtractor class
    - Implement scanning of all controller classes for @RestController, @Controller annotations
    - Extract HTTP methods (@GetMapping, @PostMapping, @PutMapping, @DeleteMapping)
    - Extract endpoint paths from annotations
    - Extract request parameters, request body types (@RequestBody), and response types
    - Generate markdown documentation organized by controller
    - _Requirements: 26.1, 26.2, 26.3, 26.4, 26.5, 26.6_

  - [x]* 22.2 Write unit tests for ApiDocumentationExtractor
    - Test controller class detection
    - Test HTTP method extraction
    - Test endpoint path extraction
    - Test request/response type extraction

- [x] 23. Implement Authentication Flow Documentation
  - [x] 23.1 Create AuthenticationFlowDocumenter class
    - Implement detection of Spring Security configuration classes
    - Extract authentication mechanisms (session-based, JWT, OAuth)
    - Extract authorization rules (URL patterns requiring authentication)
    - Generate Mermaid sequence diagram showing login flow
    - Document session management and security filter chain
    - _Requirements: 27.1, 27.2, 27.3, 27.4, 27.5_

  - [x]* 23.2 Write unit tests for AuthenticationFlowDocumenter
    - Test Spring Security configuration detection
    - Test authentication mechanism extraction
    - Test authorization rule extraction
    - Test login flow sequence diagram generation

- [x] 24. Implement Database Schema Documentation
  - [x] 24.1 Create DatabaseSchemaDocumenter class
    - Scan all entity classes for @Entity annotation
    - Extract field names, types, and JPA annotations (@Column, @Id, @GeneratedValue)
    - Extract relationships (@OneToMany, @ManyToOne, @ManyToMany)
    - Generate Mermaid class diagram showing entities and relationships
    - Document table names, column names, primary keys, foreign keys
    - _Requirements: 28.1, 28.2, 28.3, 28.4, 28.5_

  - [x]* 24.2 Write unit tests for DatabaseSchemaDocumenter
    - Test entity class detection
    - Test field and annotation extraction
    - Test relationship extraction (@OneToMany, @ManyToOne, @ManyToMany)
    - Test entity relationship class diagram generation

- [x] 25. Implement Configuration File Security
  - [x] 25.1 Create ConfigurationSecurityService for sensitive data handling
    - Implement warning detection for .env files
    - Implement password masking in application.properties and application.yml logs
    - Implement sensitive key redaction (password, secret, key, token) in file content display
    - Exclude .env file contents from generated documentation
    - Provide option to exclude sensitive folders from analysis
    - _Requirements: 23.1, 23.2, 23.3, 23.4, 23.5_

  - [x]* 25.2 Write unit tests for ConfigurationSecurityService
    - Test .env file warning detection
    - Test password masking in logs
    - Test sensitive key redaction (password, secret, key, token)
    - Test exclusion of sensitive folders

- [x] 26. Implement Performance Optimization
  - [x] 26.1 Create ParallelAnalysisService for performance optimization
    - Implement parallel file scanning with configurable thread pool
    - Implement caching of reference analysis results across phases
    - Implement caching of file hashes for duplicate detection
    - Limit dependency tree traversal depth to 10 levels
    - Implement progress indicators showing percentage complete and estimated time remaining
    - _Requirements: 33.1, 33.2, 33.3, 33.4, 33.5_

  - [x]* 26.2 Write unit tests for ParallelAnalysisService
    - Test parallel file scanning with thread pool
    - Test reference analysis caching
    - Test file hash caching
    - Test dependency tree depth limiting

- [x] 27. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 28. Implement Git Commit Recommendations
  - [x] 28.1 Create GitCommitRecommender class
    - Implement commit recommendation after each phase completion
    - Generate commit messages in format: "Phase N: [phase name] - [summary of changes]"
    - Include counts of files deleted, merged, moved, refactored in commit message
    - List all modified files for user review
    - Remind user to run deployment validation before committing
    - _Requirements: 31.1, 31.2, 31.3, 31.4, 31.5_

  - [x]* 28.2 Write unit tests for GitCommitRecommender
    - Test commit message generation
    - Test file change count inclusion (deleted, merged, moved, refactored)
    - Test modified file listing
    - Test deployment validation reminder

- [x] 29. Implement Incremental Documentation Updates
  - [x] 29.1 Create IncrementalDocumentationService
    - Implement phase-specific documentation updates: Phase 1 → README.md section, Phase 2 → Frontend architecture, Phase 3 → Backend architecture and API reference, Phase 5 → Consolidate all documentation
    - Avoid regenerating unchanged documentation sections to preserve manual edits
    - Track documentation version and last update timestamp
    - _Requirements: 30.1, 30.2, 30.3, 30.4, 30.5_

  - [x]* 29.2 Write unit tests for IncrementalDocumentationService
    - Test phase-specific documentation generation
    - Test preservation of unchanged documentation sections
    - Test documentation version tracking

- [x] 30. Create REST API endpoints and CLI interface
  - [x] 30.1 Create CleanupController REST API
    - Implement POST `/api/cleanup/start` to start cleanup process
    - Implement POST `/api/cleanup/phase/{phaseNumber}/execute` to execute specific phase
    - Implement POST `/api/cleanup/phase/{phaseNumber}/approve` to approve phase completion
    - Implement POST `/api/cleanup/operation/approve` to approve file operation
    - Implement GET `/api/cleanup/status` to get current cleanup status
    - Implement GET `/api/cleanup/phase/{phaseNumber}/report` to get phase analysis report
    - _Requirements: All requirements (REST API for user interaction)_

  - [x]* 30.2 Write integration tests for CleanupController REST API
    - Test all REST endpoints
    - Test phase execution workflow through API
    - Test approval workflow through API
    - Test status and report retrieval

- [x] 31. Final integration and wiring
  - [x] 31.1 Wire all components together in Spring Boot configuration
    - Create Spring configuration class to wire all services and components
    - Configure thread pool for parallel analysis
    - Configure file system paths for reports, backups, documentation
    - Configure Git integration
    - Add comprehensive logging throughout all components
    - _Requirements: All requirements (integration)_

  - [x]* 31.2 Write end-to-end integration tests
    - Test complete cleanup workflow from Phase 1 to Phase 19
    - Test Git safety enforcement throughout workflow
    - Test continuous validation after every operation
    - Test documentation and diagram generation across all phases
    - Test error handling and recovery scenarios

- [x] 32. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation and provide opportunities for user feedback
- Property tests validate universal correctness properties defined in the design document
- Unit tests validate specific components and edge cases
- Integration tests validate complete workflows and component interactions
- The system is implemented in Java to match the SmartTravel Spring Boot backend architecture
- All file operations require explicit user approval to ensure safety
- Git safety protocols prevent accidental destructive operations on main/master branches
- Continuous deployment validation ensures the application remains functional throughout cleanup

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1"] },
    { "id": 1, "tasks": ["2.1", "3.1", "4.1", "5.1"] },
    { "id": 2, "tasks": ["2.2", "3.2", "4.2", "4.3", "5.2", "5.3", "5.4"] },
    { "id": 3, "tasks": ["7.1", "8.1", "9.1", "10.1"] },
    { "id": 4, "tasks": ["7.2", "8.2", "9.2", "10.2"] },
    { "id": 5, "tasks": ["12.1"] },
    { "id": 6, "tasks": ["12.2", "12.3"] },
    { "id": 7, "tasks": ["13.1", "14.1"] },
    { "id": 8, "tasks": ["13.2", "14.2"] },
    { "id": 9, "tasks": ["16.1", "17.1"] },
    { "id": 10, "tasks": ["16.2", "17.2"] },
    { "id": 11, "tasks": ["18.1", "19.1", "20.1"] },
    { "id": 12, "tasks": ["18.2", "19.2", "20.2"] },
    { "id": 13, "tasks": ["22.1", "23.1", "24.1", "25.1", "26.1"] },
    { "id": 14, "tasks": ["22.2", "23.2", "24.2", "25.2", "26.2"] },
    { "id": 15, "tasks": ["28.1", "29.1"] },
    { "id": 16, "tasks": ["28.2", "29.2"] },
    { "id": 17, "tasks": ["30.1"] },
    { "id": 18, "tasks": ["30.2"] },
    { "id": 19, "tasks": ["31.1"] },
    { "id": 20, "tasks": ["31.2"] }
  ]
}
```
