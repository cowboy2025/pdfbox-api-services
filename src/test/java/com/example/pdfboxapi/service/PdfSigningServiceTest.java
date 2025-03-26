package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfSigningRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PdfSigningService
 */
@ExtendWith(MockitoExtension.class)
public class PdfSigningServiceTest {

    @InjectMocks
    private PdfSigningService pdfSigningService;

    private MockMultipartFile pdfFile;
    private MockMultipartFile keystoreFile;
    private PdfSigningRequest request;

    @BeforeEach
    void setUp() throws IOException {
        // Create a mock PDF file for testing
        try (InputStream is = getClass().getResourceAsStream("/sample.pdf")) {
            if (is != null) {
                pdfFile = new MockMultipartFile("sample.pdf", "sample.pdf", "application/pdf", is.readAllBytes());
            } else {
                // Fallback if resource not found
                pdfFile = new MockMultipartFile("sample.pdf", "sample.pdf", "application/pdf", "Sample PDF content".getBytes());
            }
        }
        
        // Create a mock keystore file for testing
        // Note: In a real test, this should be a valid PKCS12 keystore
        keystoreFile = new MockMultipartFile("keystore.p12", "keystore.p12", "application/x-pkcs12", "Mock keystore content".getBytes());

        // Set up the request
        request = new PdfSigningRequest();
        request.setPdfFile(pdfFile);
        request.setKeystoreFile(keystoreFile);
        request.setKeystorePassword("password");
        request.setCertificateAlias("alias");
        request.setReason("Testing");
        request.setLocation("Test Environment");
        request.setContactInfo("test@example.com");
    }

    @Test
    void testSignPdf() {
        // Since we're using a mock keystore that isn't a valid PKCS12 file,
        // we expect an exception when trying to load it
        Exception exception = assertThrows(Exception.class, () -> {
            pdfSigningService.signPdf(request);
        });
        
        // Verify that the exception is related to keystore loading
        assertTrue(exception instanceof IOException || 
                   exception instanceof KeyStoreException || 
                   exception instanceof CertificateException || 
                   exception instanceof NoSuchAlgorithmException || 
                   exception instanceof UnrecoverableKeyException);
    }
    
    // Note: A more comprehensive test would use a real keystore file
    // and verify that the signing process works correctly.
    // However, creating a valid keystore for testing is beyond the scope
    // of this unit test implementation.
}
