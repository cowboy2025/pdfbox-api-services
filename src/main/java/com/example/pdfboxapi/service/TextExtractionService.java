package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.TextExtractionRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service for text extraction operations using PDFBox
 * 
 * This service provides methods to extract text from PDF documents
 * using various PDFBox capabilities.
 * 
 * @author Manus
 * @version 1.0
 */
@Service
@Slf4j
public class TextExtractionService {

    /**
     * Extracts text from a PDF file
     * 
     * @param request The text extraction request containing the PDF file and extraction options
     * @return Extracted text content
     * @throws IOException If there's an error processing the PDF
     */
    public String extractText(TextExtractionRequest request) throws IOException {
        MultipartFile pdfFile = request.getPdfFile();
        File tempFile = convertMultipartFileToFile(pdfFile);
        
        try (PDDocument document = Loader.loadPDF(tempFile)) {
            PDFTextStripper textStripper = request.isMaintainPositioning() 
                ? new PDFTextStripperByArea() 
                : new PDFTextStripper();
            
            // If a specific page is requested, set the page range
            if (request.getPageNumber() != null) {
                int pageNum = request.getPageNumber();
                if (pageNum > 0 && pageNum <= document.getNumberOfPages()) {
                    textStripper.setStartPage(pageNum);
                    textStripper.setEndPage(pageNum);
                } else {
                    throw new IllegalArgumentException("Invalid page number: " + pageNum);
                }
            }
            
            // Extract text from the document
            String text = textStripper.getText(document);
            log.info("Successfully extracted text from PDF with {} pages", document.getNumberOfPages());
            return text;
        } finally {
            // Clean up the temporary file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
    
    /**
     * Converts a MultipartFile to a File
     * 
     * @param multipartFile The MultipartFile to convert
     * @return A temporary File object
     * @throws IOException If there's an error during conversion
     */
    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        Path tempFile = Files.createTempFile("pdf-", ".pdf");
        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile.toFile();
    }
}
