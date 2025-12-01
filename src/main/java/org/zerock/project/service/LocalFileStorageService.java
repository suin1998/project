package org.zerock.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir; // application.properties에서 설정

    @Value("${app.upload.url}")
    private String uploadUrl; // 이미지 접근용 URL prefix

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        // UUID 기반 파일명 생성 (중복 방지)
        String originalFilename = file.getOriginalFilename();
        String ext = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + ext;

        // 저장 경로 생성
        Path targetPath = Paths.get(uploadDir).resolve(filename);
        Files.createDirectories(targetPath.getParent()); // 상위 폴더 없으면 생성

        // 파일 저장
        file.transferTo(targetPath.toFile());

        // URL 반환
        return uploadUrl + "/" + filename;
    }
}
