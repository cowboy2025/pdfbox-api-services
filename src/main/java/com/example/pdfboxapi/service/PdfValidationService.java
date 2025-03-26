package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfValidationRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for PDF validation (preflight) operations using PDFBox
 * 
 * This service provides methods to validate PDF documents against PDF/A standards
 * using PDFBox preflight capabilities.
 * 
 * @author Manus
 * @version 1.0
 */
@Service
@Slf4j
public class PdfValidationService {

    /**
     * Validates a PDF file against PDF/A standards
     * 
     * @param request The PDF validation request containing the PDF file and validation options
     * @return List of validation error messages (empty if validation passed)
     * @throws IOException If there's an error processing the PDF
     */
    public List<String> validatePdf(PdfValidationRequest request) throws IOException {
        MultipartFile pdfFile = request.getPdfFile();
        File tempFile = convertMultipartFileToFile(pdfFile);
        List<String> validationErrors = new ArrayList<>();
        
        try {
            // Create preflight parser
            PreflightParser parser = new PreflightParser(tempFile);
            
            // Parse the PDF
            parser.parse(request.getProfile());
            
            // Get the preflight document
            PreflightDocument document = parser.getPreflightDocument();
            
            try {
                // Validate the document
                ValidationResult result = document.validate();
                
                // Check if the validation was successful
                if (!result.isValid()) {
                    // Collect validation error messages
                    result.getErrorsList().forEach(error -> 
                        validationErrors.add(error.getDetails())
                    );
                    
                    log.info("PDF validation failed with {} errors", validationErrors.size());
                } else {
                    log.info("PDF validation passed successfully");
                }
                
                return validationErrors;
                
            } finally {
                // Close the preflight document
                if (document != null) {
                    document.close();
                }
            }
            
        } catch (SyntaxValidationException e) {
            // Handle syntax validation errors
            validationErrors.add("Syntax validation error: " + e.getMessage());
            log.warn("PDF syntax validation failed", e);
            return validationErrors;
            
        } finally {
            // Clean up the temporary file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
    
    /**
     * Converts a MultipartFile to a File
     * 
     * @param multipartFile The MultipartFile to convert
     * @return A temporary File object
     * @throws IOException If there's an error during conversion
     */
    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        Path tempFile = Files.createTempFile("pdf-", ".pdf");
        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile.toFile();
    }
}
