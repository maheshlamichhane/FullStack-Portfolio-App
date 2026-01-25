package com.portfolio.app.controller;

import com.portfolio.app.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "File upload and management APIs")
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a file")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String category) {

        String fileName = fileStorageService.store(file, category);

        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("fileType", file.getContentType());
        response.put("size", String.valueOf(file.getSize()));
        response.put("category", category);
        response.put("message", "File uploaded successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{filename:.+}")
    @Operation(summary = "Download a file")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource resource = fileStorageService.loadAsResource(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/view/{filename:.+}")
    @Operation(summary = "View a file (for images)")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) {
        Resource resource = fileStorageService.loadAsResource(filename);

        String contentType = determineContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @DeleteMapping("/{filename:.+}")
    @Operation(summary = "Delete a file")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String filename) {
        fileStorageService.delete(filename);

        Map<String, String> response = new HashMap<>();
        response.put("message", "File deleted successfully");
        response.put("fileName", filename);

        return ResponseEntity.ok(response);
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            default -> "application/octet-stream";
        };
    }
}
