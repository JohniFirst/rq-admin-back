package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.enums.IsEnabled;
import org.example.enums.ResponseStatus;
import org.example.rq_admin.entity.FileInfo;
import org.example.rq_admin.response_format.FormatResponseData;
import org.example.rq_admin.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.web.bind.annotation.*;
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
import java.util.Optional;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "文件上传", description = "文件上传相关的接口，包括单文件上传、多文件上传、文件下载等功能")
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FileInfoService fileInfoService;

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize; // 默认10MB

    @Operation(summary = "单文件上传", description = "上传单个文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "上传成功"),
        @ApiResponse(responseCode = "400", description = "文件为空或格式不支持"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/upload")
    public FormatResponseData<FileUploadResponse> uploadFile(
            @Parameter(description = "要上传的文件") @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "文件不能为空");
        }

        if (file.getSize() > maxFileSize) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, 
                "文件大小超过限制，最大允许 " + (maxFileSize / 1024 / 1024) + "MB");
        }

        try {
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
            
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 按日期创建子目录
            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path datePath = uploadDir.resolve(dateFolder);
            if (!Files.exists(datePath)) {
                Files.createDirectories(datePath);
            }

            // 保存文件
            Path filePath = datePath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 保存文件信息到数据库
            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginalFilename(originalFilename);
            fileInfo.setFilename(uniqueFilename);
            fileInfo.setFilePath(dateFolder + "/" + uniqueFilename);
            fileInfo.setFileSize(file.getSize());
            fileInfo.setFileType(file.getContentType());
            fileInfo.setUploadTime(LocalDateTime.now());
            // TODO: 设置上传用户ID，需要从当前登录用户获取
            // fileInfo.setUploadUserId(getCurrentUserId());
            
            FileInfo savedFileInfo = fileInfoService.saveFileInfo(fileInfo);

            // 构建响应信息
            FileUploadResponse response = new FileUploadResponse();
            response.setId(savedFileInfo.getId());
            response.setOriginalFilename(originalFilename);
            response.setFilename(uniqueFilename);
            response.setFileSize(file.getSize());
            response.setFileType(file.getContentType());
            response.setFilePath(dateFolder + "/" + uniqueFilename);
            response.setUploadTime(LocalDateTime.now());

            return new FormatResponseData<>(ResponseStatus.SUCCESS, "文件上传成功", response);

        } catch (IOException e) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "多文件上传", description = "批量上传多个文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "上传成功"),
        @ApiResponse(responseCode = "400", description = "文件为空或格式不支持"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/upload/multiple")
    public FormatResponseData<List<FileUploadResponse>> uploadMultipleFiles(
            @Parameter(description = "要上传的文件列表") @RequestParam("files") MultipartFile[] files) {
        
        if (files == null || files.length == 0) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "请选择要上传的文件");
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
                
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
                
                // 按日期创建子目录
                String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                Path datePath = uploadDir.resolve(dateFolder);
                if (!Files.exists(datePath)) {
                    Files.createDirectories(datePath);
                }

                // 保存文件
                Path filePath = datePath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // 保存文件信息到数据库
                FileInfo fileInfo = new FileInfo();
                fileInfo.setOriginalFilename(originalFilename);
                fileInfo.setFilename(uniqueFilename);
                fileInfo.setFilePath(dateFolder + "/" + uniqueFilename);
                fileInfo.setFileSize(file.getSize());
                fileInfo.setFileType(file.getContentType());
                fileInfo.setUploadTime(LocalDateTime.now());
                // TODO: 设置上传用户ID，需要从当前登录用户获取
                // fileInfo.setUploadUserId(getCurrentUserId());
                
                FileInfo savedFileInfo = fileInfoService.saveFileInfo(fileInfo);

                // 构建响应信息
                FileUploadResponse response = new FileUploadResponse();
                response.setId(savedFileInfo.getId());
                response.setOriginalFilename(originalFilename);
                response.setFilename(uniqueFilename);
                response.setFileSize(file.getSize());
                response.setFileType(file.getContentType());
                response.setFilePath(dateFolder + "/" + uniqueFilename);
                response.setUploadTime(LocalDateTime.now());

                responses.add(response);

            } catch (IOException e) {
                errors.add("文件 " + file.getOriginalFilename() + " 上传失败: " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, 
                "部分文件上传失败: " + String.join("; ", errors), responses);
        }

        return new FormatResponseData<>(ResponseStatus.SUCCESS, "所有文件上传成功", responses);
    }

    @Operation(summary = "文件下载", description = "根据文件ID下载文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "下载成功"),
        @ApiResponse(responseCode = "404", description = "文件不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/download/{fileId}")
    public void downloadFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            HttpServletResponse response) {
        
        try {
            Optional<FileInfo> fileInfo = fileInfoService.findById(fileId);
            if (!fileInfo.isPresent()) {
                System.err.println("文件不存在，ID: " + fileId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            Path filePath = Paths.get(uploadPath).resolve(fileInfo.get().getFilePath());
            System.out.println("尝试下载文件: " + filePath.toString());
            
            if (Files.exists(filePath) && Files.isReadable(filePath)) {
                String originalFilename = fileInfo.get().getOriginalFilename();
                // 处理文件名中的特殊字符
                String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
                
                // 设置响应头
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", 
                    "attachment; filename=\"" + safeFilename + "\"");
                
                // 直接写入响应流
                Files.copy(filePath, response.getOutputStream());
                response.getOutputStream().flush();
            } else {
                System.err.println("文件不存在或不可读: " + filePath.toString());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("下载文件时发生错误: " + e.getMessage());
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                // 忽略设置状态码时的异常
            }
        }
    }

    @Operation(summary = "文件预览", description = "根据文件路径预览文件（用于兼容旧版本）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "预览成功"),
        @ApiResponse(responseCode = "404", description = "文件不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/preview/**")
    public void previewFile(HttpServletRequest request, HttpServletResponse response) {
        
        try {
            // 获取请求路径中的文件路径
            String requestURI = request.getRequestURI();
            String filePath = requestURI.substring(requestURI.indexOf("/preview/") + 9);
            
            Path fullPath = Paths.get(uploadPath).resolve(filePath);

            if (Files.exists(fullPath) && Files.isReadable(fullPath)) {
                // 根据文件类型设置Content-Type
                String contentType = determineContentType(filePath);
                
                // 设置响应头
                response.setContentType(contentType);
                response.setHeader("Content-Disposition", 
                    "inline; filename=\"" + fullPath.getFileName().toString() + "\"");
                
                // 直接写入响应流
                Files.copy(fullPath, response.getOutputStream());
                response.getOutputStream().flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("预览文件时发生错误: " + e.getMessage());
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                // 忽略设置状态码时的异常
            }
        }
    }

    /**
     * 根据文件扩展名确定Content-Type
     */
    private String determineContentType(String filePath) {
        String extension = "";
        if (filePath.contains(".")) {
            extension = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
        }
        
        switch (extension) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".bmp":
                return "image/bmp";
            case ".webp":
                return "image/webp";
            case ".pdf":
                return "application/pdf";
            case ".txt":
                return "text/plain";
            case ".html":
            case ".htm":
                return "text/html";
            case ".css":
                return "text/css";
            case ".js":
                return "application/javascript";
            case ".json":
                return "application/json";
            case ".xml":
                return "application/xml";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".ppt":
                return "application/vnd.ms-powerpoint";
            case ".pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case ".zip":
                return "application/zip";
            case ".rar":
                return "application/x-rar-compressed";
            case ".mp4":
                return "video/mp4";
            case ".mp3":
                return "audio/mpeg";
            default:
                return "application/octet-stream";
        }
    }

    @Operation(summary = "删除文件", description = "根据文件ID删除文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "文件不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/delete/{fileId}")
    public FormatResponseData deleteFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        
        try {
            Optional<FileInfo> fileInfo = fileInfoService.findById(fileId);
            if (!fileInfo.isPresent()) {
                return new FormatResponseData(ResponseStatus.FAILURE, "文件不存在");
            }
            
            // 删除物理文件
            Path filePath = Paths.get(uploadPath).resolve(fileInfo.get().getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
            // 删除数据库中的文件信息
            fileInfoService.deleteFileInfo(fileId);
            
            return new FormatResponseData(ResponseStatus.SUCCESS, "文件删除成功");
        } catch (IOException e) {
            return new FormatResponseData(ResponseStatus.FAILURE, "文件删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取文件列表", description = "分页获取所有文件信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/list")
    public FormatResponseData<Page<FileInfo>> getFileList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<FileInfo> fileList = fileInfoService.findAll(pageNumber, pageSize);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, fileList);
    }

    @Operation(summary = "根据ID获取文件信息", description = "根据文件ID获取详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @GetMapping("/info/{fileId}")
    public FormatResponseData<FileInfo> getFileInfo(
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        
        Optional<FileInfo> fileInfo = fileInfoService.findById(fileId);
        if (fileInfo.isPresent()) {
            return new FormatResponseData<>(ResponseStatus.SUCCESS, fileInfo.get());
        } else {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "文件不存在");
        }
    }

    @Operation(summary = "更新文件状态", description = "启用或禁用文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @PutMapping("/status/{fileId}")
    public FormatResponseData updateFileStatus(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            @Parameter(description = "文件状态") @RequestParam IsEnabled isEnabled) {
        
        return fileInfoService.updateFileStatus(fileId, isEnabled);
    }

    @Operation(summary = "更新文件描述", description = "更新文件的描述信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @PutMapping("/description/{fileId}")
    public FormatResponseData updateFileDescription(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            @Parameter(description = "文件描述") @RequestParam String description) {
        
        return fileInfoService.updateFileDescription(fileId, description);
    }

    @Operation(summary = "删除文件信息", description = "根据文件ID删除文件信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @DeleteMapping("/info/{fileId}")
    public FormatResponseData deleteFileInfo(
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        
        Optional<FileInfo> fileInfo = fileInfoService.findById(fileId);
        if (fileInfo.isPresent()) {
            // 删除物理文件
            try {
                Path filePath = Paths.get(uploadPath).resolve(fileInfo.get().getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                // 物理文件删除失败，但继续删除数据库记录
            }
            
            // 删除数据库记录
            return fileInfoService.deleteFileInfo(fileId);
        } else {
            return new FormatResponseData(ResponseStatus.FAILURE, "文件不存在");
        }
    }

    @Operation(summary = "批量删除文件", description = "批量删除多个文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @DeleteMapping("/batch")
    public FormatResponseData batchDeleteFiles(
            @Parameter(description = "文件ID列表") @RequestBody List<Long> fileIds) {
        
        if (fileIds == null || fileIds.isEmpty()) {
            return new FormatResponseData(ResponseStatus.FAILURE, "请选择要删除的文件");
        }
        
        // 删除物理文件
        for (Long fileId : fileIds) {
            Optional<FileInfo> fileInfo = fileInfoService.findById(fileId);
            if (fileInfo.isPresent()) {
                try {
                    Path filePath = Paths.get(uploadPath).resolve(fileInfo.get().getFilePath());
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                    }
                } catch (IOException e) {
                    // 物理文件删除失败，但继续处理其他文件
                }
            }
        }
        
        // 删除数据库记录
        return fileInfoService.deleteFileInfos(fileIds);
    }

    @Operation(summary = "测试文件上传路径", description = "测试文件上传路径是否正确")
    @GetMapping("/test/path")
    public FormatResponseData<String> testUploadPath() {
        try {
            Path uploadDir = Paths.get(uploadPath);
            String absolutePath = uploadDir.toAbsolutePath().toString();
            boolean exists = Files.exists(uploadDir);
            boolean isDirectory = Files.isDirectory(uploadDir);
            
            String result = String.format("上传路径: %s, 存在: %s, 是目录: %s", 
                absolutePath, exists, isDirectory);
            
            return new FormatResponseData<>(ResponseStatus.SUCCESS, result);
        } catch (Exception e) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "测试失败: " + e.getMessage());
        }
    }

    // 文件上传响应类
    public static class FileUploadResponse {
        private Long id;
        private String originalFilename;
        private String filename;
        private long fileSize;
        private String fileType;
        private String filePath;
        private LocalDateTime uploadTime;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }

        public void setOriginalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public LocalDateTime getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(LocalDateTime uploadTime) {
            this.uploadTime = uploadTime;
        }
    }
} 