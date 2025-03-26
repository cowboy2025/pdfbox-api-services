package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfSplitRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for PDF splitting operations using PDFBox
 * 
 * This service provides methods to split PDF documents into multiple files
 * using various PDFBox capabilities.
 * 
 * @author Manus
 * @version 1.0
 */
@Service
@Slf4j
public class PdfSplitService {

    /**
     * Splits a PDF file according to the specified request parameters
     * 
     * @param request The PDF split request containing the PDF file and split options
     * @return List of byte arrays representing the split PDF files
     * @throws IOException If there's an error processing the PDF
     */
    public List<byte[]> splitPdf(PdfSplitRequest request) throws IOException {
        MultipartFile pdfFile = request.getPdfFile();
        File tempFile = convertMultipartFileToFile(pdfFile);
        List<byte[]> splitPdfs = new ArrayList<>();
        
        try (PDDocument document = Loader.loadPDF(tempFile)) {
            if (request.getSplitType() == PdfSplitRequest.SplitType.BY_PAGE) {
                splitPdfs = splitByPage(document);
            } else if (request.getSplitType() == PdfSplitRequest.SplitType.BY_RANGE) {
                splitPdfs = splitByRange(document, request.getPageRanges());
            }
            
            log.info("Successfully split PDF into {} parts", splitPdfs.size());
            return splitPdfs;
        } finally {
            // Clean up the temporary file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
    
    /**
     * Splits a PDF document into individual pages
     * 
     * @param document The PDF document to split
     * @return List of byte arrays representing individual pages as PDFs
     * @throws IOException If there's an error during splitting
     */
    private List<byte[]> splitByPage(PDDocument document) throws IOException {
        List<byte[]> splitPdfs = new ArrayList<>();
        Splitter splitter = new Splitter();
        
        List<PDDocument> pages = splitter.split(document);
        for (PDDocument page : pages) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                page.save(baos);
                splitPdfs.add(baos.toByteArray());
                page.close();
            }
        }
        
        return splitPdfs;
    }
    
    /**
     * Splits a PDF document according to specified page ranges
     * 
     * @param document The PDF document to split
     * @param pageRanges String representing page ranges (e.g., "1-3,5,7-10")
     * @return List of byte arrays representing the split PDFs
     * @throws IOException If there's an error during splitting
     */
    private List<byte[]> splitByRange(PDDocument document, String pageRanges) throws IOException {
        List<byte[]> splitPdfs = new ArrayList<>();
        
        if (pageRanges == null || pageRanges.trim().isEmpty()) {
            throw new IllegalArgumentException("Page ranges must be specified for BY_RANGE split type");
        }
        
        // Parse page ranges
        List<PageRange> ranges = parsePageRanges(pageRanges, document.getNumberOfPages());
        
        // Create a new PDF for each range
        for (PageRange range : ranges) {
            try (PDDocument newDoc = new PDDocument()) {
                for (int i = range.getStart(); i <= range.getEnd(); i++) {
                    // PDFBox page indices are 0-based, but user input is 1-based
                    newDoc.addPage(document.getPage(i - 1));
                }
                
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    newDoc.save(baos);
                    splitPdfs.add(baos.toByteArray());
                }
            }
        }
        
        return splitPdfs;
    }
    
    /**
     * Parses a string of page ranges into a list of PageRange objects
     * 
     * @param pageRanges String representing page ranges (e.g., "1-3,5,7-10")
     * @param maxPages Maximum number of pages in the document
     * @return List of PageRange objects
     */
    private List<PageRange> parsePageRanges(String pageRanges, int maxPages) {
        List<PageRange> ranges = new ArrayList<>();
        String[] parts = pageRanges.split(",");
        
        Pattern rangePattern = Pattern.compile("(\\d+)(?:-(\\d+))?");
        
        for (String part : parts) {
            part = part.trim();
            Matcher matcher = rangePattern.matcher(part);
            
            if (matcher.matches()) {
                int start = Integer.parseInt(matcher.group(1));
                
                // If there's a second group, it's a range (e.g., "1-3")
                // Otherwise, it's a single page (e.g., "5")
                int end = matcher.group(2) != null 
                        ? Integer.parseInt(matcher.group(2)) 
                        : start;
                
                // Validate page numbers
                if (start < 1 || end > maxPages || start > end) {
                    throw new IllegalArgumentException(
                            "Invalid page range: " + part + ". Valid range is 1-" + maxPages);
                }
                
                ranges.add(new PageRange(start, end));
            } else {
                throw new IllegalArgumentException("Invalid page range format: " + part);
            }
        }
        
        return ranges;
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
    
    /**
     * Inner class representing a range of pages
     */
    private static class PageRange {
        private final int start;
        private final int end;
        
        public PageRange(int start, int end) {
            this.start = start;
            this.end = end;
        }
        
        public int getStart() {
            return start;
        }
        
        public int getEnd() {
            return end;
        }
    }
}
