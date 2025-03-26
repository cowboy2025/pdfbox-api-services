package com.example.pdfboxapi.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request model for PDF creation operations
 * 
 * This class represents the request parameters for creating a new PDF document.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
public class PdfCreationRequest {
    
    /**
     * The title of the PDF document
     */
    @NotBlank(message = "Title is required")
    private String title;
    
    /**
     * The content to include in the PDF document
     */
    @NotBlank(message = "Content is required")
    private String content;
    
    /**
     * Font name to use (default is Helvetica)
     */
    private String fontName = "Helvetica";
    
    /**
     * Font size to use (default is 12)
     */
    private int fontSize = 12;
    
    /**
     * Optional image to include in the PDF
     */
    private MultipartFile image;
    
    /**
     * Author metadata for the PDF
     */
    private String author = "PDFBox API Service";
}
