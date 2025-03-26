package com.example.pdfboxapi.controller;

import com.example.pdfboxapi.model.ApiResponse;
import com.example.pdfboxapi.model.request.PdfCreationRequest;
import com.example.pdfboxapi.service.PdfCreationService;
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
 * REST controller for PDF creation operations
 * 
 * This controller provides endpoints for creating new PDF documents.
 * 
 * @author Manus
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/create")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PDF Creation", description = "API endpoints for creating new PDF documents")
public class PdfCreationController {

    private final PdfCreationService pdfCreationService;

    /**
     * Creates a new PDF document
     * 
     * @param title The title of the PDF document
     * @param content The content to include in the PDF document
     * @param fontName Font name to use
     * @param fontSize Font size to use
     * @param image Optional image to include in the PDF
     * @param author Author metadata for the PDF
     * @return The created PDF document
     */
    @Operation(
        summary = "Create a new PDF document",
        description = "Creates a new PDF document with specified content and formatting options"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Error creating PDF file")
    })
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> createPdf(
            @Parameter(description = "Title of the PDF document", required = true)
            @RequestParam("title") String title,
            
            @Parameter(description = "Content to include in the PDF document", required = true)
            @RequestParam("content") String content,
            
            @Parameter(description = "Font name to use")
            @RequestParam(value = "fontName", required = false, defaultValue = "Helvetica") 
            String fontName,
            
            @Parameter(description = "Font size to use")
            @RequestParam(value = "fontSize", required = false, defaultValue = "12") 
            int fontSize,
            
            @Parameter(description = "Optional image to include in the PDF")
            @RequestParam(value = "image", required = false) 
            MultipartFile image,
            
            @Parameter(description = "Author metadata for the PDF")
            @RequestParam(value = "author", required = false, defaultValue = "PDFBox API Service") 
            String author
    ) {
        try {
            PdfCreationRequest request = new PdfCreationRequest();
            request.setTitle(title);
            request.setContent(content);
            request.setFontName(fontName);
            request.setFontSize(fontSize);
            request.setImage(image);
            request.setAuthor(author);
            
            byte[] pdfBytes = pdfCreationService.createPdf(request);
            
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=created_document.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Error creating PDF file", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
