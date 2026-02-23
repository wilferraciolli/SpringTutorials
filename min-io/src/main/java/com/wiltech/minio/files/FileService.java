package com.wiltech.minio.files;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FileService {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    public String uploadFile(final MultipartFile file) throws Exception {
        // create bucket if not exiss
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }

        String fileName = System.currentTimeMillis()
                + "_"
                + file.getOriginalFilename();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .build()
        );

        return "File uploaded successfully: " + fileName;
    }

    public List<String> listAllFiles() throws Exception {
        List<String> filenames = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        for (Result<Item> result : results) {
            filenames.add(result.get().objectName());
        }

        return filenames;
    }

    public String getDownloadLink(final String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .expiry(2, TimeUnit.HOURS) // to expiry in 2 hours
                        .build()
        );
    }

    public void deleteFile(final String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );

        throw new ServerException("Could not find the file", 500, "File not found");
    }

    public boolean doesFileExist(String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            return true;
        } catch (Exception e) {
            return false; // File doesn't exist or other error
        }
    }
}
