package com.example.pdfboxapi.controller;

import com.example.pdfboxapi.model.ApiResponse;
import com.example.pdfboxapi.model.request.PdfSigningRequest;
import com.example.pdfboxapi.service.PdfSigningService;
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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * REST controller for PDF signing operations
 * 
 * This controller provides endpoints for digitally signing PDF documents.
 * 
 * @author Manus
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/sign")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PDF Signing", description = "API endpoints for digitally signing PDF documents")
public class PdfSigningController {

    private final PdfSigningService pdfSigningService;

    /**
     * Signs a PDF document with a digital signature
     * 
     * @param pdfFile The PDF file to sign
     * @param keystoreFile The keystore file containing the signing certificate
     * @param keystorePassword Password for the keystore
     * @param certificateAlias Alias of the certificate in the keystore
     * @param reason Reason for signing the document
     * @param location Location where the document was signed
     * @param contactInfo Contact information of the signer
     * @return The signed PDF document
     */
    @Operation(
        summary = "Sign a PDF document",
        description = "Digitally signs a PDF document using a certificate from a keystore"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF successfully signed"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Error signing PDF file")
    })
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> signPdf(
            @Parameter(description = "PDF file to sign", required = true)
            @RequestParam("pdfFile") MultipartFile pdfFile,
            
            @Parameter(description = "Keystore file containing the signing certificate", required = true)
            @RequestParam("keystoreFile") MultipartFile keystoreFile,
            
            @Parameter(description = "Password for the keystore", required = true)
            @RequestParam("keystorePassword") String keystorePassword,
            
            @Parameter(description = "Alias of the certificate in the keystore", required = true)
            @RequestParam("certificateAlias") String certificateAlias,
            
            @Parameter(description = "Reason for signing the document")
            @RequestParam(value = "reason", required = false) String reason,
            
            @Parameter(description = "Location where the document was signed")
            @RequestParam(value = "location", required = false) String location,
            
            @Parameter(description = "Contact information of the signer")
            @RequestParam(value = "contactInfo", required = false) String contactInfo
    ) {
        try {
            PdfSigningRequest request = new PdfSigningRequest();
            request.setPdfFile(pdfFile);
            request.setKeystoreFile(keystoreFile);
            request.setKeystorePassword(keystorePassword);
            request.setCertificateAlias(certificateAlias);
            request.setReason(reason);
            request.setLocation(location);
            request.setContactInfo(contactInfo);
            
            byte[] signedPdf = pdfSigningService.signPdf(request);
            
            ByteArrayResource resource = new ByteArrayResource(signedPdf);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed_document.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException | KeyStoreException | CertificateException | 
                NoSuchAlgorithmException | UnrecoverableKeyException e) {
            log.error("Error signing PDF file", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
