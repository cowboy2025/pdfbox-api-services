package com.example.pdfboxapi.controller;

import com.example.pdfboxapi.model.ApiResponse;
import com.example.pdfboxapi.model.request.PdfMergeRequest;
import com.example.pdfboxapi.service.PdfMergeService;
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
 * REST controller for PDF merging operations
 * 
 * This controller provides endpoints for merging multiple PDF documents into a single file.
 * 
 * @author Manus
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/merge")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PDF Merging", description = "API endpoints for merging PDF documents")
public class PdfMergeController {

    private final PdfMergeService pdfMergeService;

    /**
     * Merges multiple PDF files into a single PDF
     * 
     * @param pdfFiles The PDF files to merge
     * @return The merged PDF file
     */
    @Operation(
        summary = "Merge multiple PDFs into a single file",
        description = "Combines multiple PDF files into a single PDF document"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDFs successfully merged"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Error processing PDF files")
    })
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> mergePdfs(
            @Parameter(description = "PDF files to merge (minimum 2 files)", required = true)
            @RequestParam("pdfFiles") MultipartFile[] pdfFiles
    ) {
        try {
            PdfMergeRequest request = new PdfMergeRequest();
            request.setPdfFiles(pdfFiles);
            
            byte[] mergedPdf = pdfMergeService.mergePdfs(request);
            
            ByteArrayResource resource = new ByteArrayResource(mergedPdf);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=merged.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Error processing PDF files", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
