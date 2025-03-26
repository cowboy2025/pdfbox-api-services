package com.example.pdfboxapi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request model for PDF to image conversion operations
 * 
 * This class represents the request parameters for converting PDF pages to images.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
public class PdfToImageRequest {
    
    /**
     * The PDF file to convert to images
     */
    @NotNull(message = "PDF file is required")
    private MultipartFile pdfFile;
    
    /**
     * Image format to use for conversion (PNG, JPEG, etc.)
     */
    private String imageFormat = "PNG";
    
    /**
     * DPI (dots per inch) for the output images
     */
    private int dpi = 300;
    
    /**
     * Page number to convert (optional)
     * When null, converts all pages
     */
    private Integer pageNumber;
}
