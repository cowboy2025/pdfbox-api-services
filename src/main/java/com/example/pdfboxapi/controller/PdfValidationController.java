package com.example.pdfboxapi.controller;

import com.example.pdfboxapi.model.ApiResponse;
import com.example.pdfboxapi.model.request.PdfValidationRequest;
import com.example.pdfboxapi.service.PdfValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for PDF validation (preflight) operations
 * 
 * This controller provides endpoints for validating PDF documents against PDF/A standards.
 * 
 * @author Manus
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/validate")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PDF Validation", description = "API endpoints for validating PDF documents against standards")
public class PdfValidationController {

    private final PdfValidationService pdfValidationService;

    /**
     * Validates a PDF file against PDF/A standards
     * 
     * @param pdfFile The PDF file to validate
     * @param profile Validation profile to use
     * @return API response containing validation results
     */
    @Operation(
        summary = "Validate PDF against standards",
        description = "Validates a PDF file against PDF/A standards and returns validation results"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validation completed"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Error processing PDF file")
    })
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public com.example.pdfboxapi.model.ApiResponse<ValidationResult> validatePdf(
            @Parameter(description = "PDF file to validate", required = true)
            @RequestParam("pdfFile") MultipartFile pdfFile,
            
            @Parameter(description = "Validation profile (e.g., 'PDF/A-1b')")
            @RequestParam(value = "profile", required = false, defaultValue = "PDF/A-1b") 
            String profile
    ) {
        try {
            PdfValidationRequest request = new PdfValidationRequest();
            request.setPdfFile(pdfFile);
            request.setProfile(profile);
            
            List<String> validationErrors = pdfValidationService.validatePdf(request);
            
            ValidationResult result = new ValidationResult();
            result.setValid(validationErrors.isEmpty());
            result.setErrors(validationErrors);
            
            if (validationErrors.isEmpty()) {
                return com.example.pdfboxapi.model.ApiResponse.success(
                        result, 
                        "PDF validation passed successfully"
                );
            } else {
                return com.example.pdfboxapi.model.ApiResponse.success(
                        result, 
                        "PDF validation failed with " + validationErrors.size() + " errors"
                );
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", e.getMessage());
            return com.example.pdfboxapi.model.ApiResponse.error("Invalid request: " + e.getMessage());
        } catch (IOException e) {
            log.error("Error processing PDF file", e);
            return com.example.pdfboxapi.model.ApiResponse.error("Error processing PDF file: " + e.getMessage());
        }
    }
    
    /**
     * Inner class representing validation results
     */
    @lombok.Data
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
    }
}
