package com.wiltech.minio.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileRestService {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(fileService.uploadFile(file));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}
