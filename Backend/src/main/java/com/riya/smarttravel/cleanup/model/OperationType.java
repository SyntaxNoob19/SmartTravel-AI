package com.riya.smarttravel.cleanup.model;

/**
 * Types of file operations that can be performed during cleanup.
 * All operations require approval before execution.
 */
public enum OperationType {
    /**
     * Delete a file
     */
    DELETE,
    
    /**
     * Move a file to a different location
     */
    MOVE,
    
    /**
     * Rename a file
     */
    RENAME,
    
    /**
     * Merge duplicate files into one
     */
    MERGE,
    
    /**
     * Refactor code structure
     */
    REFACTOR
}
