package com.example.pdfboxapi.service;

import com.example.pdfboxapi.model.request.PdfSigningRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

/**
 * Service for PDF signing operations using PDFBox
 * 
 * This service provides methods to digitally sign PDF documents
 * using PDFBox capabilities.
 * 
 * @author Manus
 * @version 1.0
 */
@Service
@Slf4j
public class PdfSigningService {

    /**
     * Signs a PDF document with a digital signature
     * 
     * @param request The PDF signing request containing the PDF file and signing parameters
     * @return Byte array representing the signed PDF document
     * @throws IOException If there's an error processing the PDF
     * @throws KeyStoreException If there's an error with the keystore
     * @throws CertificateException If there's an error with the certificate
     * @throws NoSuchAlgorithmException If the algorithm for keystore isn't available
     * @throws UnrecoverableKeyException If the key cannot be recovered from the keystore
     */
    public byte[] signPdf(PdfSigningRequest request) throws IOException, KeyStoreException, 
            CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        
        MultipartFile pdfFile = request.getPdfFile();
        MultipartFile keystoreFile = request.getKeystoreFile();
        
        File tempPdfFile = convertMultipartFileToFile(pdfFile);
        File tempKeystoreFile = convertMultipartFileToFile(keystoreFile);
        
        try (PDDocument document = Loader.loadPDF(tempPdfFile)) {
            // Load keystore
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(Files.newInputStream(tempKeystoreFile.toPath()), 
                    request.getKeystorePassword().toCharArray());
            
            // Create signature
            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            
            // Set signature metadata
            if (request.getReason() != null && !request.getReason().isEmpty()) {
                signature.setReason(request.getReason());
            }
            
            if (request.getLocation() != null && !request.getLocation().isEmpty()) {
                signature.setLocation(request.getLocation());
            }
            
            if (request.getContactInfo() != null && !request.getContactInfo().isEmpty()) {
                signature.setContactInfo(request.getContactInfo());
            }
            
            // Set signature date
            Calendar signDate = Calendar.getInstance();
            signature.setSignDate(signDate);
            
            // Create signature options
            SignatureOptions signatureOptions = new SignatureOptions();
            signatureOptions.setVisualSignature(document.getPage(0));
            
            // Add signature to document
            document.addSignature(signature, signatureOptions);
            
            // Save the signed document to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            
            log.info("Successfully signed PDF document");
            return baos.toByteArray();
            
        } finally {
            // Clean up temporary files
            if (tempPdfFile != null && tempPdfFile.exists()) {
                tempPdfFile.delete();
            }
            if (tempKeystoreFile != null && tempKeystoreFile.exists()) {
                tempKeystoreFile.delete();
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
        Path tempFile = Files.createTempFile("file-", getFileExtension(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile.toFile();
    }
    
    /**
     * Gets the file extension from a filename
     * 
     * @param filename The filename
     * @return The file extension with dot (e.g., ".pdf")
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return ".tmp";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
