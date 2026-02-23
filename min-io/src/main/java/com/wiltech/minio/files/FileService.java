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
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class FileService {
    public static final String ORIGINAL_FILENAME = "original-filename";
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    public String uploadFile(final MultipartFile file) throws Exception {
        // create bucket if not exists
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }

        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")){
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uuidName = UUID.randomUUID() + extension;

        // Create file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put(ORIGINAL_FILENAME, originalFileName);
        metadata.put("extension", extension);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uuidName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .userMetadata(metadata)
                        .build()
        );

        return "File uploaded successfully: " + uuidName;
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

    public String getDownloadLink(final String uuidName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String originalName = getOriginalFileName(uuidName);

        Map<String, String> reqParams = new HashMap<>();
        // This tells the browser: "Save this as 'original name.jpg' even though the URL is a UUID"
        reqParams.put("response-content-disposition", "attachment; filename=\"" + originalName + "\"");

        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(uuidName)
                        .expiry(2, TimeUnit.HOURS) // to expiry in 2 hours
                        .extraQueryParams(reqParams)
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

    public String getOriginalFileName(final String uuidName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uuidName)
                        .build()
        );

        // MinIO prefixes user metadata with "X-Amz-Meta-", so make sure to manage it
    return stat.userMetadata().get(ORIGINAL_FILENAME);
    }
}
