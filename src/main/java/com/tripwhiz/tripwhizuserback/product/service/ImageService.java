package com.tripwhiz.tripwhizuserback.product.service;

import com.tripwhiz.tripwhizuserback.product.domain.Image;
import com.tripwhiz.tripwhizuserback.product.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Arrays;


@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private ImageRepository imageRepository;

    private static final String BASE_URL = "http://localhost/images/";

    @Transactional
    public void saveImagesWithUrl(String directoryPath) {
        logger.info("Starting to save images from directory: {}", directoryPath);

        File directory = new File(directoryPath);  // 전달받은 directoryPath 사용
        if (directory.exists() && directory.isDirectory()) {
            logger.info("Directory found: {}", directoryPath);

            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                Arrays.stream(files)
                        .filter(File::isFile)
                        .forEach(file -> {
                            String fileName = file.getName(); // 파일 이름 추출
                            String imageUrl = BASE_URL + fileName; // 전체 URL 생성
                            Image image = new Image(fileName, imageUrl); // 파일 이름과 URL로 Image 객체 생성

                            // 이미지 데이터베이스에 저장
                            imageRepository.save(image);
                            logger.info("Saved image - Filename: {}, URL: {}", fileName, imageUrl);
                        });
            } else {
                logger.warn("No files found in directory: {}", directoryPath);
            }
        } else {
            logger.error("Directory does not exist or is not a directory: {}", directoryPath);
        }

        logger.info("Image saving process completed.");
    }
}
