package com.example.uploadingfiles;

import com.example.uploadingfiles.exceptions.MinioFetchException;
import com.example.uploadingfiles.exceptions.StorageException;
import com.example.uploadingfiles.properties.StorageProperties;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FileSystemStorageService{

    private final Path rootLocation;
    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000/")
                .credentials(
                        "AKIAIOSFODNN7EXAMPLE",
                        "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")
                .build();
        this.bucketName = "asiatrip";
    }

	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
			}
			Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename())
                            .filename(this.rootLocation.resolve(file.getOriginalFilename()).toString())
                            .build());
		} catch (IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
			throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
		}
	}

	public InputStream get(Path path) throws Exception {
		return minioClient.getObject(
				GetObjectArgs.builder()
						.bucket(bucketName)
						.object(path.toString())
						.build());
	}

	public List<String> fullList() {
		try {
			Iterable<Result<Item>> myObjects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
			return getItems(myObjects);
		} catch (Exception e) {
			throw e;
		}
	}

	private List<String> getItems(Iterable<Result<Item>> myObjects) {
		return StreamSupport
				.stream(myObjects.spliterator(), true)
				.map(itemResult -> {
					try {
						return itemResult.get().objectName();
					} catch (Exception e) {
						throw new MinioFetchException("Error while parsing list of objects", e);
					}
				})
				.collect(Collectors.toList());
	}
}
