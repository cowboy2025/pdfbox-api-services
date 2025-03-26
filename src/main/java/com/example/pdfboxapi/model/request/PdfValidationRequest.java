package com.example.pdfboxapi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request model for PDF validation (preflight) operations
 * 
 * This class represents the request parameters for validating a PDF file
 * against PDF/A standards.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
public class PdfValidationRequest {
    
    /**
     * The PDF file to validate
     */
    @NotNull(message = "PDF file is required")
    private MultipartFile pdfFile;
    
    /**
     * Validation profile to use
     * PDF/A-1b is the most common validation profile
     */
    private String profile = "PDF/A-1b";
}
