# PDFBox API Service

A Spring Boot application that provides REST API services for PDF operations using Apache PDFBox.

## Features

This API service provides the following PDF operations:

1. **Text Extraction** - Extract text from PDF files with options for maintaining positioning and extracting from specific pages
2. **PDF Splitting** - Split PDFs into individual pages or by specified page ranges
3. **PDF Merging** - Combine multiple PDF files into a single document
4. **Form Filling** - Fill form fields in PDF documents with provided data
5. **PDF to Image Conversion** - Convert PDF pages to images with specified format and DPI
6. **PDF Validation (Preflight)** - Validate PDF files against PDF/A standards
7. **PDF Creation** - Create new PDF documents with text content and optional images
8. **PDF Signing** - Digitally sign PDF documents using certificates from a keystore

## Technology Stack

- Java 17
- Spring Boot 3.2.3
- Apache PDFBox 3.0.4
- SpringDoc OpenAPI 2.3.0
- Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Building the Application

```bash
mvn clean package
```

### Running the Application

```bash
java -jar target/pdfbox-api-0.0.1-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

The application will start on port 8080 by default.

## API Documentation

Once the application is running, you can access the API documentation at:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## API Endpoints

### Text Extraction

```
POST /api/v1/extract/text
```

Extracts text from a PDF file with options to maintain positioning and extract from specific pages.

### PDF Splitting

```
POST /api/v1/split/pdf
```

Splits a PDF file into multiple PDFs based on specified criteria and returns them as a ZIP file.

### PDF Merging

```
POST /api/v1/merge/pdf
```

Combines multiple PDF files into a single PDF document.

### Form Filling

```
POST /api/v1/forms/fill
```

Fills form fields in a PDF document with provided data.

### PDF to Image Conversion

```
POST /api/v1/convert/to-image
```

Converts PDF pages to images with specified format and DPI.

### PDF Validation

```
POST /api/v1/validate/pdf
```

Validates a PDF file against PDF/A standards and returns validation results.

### PDF Creation

```
POST /api/v1/create/pdf
```

Creates a new PDF document with specified content and formatting options.

### PDF Signing

```
POST /api/v1/sign/pdf
```

Digitally signs a PDF document using a certificate from a keystore.

## Configuration

The application can be configured using the `application.properties` file. Key configuration options include:

- `server.port`: The port the server will run on (default: 8080)
- `spring.servlet.multipart.max-file-size`: Maximum file size for uploads (default: 10MB)
- `spring.servlet.multipart.max-request-size`: Maximum request size (default: 10MB)
- `app.upload.dir`: Directory for temporary file storage (default: temp-files)

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
