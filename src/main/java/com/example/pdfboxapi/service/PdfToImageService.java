package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfToImageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for PDF to image conversion operations using PDFBox
 * 
 * This service provides methods to convert PDF pages to images
 * using PDFBox capabilities.
 * 
 * @author Manus
 * @version 1.0
 */
@Service
@Slf4j
public class PdfToImageService {

    /**
     * Converts PDF pages to images
     * 
     * @param request The PDF to image request containing the PDF file and conversion options
     * @return List of byte arrays representing the converted images
     * @throws IOException If there's an error processing the PDF
     */
    public List<byte[]> convertPdfToImages(PdfToImageRequest request) throws IOException {
        MultipartFile pdfFile = request.getPdfFile();
        File tempFile = convertMultipartFileToFile(pdfFile);
        List<byte[]> images = new ArrayList<>();
        
        try (PDDocument document = Loader.loadPDF(tempFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            
            // Determine which pages to convert
            int startPage = 0;
            int endPage = document.getNumberOfPages() - 1;
            
            if (request.getPageNumber() != null) {
                int pageNum = request.getPageNumber() - 1; // Convert to 0-based index
                if (pageNum >= 0 && pageNum < document.getNumberOfPages()) {
                    startPage = pageNum;
                    endPage = pageNum;
                } else {
                    throw new IllegalArgumentException("Invalid page number: " + (pageNum + 1));
                }
            }
            
            // Convert pages to images
            for (int i = startPage; i <= endPage; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, request.getDpi(), ImageType.RGB);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, request.getImageFormat(), baos);
                images.add(baos.toByteArray());
                
                log.debug("Converted page {} to {} image", i + 1, request.getImageFormat());
            }
            
            log.info("Successfully converted {} pages to images", images.size());
            return images;
            
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
