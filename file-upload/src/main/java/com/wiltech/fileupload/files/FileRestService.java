package com.wiltech.fileupload.files;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/files")
public class FileRestService {
    @Autowired
    private FileService storageService;

    @PostMapping("")
    public ResponseEntity<ResponseFile> uploadFile(@RequestParam("file") MultipartFile file) {

        try {
            File store = storageService.store(file);

            ResponseFile responseFile = new ResponseFile(
                    store.getId(),
                    store.getName(),
                    null,
                    store.getType(),
                    store.getData().length);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseFile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @GetMapping("")
    public ResponseEntity<List<ResponseFile>> getListFiles() {
        List<ResponseFile> files = storageService.getAllFiles().stream()
                .map(dbFile -> {
                    String fileDownloadUri = ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path("/files/")
                            .path(dbFile.getId())
                            .toUriString();

                    return new ResponseFile(
                            dbFile.getId(),
                            dbFile.getName(),
                            fileDownloadUri,
                            dbFile.getType(),
                            dbFile.getData().length);
                }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        File fileDB = storageService.getFile(id).get();

//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
//                .body(fileDB.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(fileDB.getType()))
                .body(fileDB.getData());
    }
}
