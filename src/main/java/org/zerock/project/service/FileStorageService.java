package org.zerock.project.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    // 파일 저장 후 접근 가능한 URL 반환
    String storeFile(MultipartFile file) throws IOException;
}
