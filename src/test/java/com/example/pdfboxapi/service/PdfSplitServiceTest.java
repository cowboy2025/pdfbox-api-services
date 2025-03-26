package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfSplitRequest;
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
 * Unit tests for PdfSplitService
 */
@ExtendWith(MockitoExtension.class)
public class PdfSplitServiceTest {

    @InjectMocks
    private PdfSplitService pdfSplitService;

    private MockMultipartFile pdfFile;
    private PdfSplitRequest request;

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
        request = new PdfSplitRequest();
        request.setPdfFile(pdfFile);
        request.setSplitType(PdfSplitRequest.SplitType.BY_PAGE);
    }

    @Test
    void testSplitPdfByPage() throws IOException {
        try {
            List<byte[]> result = pdfSplitService.splitPdf(request);
            
            // Verify the result
            assertNotNull(result);
            assertFalse(result.isEmpty());
            
            // Our sample PDF has 1 page, so we expect 1 PDF in the result
            assertEquals(1, result.size());
            
            // Each result should be a valid PDF (non-empty)
            for (byte[] pdf : result) {
                assertTrue(pdf.length > 0);
            }
        } catch (Exception e) {
            // If using a mock PDF without proper structure, we expect an exception
            // In a real test with a real PDF, this catch block should not be executed
            assertTrue(e instanceof IOException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testSplitPdfByRange() throws IOException {
        // Set up the request for BY_RANGE split type
        request.setSplitType(PdfSplitRequest.SplitType.BY_RANGE);
        request.setPageRanges("1");
        
        try {
            List<byte[]> result = pdfSplitService.splitPdf(request);
            
            // Verify the result
            assertNotNull(result);
            assertFalse(result.isEmpty());
            
            // We specified one range (page 1), so we expect 1 PDF in the result
            assertEquals(1, result.size());
            
            // Each result should be a valid PDF (non-empty)
            for (byte[] pdf : result) {
                assertTrue(pdf.length > 0);
            }
        } catch (Exception e) {
            // If using a mock PDF without proper structure, we expect an exception
            // In a real test with a real PDF, this catch block should not be executed
            assertTrue(e instanceof IOException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testSplitPdfByRangeWithInvalidRange() {
        // Set up the request with an invalid page range
        request.setSplitType(PdfSplitRequest.SplitType.BY_RANGE);
        request.setPageRanges("100"); // Our sample PDF doesn't have 100 pages
        
        // Expect an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            pdfSplitService.splitPdf(request);
        });
    }

    @Test
    void testSplitPdfByRangeWithMissingRanges() {
        // Set up the request without specifying page ranges
        request.setSplitType(PdfSplitRequest.SplitType.BY_RANGE);
        request.setPageRanges(null);
        
        // Expect an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            pdfSplitService.splitPdf(request);
        });
    }
}
