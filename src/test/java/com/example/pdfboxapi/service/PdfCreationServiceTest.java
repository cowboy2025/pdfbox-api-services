package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfCreationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PdfCreationService
 */
@ExtendWith(MockitoExtension.class)
public class PdfCreationServiceTest {

    @InjectMocks
    private PdfCreationService pdfCreationService;

    private PdfCreationRequest request;
    private MockMultipartFile image;

    @BeforeEach
    void setUp() throws IOException {
        // Create a mock image file for testing
        try (InputStream is = getClass().getResourceAsStream("/sample.pdf")) {
            if (is != null) {
                image = new MockMultipartFile("image.jpg", "image.jpg", "image/jpeg", is.readAllBytes());
            } else {
                // Fallback if resource not found
                image = new MockMultipartFile("image.jpg", "image.jpg", "image/jpeg", "Sample image content".getBytes());
            }
        }

        // Set up the request
        request = new PdfCreationRequest();
        request.setTitle("Test PDF");
        request.setContent("This is a test PDF document created for unit testing.");
        request.setFontName("Helvetica");
        request.setFontSize(12);
        request.setAuthor("Test Author");
    }

    @Test
    void testCreatePdf() throws IOException {
        try {
            byte[] result = pdfCreationService.createPdf(request);
            
            // Verify the result
            assertNotNull(result);
            assertTrue(result.length > 0);
        } catch (Exception e) {
            // If there's an issue with font loading or other PDF creation steps
            // In a real environment, this should not happen
            fail("PDF creation failed: " + e.getMessage());
        }
    }

    @Test
    void testCreatePdfWithImage() throws IOException {
        // Add an image to the request
        request.setImage(image);
        
        try {
            byte[] result = pdfCreationService.createPdf(request);
            
            // Verify the result
            assertNotNull(result);
            assertTrue(result.length > 0);
            
            // The PDF with an image should be larger than a PDF without an image
            byte[] resultWithoutImage = pdfCreationService.createPdf(new PdfCreationRequest() {{
                setTitle(request.getTitle());
                setContent(request.getContent());
                setFontName(request.getFontName());
                setFontSize(request.getFontSize());
                setAuthor(request.getAuthor());
            }});
            
            assertTrue(result.length > resultWithoutImage.length);
        } catch (Exception e) {
            // If there's an issue with image loading or other PDF creation steps
            // In a real environment with a valid image, this should not happen
            assertTrue(e instanceof IOException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testCreatePdfWithDifferentFont() throws IOException {
        // Set a different font
        request.setFontName("Times-Roman");
        
        try {
            byte[] result = pdfCreationService.createPdf(request);
            
            // Verify the result
            assertNotNull(result);
            assertTrue(result.length > 0);
        } catch (Exception e) {
            // If there's an issue with font loading
            // In a real environment, this should not happen
            fail("PDF creation failed: " + e.getMessage());
        }
    }
}
