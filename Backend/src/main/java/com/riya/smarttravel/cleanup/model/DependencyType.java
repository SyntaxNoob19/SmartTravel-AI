package com.riya.smarttravel.cleanup.model;

/**
 * Types of dependencies between files in the codebase.
 * Used for building dependency graphs and reference analysis.
 */
public enum DependencyType {
    /**
     * Import statement (Java/JavaScript)
     */
    IMPORT,
    
    /**
     * Direct reference (function call, class usage)
     */
    REFERENCE,
    
    /**
     * Asset reference (img src, link href)
     */
    ASSET,
    
    /**
     * Route reference (frontend navigation)
     */
    ROUTE,
    
    /**
     * API endpoint call (REST API usage)
     */
    API_CALL
}
