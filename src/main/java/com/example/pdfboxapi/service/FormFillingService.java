package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.FormFillingRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Service for PDF form filling operations using PDFBox
 * 
 * This service provides methods to fill form fields in PDF documents
 * using PDFBox capabilities.
 * 
 * @author Manus
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FormFillingService {

    private final ObjectMapper objectMapper;

    /**
     * Fills form fields in a PDF document
     * 
     * @param request The form filling request containing the PDF file and form data
     * @return Byte array representing the filled PDF form
     * @throws IOException If there's an error processing the PDF
     */
    public byte[] fillForm(FormFillingRequest request) throws IOException {
        MultipartFile pdfFile = request.getPdfFile();
        File tempFile = convertMultipartFileToFile(pdfFile);
        
        try (PDDocument document = Loader.loadPDF(tempFile)) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            
            if (acroForm == null) {
                throw new IllegalArgumentException("The provided PDF does not contain a form");
            }
            
            // Parse form data from JSON
            Map<String, String> formData = parseFormData(request.getFormData());
            
            // Fill form fields
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                
                PDField field = acroForm.getField(fieldName);
                if (field != null) {
                    field.setValue(fieldValue);
                    log.debug("Set field '{}' to value '{}'", fieldName, fieldValue);
                } else {
                    log.warn("Field '{}' not found in the form", fieldName);
                }
            }
            
            // Flatten form if requested (makes form fields non-editable)
            if (request.isFlatten()) {
                acroForm.flatten();
                log.debug("Form has been flattened");
            }
            
            // Save the filled form to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            
            log.info("Successfully filled {} form fields", formData.size());
            return baos.toByteArray();
            
        } finally {
            // Clean up the temporary file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
    
    /**
     * Parses form data from JSON string
     * 
     * @param formDataJson JSON string containing form field data
     * @return Map of field names to field values
     * @throws JsonProcessingException If there's an error parsing the JSON
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> parseFormData(String formDataJson) throws JsonProcessingException {
        return objectMapper.readValue(formDataJson, Map.class);
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
