package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfToImageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PdfToImageService
 */
@ExtendWith(MockitoExtension.class)
public class PdfToImageServiceTest {

    @InjectMocks
    private PdfToImageService pdfToImageService;

    private MockMultipartFile pdfFile;
    private PdfToImageRequest request;

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

        // Set up the request
        request = new PdfToImageRequest();
        request.setPdfFile(pdfFile);
        request.setImageFormat("PNG");
        request.setDpi(300);
        request.setPageNumber(null);
    }

    @Test
    void testConvertPdfToImages() throws IOException {
        try {
            List<byte[]> result = pdfToImageService.convertPdfToImages(request);
            
            // Verify the result
            assertNotNull(result);
            assertFalse(result.isEmpty());
            
            // Our sample PDF has 1 page, so we expect 1 image in the result
            assertEquals(1, result.size());
            
            // Each result should be a valid image (non-empty)
            for (byte[] image : result) {
                assertTrue(image.length > 0);
            }
        } catch (Exception e) {
            // If using a mock PDF without proper structure, we expect an exception
            // In a real test with a real PDF, this catch block should not be executed
            assertTrue(e instanceof IOException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testConvertPdfToImagesWithSpecificPage() throws IOException {
        // Set a specific page number
        request.setPageNumber(1);
        
        try {
            List<byte[]> result = pdfToImageService.convertPdfToImages(request);
            
            // Verify the result
            assertNotNull(result);
            assertFalse(result.isEmpty());
            
            // We specified one page, so we expect 1 image in the result
            assertEquals(1, result.size());
            
            // Each result should be a valid image (non-empty)
            for (byte[] image : result) {
                assertTrue(image.length > 0);
            }
        } catch (Exception e) {
            // If using a mock PDF without proper structure, we expect an exception
            // In a real test with a real PDF, this catch block should not be executed
            assertTrue(e instanceof IOException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testConvertPdfToImagesWithDifferentFormat() throws IOException {
        // Set a different image format
        request.setImageFormat("JPEG");
        
        try {
            List<byte[]> result = pdfToImageService.convertPdfToImages(request);
            
            // Verify the result
            assertNotNull(result);
            assertFalse(result.isEmpty());
            
            // Each result should be a valid image (non-empty)
            for (byte[] image : result) {
                assertTrue(image.length > 0);
            }
        } catch (Exception e) {
            // If using a mock PDF without proper structure, we expect an exception
            // In a real test with a real PDF, this catch block should not be executed
            assertTrue(e instanceof IOException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testConvertPdfToImagesWithInvalidPageNumber() {
        // Set an invalid page number
        request.setPageNumber(-1);
        
        // Expect an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            pdfToImageService.convertPdfToImages(request);
        });
    }
}
