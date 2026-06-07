package com.riya.smarttravel.cleanup.model;

/**
 * Risk level assessment for file operations.
 * Determines the level of caution required for approval.
 */
public enum RiskLevel {
    /**
     * Low risk - unlikely to cause issues
     */
    LOW,
    
    /**
     * Medium risk - may affect some functionality
     */
    MEDIUM,
    
    /**
     * High risk - likely to affect important functionality
     */
    HIGH,
    
    /**
     * Critical risk - may break deployment or build
     */
    CRITICAL
}
