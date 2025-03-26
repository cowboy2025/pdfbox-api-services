package com.example.pdfboxapi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request model for PDF form filling operations
 * 
 * This class represents the request parameters for filling form fields in a PDF file.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
public class FormFillingRequest {
    
    /**
     * The PDF form file to fill
     */
    @NotNull(message = "PDF form file is required")
    private MultipartFile pdfFile;
    
    /**
     * Form field data in JSON format
     * Example: {"name":"John Doe","email":"john@example.com"}
     */
    @NotNull(message = "Form field data is required")
    private String formData;
    
    /**
     * Whether to flatten the form after filling (makes form fields non-editable)
     */
    private boolean flatten = false;
}
