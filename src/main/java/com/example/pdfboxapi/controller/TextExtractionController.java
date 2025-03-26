package com.example.pdfboxapi.controller;

import com.example.pdfboxapi.model.ApiResponse;
import com.example.pdfboxapi.model.request.TextExtractionRequest;
import com.example.pdfboxapi.service.TextExtractionService;
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

/**
 * REST controller for text extraction operations
 * 
 * This controller provides endpoints for extracting text from PDF documents.
 * 
 * @author Manus
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/extract")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Text Extraction", description = "API endpoints for extracting text from PDF documents")
public class TextExtractionController {

    private final TextExtractionService textExtractionService;

    /**
     * Extracts text from a PDF file
     * 
     * @param pdfFile The PDF file to extract text from
     * @param maintainPositioning Whether to maintain text positioning (optional)
     * @param pageNumber Specific page to extract text from (optional)
     * @return API response containing the extracted text
     */
    @Operation(
        summary = "Extract text from PDF",
        description = "Extracts text content from a PDF file with options to maintain positioning and extract from specific pages"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Text successfully extracted"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Error processing PDF file")
    })
    @PostMapping(value = "/text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public com.example.pdfboxapi.model.ApiResponse<String> extractText(
            @Parameter(description = "PDF file to extract text from", required = true)
            @RequestParam("pdfFile") MultipartFile pdfFile,
            
            @Parameter(description = "Whether to maintain text positioning")
            @RequestParam(value = "maintainPositioning", required = false, defaultValue = "false") 
            boolean maintainPositioning,
            
            @Parameter(description = "Specific page number to extract text from (1-based)")
            @RequestParam(value = "pageNumber", required = false) 
            Integer pageNumber
    ) {
        try {
            TextExtractionRequest request = new TextExtractionRequest();
            request.setPdfFile(pdfFile);
            request.setMaintainPositioning(maintainPositioning);
            request.setPageNumber(pageNumber);
            
            String extractedText = textExtractionService.extractText(request);
            return com.example.pdfboxapi.model.ApiResponse.success(
                    extractedText, 
                    "Text successfully extracted from PDF"
            );
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", e.getMessage());
            return com.example.pdfboxapi.model.ApiResponse.error("Invalid request: " + e.getMessage());
        } catch (IOException e) {
            log.error("Error processing PDF file", e);
            return com.example.pdfboxapi.model.ApiResponse.error("Error processing PDF file: " + e.getMessage());
        }
    }
}
