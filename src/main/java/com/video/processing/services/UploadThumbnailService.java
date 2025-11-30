package com.video.processing.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadThumbnailService {
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/thumbnails";
    private static final Logger logger = LoggerFactory.getLogger(UploadThumbnailService.class);

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        logger.info("file-name: {}", file.getOriginalFilename().toString());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        file.transferTo(filePath.toFile());
        return "uploads/thumbnails/" + fileName;
    }
}
