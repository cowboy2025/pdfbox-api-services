package com.example.pdfboxapi.controller;

import com.example.pdfboxapi.model.ApiResponse;
import com.example.pdfboxapi.model.request.PdfSplitRequest;
import com.example.pdfboxapi.service.PdfSplitService;
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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.ByteArrayOutputStream;

/**
 * REST controller for PDF splitting operations
 * 
 * This controller provides endpoints for splitting PDF documents into multiple files.
 * 
 * @author Manus
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/split")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PDF Splitting", description = "API endpoints for splitting PDF documents")
public class PdfSplitController {

    private final PdfSplitService pdfSplitService;

    /**
     * Splits a PDF file into multiple PDFs
     * 
     * @param pdfFile The PDF file to split
     * @param splitType The type of split operation (BY_PAGE or BY_RANGE)
     * @param pageRanges Page ranges for splitting (only used when splitType is BY_RANGE)
     * @return ZIP file containing the split PDFs
     */
    @Operation(
        summary = "Split PDF into multiple files",
        description = "Splits a PDF file into multiple PDFs based on specified criteria and returns them as a ZIP file"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF successfully split"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Error processing PDF file")
    })
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> splitPdf(
            @Parameter(description = "PDF file to split", required = true)
            @RequestParam("pdfFile") MultipartFile pdfFile,
            
            @Parameter(description = "Split type (BY_PAGE or BY_RANGE)")
            @RequestParam(value = "splitType", required = false, defaultValue = "BY_PAGE") 
            PdfSplitRequest.SplitType splitType,
            
            @Parameter(description = "Page ranges for splitting (e.g., '1-3,5,7-10')")
            @RequestParam(value = "pageRanges", required = false) 
            String pageRanges
    ) {
        try {
            PdfSplitRequest request = new PdfSplitRequest();
            request.setPdfFile(pdfFile);
            request.setSplitType(splitType);
            request.setPageRanges(pageRanges);
            
            List<byte[]> splitPdfs = pdfSplitService.splitPdf(request);
            
            // Create a ZIP file containing all split PDFs
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (int i = 0; i < splitPdfs.size(); i++) {
                    String filename = String.format("split_%03d.pdf", i + 1);
                    ZipEntry entry = new ZipEntry(filename);
                    zos.putNextEntry(entry);
                    zos.write(splitPdfs.get(i));
                    zos.closeEntry();
                }
            }
            
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=split_pdfs.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
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
