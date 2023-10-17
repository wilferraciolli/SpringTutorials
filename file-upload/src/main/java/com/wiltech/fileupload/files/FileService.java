package com.wiltech.fileupload.files;

import com.wiltech.fileupload.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    public File store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        File fileToCreate = File.builder()
                .name(fileName)
                .type(file.getContentType())
                .data(ImageUtils.compressImage(file.getBytes()))
                .build();

        return fileRepository.save(fileToCreate);
    }

    public Optional<File> getFile(String id) {
        // decompress the image
        return fileRepository.findById(id)
                .map(file -> File.builder()
                        .id(file.getId())
                        .name(file.getName())
                        .type(file.getType())
                        .data(ImageUtils.decompressImage(file.getData()))
                        .build());
    }

    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }
}
