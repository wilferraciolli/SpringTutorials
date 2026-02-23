package com.wiltech.minio.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileRestService {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "path", defaultValue = "") String path) {
        try {
            return ResponseEntity.ok(fileService.uploadFile(file, path));
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

    @GetMapping("/allinpath")
    public ResponseEntity<List<String>> listFilesInPAth(
            @RequestParam(value = "path", defaultValue = "") String path) {
        try {
            return ResponseEntity.ok(fileService.listFilesByPath(path));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<String> getDownloadLink(
            @PathVariable String fileName) {
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

    @GetMapping("/{uuidName}/stat")
    public ResponseEntity<FileStatDTO> getFileStat(
            @PathVariable String uuidName) {
        try {
            if (!fileService.doesFileExist(uuidName)) {
                return ResponseEntity.status(404)
                        .body(new FileStatDTO("File not found: " + uuidName, 0, null, Map.of()));
            }

            FileStatDTO dto = fileService.getFileStat(uuidName);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new FileStatDTO(e.getMessage(), 0, null, Map.of()) );
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
