package com.apiGenerator.controller;

import com.apiGenerator.model.ApiSpecification;
import com.apiGenerator.service.CodeGeneratorService;
import com.apiGenerator.validator.JsonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Map;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/generator")
public class ApiGeneratorController {

    private final JsonValidator validator;
    private final CodeGeneratorService codeGeneratorService;

    @Autowired
    public ApiGeneratorController(
            JsonValidator validator,
            CodeGeneratorService codeGeneratorService
    ) {
        this.validator = validator;
        this.codeGeneratorService = codeGeneratorService;
    }

    // Validate API Specification
    @PostMapping("/validate")
    public ResponseEntity<?> validateApiSpec(@RequestBody ApiSpecification spec) {
        List<String> errors = validator.validate(spec);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok("Valid API specification");
    }

    // Generate API Code
    @PostMapping("/generate")
    public ResponseEntity<?> generateApiCode(@RequestBody Map<String, Object> request) {
        try {
            ApiSpecification spec = (ApiSpecification) request.get("apiSpecification");
            List<String> languages = (List<String>) request.get("languages");
            // Validate the API specification
            if (!validator.validate(spec).isEmpty()) {
                return ResponseEntity.badRequest().body(validator.validate((spec)));
            }

            // Generate code for selected languages
            Map<String, String> generatedCode = codeGeneratorService.generateCode(spec,languages);

            return ResponseEntity.ok(generatedCode);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new String[]{e.getMessage()});
        }
    }

    // Generate and Download API Code
    @PostMapping("/download")
    public ResponseEntity<?> downloadApiCode(@RequestBody Map<String, Object> request) {
        try {
            ApiSpecification spec = (ApiSpecification) request.get("apiSpecification");
            System.out.println(spec);
            List<String> languages = (List<String>) request.get("languages");
            // Validate the API specification
            if (!validator.validate(spec).isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid API specification");
            }

            // Generate code for selected languages
            Map<String,String> generatedCode = null;
            try {
                generatedCode = codeGeneratorService.generateCode(spec, languages);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            // Create a zip file with the generated code
            File zipFile = createZipFile(generatedCode, spec.getBaseClassName());

            // Return the zip file as a downloadable response
            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + spec.getBaseClassName() + "_clients.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error creating zip file: " + e.getMessage());
        }
    }

    private File createZipFile(Map<String, String> generatedCode, String serviceName) throws IOException {
        // Create a temporary file for the zip
        File zipFile = File.createTempFile(serviceName + "_clients", ".zip");

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (Map.Entry<String, String> entry : generatedCode.entrySet()) {
                // Create a new zip entry for each generated file
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zipOut.putNextEntry(zipEntry);

                // Write the file content into the zip
                zipOut.write(entry.getValue().getBytes());
                zipOut.closeEntry();
            }
        }

        return zipFile;
    }
}
