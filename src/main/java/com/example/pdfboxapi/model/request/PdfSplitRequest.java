package com.example.pdfboxapi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request model for PDF splitting operations
 * 
 * This class represents the request parameters for splitting a PDF file
 * into multiple files.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
public class PdfSplitRequest {
    
    /**
     * The PDF file to split
     */
    @NotNull(message = "PDF file is required")
    private MultipartFile pdfFile;
    
    /**
     * Split type determines how the PDF will be split
     * - BY_PAGE: Split into individual pages
     * - BY_RANGE: Split according to page ranges
     */
    private SplitType splitType = SplitType.BY_PAGE;
    
    /**
     * Page ranges for splitting (only used when splitType is BY_RANGE)
     * Format: "1-3,5,7-10" would create 3 PDFs with pages 1-3, 5, and 7-10
     */
    private String pageRanges;
    
    /**
     * Enum representing different ways to split a PDF
     */
    public enum SplitType {
        BY_PAGE,    // Split into individual pages
        BY_RANGE    // Split according to specified page ranges
    }
}
