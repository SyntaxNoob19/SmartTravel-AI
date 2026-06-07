package com.riya.smarttravel.cleanup.model;

/**
 * Represents the status of a cleanup phase.
 * Phases transition: PENDING → IN_PROGRESS → COMPLETED/FAILED
 */
public enum PhaseStatus {
    /**
     * Phase has not started yet
     */
    PENDING,
    
    /**
     * Phase is currently executing
     */
    IN_PROGRESS,
    
    /**
     * Phase completed successfully
     */
    COMPLETED,
    
    /**
     * Phase execution failed
     */
    FAILED
}
