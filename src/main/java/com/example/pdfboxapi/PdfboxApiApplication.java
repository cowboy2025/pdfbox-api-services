package com.example.pdfboxapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for PDFBox API Services
 * 
 * This application provides REST API endpoints for various PDF operations
 * using Apache PDFBox library capabilities.
 * 
 * @author Manus
 * @version 1.0
 */
@SpringBootApplication
public class PdfboxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfboxApiApplication.class, args);
    }
}
