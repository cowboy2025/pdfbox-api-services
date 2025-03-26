package com.example.pdfboxapi.controller;

import com.example.pdfboxapi.model.ApiResponse;
import com.example.pdfboxapi.model.request.PdfToImageRequest;
import com.example.pdfboxapi.service.PdfToImageService;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * REST controller for PDF to image conversion operations
 * 
 * This controller provides endpoints for converting PDF pages to images.
 * 
 * @author Manus
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/convert")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PDF to Image Conversion", description = "API endpoints for converting PDF pages to images")
public class PdfToImageController {

    private final PdfToImageService pdfToImageService;

    /**
     * Converts PDF pages to images
     * 
     * @param pdfFile The PDF file to convert
     * @param imageFormat Image format to use for conversion
     * @param dpi DPI (dots per inch) for the output images
     * @param pageNumber Specific page to convert (optional)
     * @return ZIP file containing the converted images
     */
    @Operation(
        summary = "Convert PDF to images",
        description = "Converts PDF pages to images with specified format and DPI"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF successfully converted to images"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Error processing PDF file")
    })
    @PostMapping(value = "/to-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> convertPdfToImages(
            @Parameter(description = "PDF file to convert", required = true)
            @RequestParam("pdfFile") MultipartFile pdfFile,
            
            @Parameter(description = "Image format (PNG, JPEG, etc.)")
            @RequestParam(value = "imageFormat", required = false, defaultValue = "PNG") 
            String imageFormat,
            
            @Parameter(description = "DPI (dots per inch) for the output images")
            @RequestParam(value = "dpi", required = false, defaultValue = "300") 
            int dpi,
            
            @Parameter(description = "Specific page number to convert (1-based)")
            @RequestParam(value = "pageNumber", required = false) 
            Integer pageNumber
    ) {
        try {
            PdfToImageRequest request = new PdfToImageRequest();
            request.setPdfFile(pdfFile);
            request.setImageFormat(imageFormat.toUpperCase());
            request.setDpi(dpi);
            request.setPageNumber(pageNumber);
            
            List<byte[]> images = pdfToImageService.convertPdfToImages(request);
            
            // Create a ZIP file containing all images
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (int i = 0; i < images.size(); i++) {
                    String filename = String.format("page_%03d.%s", i + 1, imageFormat.toLowerCase());
                    ZipEntry entry = new ZipEntry(filename);
                    zos.putNextEntry(entry);
                    zos.write(images.get(i));
                    zos.closeEntry();
                }
            }
            
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pdf_images.zip")
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
