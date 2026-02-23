package com.wiltech.minio.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/all")
    public ResponseEntity<List<String>> listFiles() {
        try {
            return ResponseEntity.ok(fileService.listAllFiles());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<String> getDownloadLink(@PathVariable String fileName) {
        try {
            if (!fileService.doesFileExist(fileName)) {
                return ResponseEntity.status(404).body("File not found: " + fileName);
            }

            String url = fileService.getDownloadLink(fileName);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error generating link: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> delete(@PathVariable String fileName) {
        try {
            if (!fileService.doesFileExist(fileName)) {
                return ResponseEntity.status(404).body("File not found: " + fileName);
            }

            fileService.deleteFile(fileName);
            return ResponseEntity.ok("File '" + fileName + "' deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Delete failed: " + e.getMessage());
        }
    }
}
