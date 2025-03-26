package com.example.pdfboxapi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request model for text extraction operations
 * 
 * This class represents the request parameters for extracting text from a PDF file.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
public class TextExtractionRequest {
    
    /**
     * The PDF file to extract text from
     */
    @NotNull(message = "PDF file is required")
    private MultipartFile pdfFile;
    
    /**
     * Whether to extract text by maintaining positioning (optional)
     * When true, attempts to maintain the original text positioning
     */
    private boolean maintainPositioning = false;
    
    /**
     * Page number to extract text from (optional)
     * When null, extracts text from all pages
     */
    private Integer pageNumber;
}
