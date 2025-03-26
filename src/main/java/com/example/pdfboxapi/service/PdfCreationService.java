package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfCreationRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for PDF creation operations using PDFBox
 * 
 * This service provides methods to create new PDF documents
 * using PDFBox capabilities.
 * 
 * @author Manus
 * @version 1.0
 */
@Service
@Slf4j
public class PdfCreationService {

    private static final float MARGIN = 50;
    private static final float LEADING = 1.5f;

    /**
     * Creates a new PDF document
     * 
     * @param request The PDF creation request containing content and formatting options
     * @return Byte array representing the created PDF document
     * @throws IOException If there's an error creating the PDF
     */
    public byte[] createPdf(PdfCreationRequest request) throws IOException {
        File tempImageFile = null;
        
        try (PDDocument document = new PDDocument()) {
            // Set document metadata
            document.getDocumentInformation().setTitle(request.getTitle());
            document.getDocumentInformation().setAuthor(request.getAuthor());
            
            // Create a page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            // Select font
            PDFont font = getFont(request.getFontName());
            float fontSize = request.getFontSize();
            
            // Calculate text width and height
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float textWidth = pageWidth - 2 * MARGIN;
            
            // Split content into lines that fit within the page width
            List<String> lines = splitTextIntoLines(request.getContent(), font, fontSize, textWidth);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set font
                contentStream.setFont(font, fontSize);
                
                // Add title
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, pageHeight - MARGIN);
                contentStream.setFont(font, fontSize + 4); // Larger font for title
                contentStream.showText(request.getTitle());
                contentStream.endText();
                
                // Add content
                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.newLineAtOffset(MARGIN, pageHeight - MARGIN - (fontSize + 4) * 2); // Position after title
                
                float leading = fontSize * LEADING;
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leading);
                }
                contentStream.endText();
                
                // Add image if provided
                if (request.getImage() != null && !request.getImage().isEmpty()) {
                    tempImageFile = convertMultipartFileToFile(request.getImage());
                    PDImageXObject image = PDImageXObject.createFromFileByContent(tempImageFile, document);
                    
                    // Scale image to fit within page width while maintaining aspect ratio
                    float imageWidth = Math.min(textWidth, image.getWidth());
                    float scale = imageWidth / image.getWidth();
                    float imageHeight = image.getHeight() * scale;
                    
                    // Position image at the bottom of the page
                    float imageX = MARGIN;
                    float imageY = MARGIN;
                    
                    contentStream.drawImage(image, imageX, imageY, imageWidth, imageHeight);
                }
            }
            
            // Save the document to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            
            log.info("Successfully created PDF document with title: {}", request.getTitle());
            return baos.toByteArray();
            
        } finally {
            // Clean up the temporary image file
            if (tempImageFile != null && tempImageFile.exists()) {
                tempImageFile.delete();
            }
        }
    }
    
    /**
     * Gets a PDFont object based on the font name
     * 
     * @param fontName Name of the font
     * @return PDFont object
     */
    private PDFont getFont(String fontName) {
        // Map common font names to Standard14Fonts
        switch (fontName.toLowerCase()) {
            case "helvetica":
                return new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            case "helvetica-bold":
                return new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            case "times-roman":
                return new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
            case "times-bold":
                return new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
            case "courier":
                return new PDType1Font(Standard14Fonts.FontName.COURIER);
            case "courier-bold":
                return new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
            default:
                // Default to Helvetica if font name is not recognized
                return new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        }
    }
    
    /**
     * Splits text into lines that fit within the specified width
     * 
     * @param text Text to split
     * @param font Font to use
     * @param fontSize Font size
     * @param maxWidth Maximum width for each line
     * @return List of text lines
     * @throws IOException If there's an error calculating text width
     */
    private List<String> splitTextIntoLines(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] paragraphs = text.split("\n");
        
        for (String paragraph : paragraphs) {
            String[] words = paragraph.split(" ");
            StringBuilder line = new StringBuilder();
            
            for (String word : words) {
                String testLine = line.toString();
                if (!testLine.isEmpty()) {
                    testLine += " ";
                }
                testLine += word;
                
                float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;
                
                if (textWidth > maxWidth) {
                    // If the line is too long, add the current line to the list
                    // and start a new line with the current word
                    if (!line.toString().isEmpty()) {
                        lines.add(line.toString());
                        line = new StringBuilder(word);
                    } else {
                        // If a single word is too long, split it
                        lines.add(word);
                        line = new StringBuilder();
                    }
                } else {
                    // Add the word to the current line
                    if (!line.toString().isEmpty()) {
                        line.append(" ");
                    }
                    line.append(word);
                }
            }
            
            // Add the last line of the paragraph
            if (!line.toString().isEmpty()) {
                lines.add(line.toString());
            }
            
            // Add an empty line between paragraphs
            if (paragraphs.length > 1) {
                lines.add("");
            }
        }
        
        return lines;
    }
    
    /**
     * Converts a MultipartFile to a File
     * 
     * @param multipartFile The MultipartFile to convert
     * @return A temporary File object
     * @throws IOException If there's an error during conversion
     */
    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        Path tempFile = Files.createTempFile("img-", getFileExtension(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile.toFile();
    }
    
    /**
     * Gets the file extension from a filename
     * 
     * @param filename The filename
     * @return The file extension with dot (e.g., ".jpg")
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return ".tmp";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
