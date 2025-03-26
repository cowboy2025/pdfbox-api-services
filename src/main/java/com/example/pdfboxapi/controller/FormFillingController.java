package com.example.pdfboxapi.controller;

import com.example.pdfboxapi.model.ApiResponse;
import com.example.pdfboxapi.model.request.FormFillingRequest;
import com.example.pdfboxapi.service.FormFillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller for PDF form filling operations
 * 
 * This controller provides endpoints for filling form fields in PDF documents.
 * 
 * @author Manus
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/forms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PDF Form Filling", description = "API endpoints for filling PDF forms")
public class FormFillingController {

    private final FormFillingService formFillingService;

    /**
     * Fills form fields in a PDF document
     * 
     * @param pdfFile The PDF form file to fill
     * @param formData Form field data in JSON format
     * @param flatten Whether to flatten the form after filling
     * @return The filled PDF form
     */
    @Operation(
        summary = "Fill PDF form fields",
        description = "Fills form fields in a PDF document with provided data"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Form successfully filled"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Error processing PDF file")
    })
    @PostMapping(value = "/fill", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> fillForm(
            @Parameter(description = "PDF form file to fill", required = true)
            @RequestParam("pdfFile") MultipartFile pdfFile,
            
            @Parameter(description = "Form field data in JSON format", required = true)
            @RequestParam("formData") String formData,
            
            @Parameter(description = "Whether to flatten the form after filling")
            @RequestParam(value = "flatten", required = false, defaultValue = "false") 
            boolean flatten
    ) {
        try {
            FormFillingRequest request = new FormFillingRequest();
            request.setPdfFile(pdfFile);
            request.setFormData(formData);
            request.setFlatten(flatten);
            
            byte[] filledPdf = formFillingService.fillForm(request);
            
            ByteArrayResource resource = new ByteArrayResource(filledPdf);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filled_form.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Error processing PDF file", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
