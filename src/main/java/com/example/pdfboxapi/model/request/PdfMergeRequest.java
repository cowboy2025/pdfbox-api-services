package com.example.pdfboxapi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request model for PDF merge operations
 * 
 * This class represents the request parameters for merging multiple PDF files
 * into a single PDF document.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
public class PdfMergeRequest {
    
    /**
     * The PDF files to merge
     */
    @NotNull(message = "At least one PDF file is required")
    private MultipartFile[] pdfFiles;
}
