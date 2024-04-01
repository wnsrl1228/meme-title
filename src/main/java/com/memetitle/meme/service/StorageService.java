package com.memetitle.meme.service;

import com.memetitle.global.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import static com.memetitle.global.exception.ErrorCode.*;

/**
 * TODO : 이미지 저장 방식에 대해서는 추후 수정할 예정
 *      방안 1. awsS3
 *      방안 2. 프론트 서버에 저장
 */
@Service
public class StorageService {

    private final Path rootLocation;

    public StorageService(
            @Value("${storage.upload-dir}") final String location
    ) {
        this.rootLocation = Paths.get(location);
    }

    public String store(final MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException(EMPTY_FILE_STORE_FAILED);
            }

            // ../2024-11-11/filename.jpg
            Path todayFolder = rootLocation.resolve(LocalDate.now().toString());
            final Path destinationFile = todayFolder.resolve(file.getOriginalFilename())
                    .normalize()
                    .toAbsolutePath();

            // 오늘 날짜의 폴더가 없으면 생성
            try {
                if (!Files.exists(todayFolder)) {
                    Files.createDirectories(todayFolder);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!destinationFile.getParent().equals(todayFolder.toAbsolutePath())) {
                throw new StorageException(OUTSIDE_CURRENT_DIRECTORY);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return destinationFile.toString();
        }
        catch (IOException e) {
            throw new StorageException(FAILED_TO_STORE_FILE);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageException(NOT_FOUND_IMAGE);
            }
        }
        catch (MalformedURLException e) {
            throw new StorageException(NOT_FOUND_IMAGE);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
