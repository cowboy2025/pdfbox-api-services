package com.example.pdfboxapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response model for API operations
 * 
 * This class represents a standardized response format for all API operations.
 * It includes status information, messages, and the actual data payload.
 * 
 * @author Manus
 * @version 1.0
 * @param <T> Type of data contained in the response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * Indicates if the operation was successful
     */
    private boolean success;
    
    /**
     * Message describing the result of the operation
     */
    private String message;
    
    /**
     * Data payload returned by the operation
     */
    private T data;
    
    /**
     * Factory method to create a successful response
     * 
     * @param <T> Type of data
     * @param data The data to include in the response
     * @param message Success message
     * @return ApiResponse object with success status
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Factory method to create an error response
     * 
     * @param <T> Type of data
     * @param message Error message
     * @return ApiResponse object with error status
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
