package com.riya.smarttravel.cleanup.model;

/**
 * Represents the approval status for file operations.
 * All destructive operations require explicit approval.
 */
public enum ApprovalStatus {
    /**
     * Operation is awaiting user approval
     */
    PENDING,
    
    /**
     * Operation has been approved by user
     */
    APPROVED,
    
    /**
     * Operation has been rejected by user
     */
    REJECTED
}
