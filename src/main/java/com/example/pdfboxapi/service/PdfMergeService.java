package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfMergeRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for PDF merging operations using PDFBox
 * 
 * This service provides methods to merge multiple PDF documents into a single file
 * using PDFBox capabilities.
 * 
 * @author Manus
 * @version 1.0
 */
@Service
@Slf4j
public class PdfMergeService {

    /**
     * Merges multiple PDF files into a single PDF document
     * 
     * @param request The PDF merge request containing the PDF files to merge
     * @return Byte array representing the merged PDF file
     * @throws IOException If there's an error processing the PDFs
     */
    public byte[] mergePdfs(PdfMergeRequest request) throws IOException {
        MultipartFile[] pdfFiles = request.getPdfFiles();
        
        if (pdfFiles == null || pdfFiles.length < 2) {
            throw new IllegalArgumentException("At least two PDF files are required for merging");
        }
        
        List<File> tempFiles = new ArrayList<>();
        try {
            // Convert MultipartFiles to temporary Files
            for (MultipartFile pdfFile : pdfFiles) {
                tempFiles.add(convertMultipartFileToFile(pdfFile));
            }
            
            // Create PDF merger utility
            PDFMergerUtility merger = new PDFMergerUtility();
            
            // Add source files to merger
            for (File tempFile : tempFiles) {
                merger.addSource(tempFile);
            }
            
            // Set up output stream for merged PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            merger.setDestinationStream(outputStream);
            
            // Merge the PDFs
            merger.mergeDocuments(null);
            
            log.info("Successfully merged {} PDF files", pdfFiles.length);
            return outputStream.toByteArray();
            
        } finally {
            // Clean up temporary files
            for (File tempFile : tempFiles) {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
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
