package com.riya.smarttravel.cleanup.model;

/**
 * Classification categories for files during cleanup analysis.
 * Determines the recommended action for each file.
 */
public enum FileClassification {
    /**
     * File should be kept (actively used)
     */
    KEEP,
    
    /**
     * File can be safely deleted (unused, not critical)
     */
    SAFE_TO_DELETE,
    
    /**
     * File has duplicates and can be merged
     */
    SAFE_TO_MERGE,
    
    /**
     * File requires manual verification before action
     */
    NEEDS_VERIFICATION,
    
    /**
     * File is critical for deployment (cannot be deleted)
     */
    DEPLOYMENT_CRITICAL,
    
    /**
     * File is critical for build process (cannot be deleted)
     */
    BUILD_CRITICAL
}
