package org.example.rq_admin.service;

import lombok.Getter;
import lombok.Setter;
import org.example.rq_admin.entity.FileInfo;
import org.example.rq_admin.response_format.FormatResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    private final FileInfoService fileInfoService;
    private final String uploadPath;
    private final long maxFileSize;

    public FileUploadService(FileInfoService fileInfoService,
                             @Value("${file.upload.path:uploads}") String uploadPath,
                             @Value("${file.upload.max-size:10485760}") long maxFileSize) {
        this.fileInfoService = fileInfoService;
        this.uploadPath = uploadPath;
        this.maxFileSize = maxFileSize;
    }

    /**
     * 上传单个文件
     */
    public FormatResponseData uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return FormatResponseData.error("文件不能为空");
        }

        if (file.getSize() > maxFileSize) {
            return FormatResponseData.error("文件大小超过限制，最大允许 " + (maxFileSize / 1024 / 1024) + "MB");
        }

        try {
            FileUploadResult result = saveFileToDisk(file);
            FileInfo savedFileInfo = saveFileInfoToDatabase(result);

            FileUploadResponse response = buildUploadResponse(result, savedFileInfo);
            return FormatResponseData.ok(response);

        } catch (IOException e) {
            logger.error("文件上传失败: {}", file.getOriginalFilename(), e);
            return FormatResponseData.error(e.getMessage());
        }
    }

    /**
     * 批量上传多个文件
     */
    public FormatResponseData uploadMultipleFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return FormatResponseData.error("请选择要上传的文件");
        }

        List<FileUploadResponse> responses = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                errors.add("文件 " + file.getOriginalFilename() + " 为空");
                continue;
            }

            if (file.getSize() > maxFileSize) {
                errors.add("文件 " + file.getOriginalFilename() + " 大小超过限制");
                continue;
            }

            try {
                FileUploadResult result = saveFileToDisk(file);
                FileInfo savedFileInfo = saveFileInfoToDatabase(result);

                FileUploadResponse response = buildUploadResponse(result, savedFileInfo);
                responses.add(response);

            } catch (IOException e) {
                logger.error("文件上传失败: {}", file.getOriginalFilename(), e);
                errors.add("文件 " + file.getOriginalFilename() + " 上传失败: " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            return FormatResponseData.error("部分文件上传失败: " + String.join("; ", errors), responses);
        }

        return FormatResponseData.ok(responses);
    }

    /**
     * 保存文件到磁盘
     */
    private FileUploadResult saveFileToDisk(MultipartFile file) throws IOException {
        // 创建上传目录
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID() + fileExtension;

        // 按日期创建子目录
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path datePath = uploadDir.resolve(dateFolder);
        if (!Files.exists(datePath)) {
            Files.createDirectories(datePath);
        }

        // 保存文件
        Path filePath = datePath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return new FileUploadResult(originalFilename, uniqueFilename, dateFolder, file);
    }

    /**
     * 保存文件信息到数据库
     */
    private FileInfo saveFileInfoToDatabase(FileUploadResult result) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalFilename(result.originalFilename());
        fileInfo.setFilename(result.uniqueFilename());
        fileInfo.setFilePath(result.dateFolder() + "/" + result.uniqueFilename());
        fileInfo.setFileSize(result.file().getSize());
        fileInfo.setFileType(result.file().getContentType());
        fileInfo.setUploadTime(LocalDateTime.now());
        // TODO: 设置上传用户ID，需要从当前登录用户获取
        // fileInfo.setUploadUserId(getCurrentUserId());

        return fileInfoService.saveFileInfo(fileInfo);
    }

    /**
     * 构建上传响应
     */
    private FileUploadResponse buildUploadResponse(FileUploadResult result, FileInfo savedFileInfo) {
        FileUploadResponse response = new FileUploadResponse();
        response.setId(savedFileInfo.getId());
        response.setName(result.originalFilename());
        response.setFilename(result.uniqueFilename());
        response.setFileSize(result.file().getSize());
        response.setFileType(result.file().getContentType());
        response.setUploadTime(LocalDateTime.now());
        return response;
    }

    /**
     * 文件上传结果内部类
     */
    private record FileUploadResult(String originalFilename, String uniqueFilename, String dateFolder,
                                    MultipartFile file) {
    }

    /**
     * 文件上传响应类
     */
    @Setter
    @Getter
    public static class FileUploadResponse {
        private String id;
        //        上传的原始文件名
        private String name;
        //        保存到服务器的文件名
        private String filename;
        //        文件大小
        private long fileSize;
        //        文件类型
        private String fileType;

        private LocalDateTime uploadTime;
    }
} 