package com.example.pdfboxapi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request model for PDF signing operations
 * 
 * This class represents the request parameters for digitally signing a PDF document.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
public class PdfSigningRequest {
    
    /**
     * The PDF file to sign
     */
    @NotNull(message = "PDF file is required")
    private MultipartFile pdfFile;
    
    /**
     * The keystore file containing the signing certificate
     */
    @NotNull(message = "Keystore file is required")
    private MultipartFile keystoreFile;
    
    /**
     * Password for the keystore
     */
    @NotNull(message = "Keystore password is required")
    private String keystorePassword;
    
    /**
     * Alias of the certificate in the keystore
     */
    @NotNull(message = "Certificate alias is required")
    private String certificateAlias;
    
    /**
     * Reason for signing the document
     */
    private String reason;
    
    /**
     * Location where the document was signed
     */
    private String location;
    
    /**
     * Contact information of the signer
     */
    private String contactInfo;
}
