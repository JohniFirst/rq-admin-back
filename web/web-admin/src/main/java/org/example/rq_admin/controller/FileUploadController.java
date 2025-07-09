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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
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

@Tag(name = "文件上传", description = "文件上传相关的接口，包括单文件上传、多文件上传、文件下载等功能")
@RestController
@RequestMapping("/api/file")
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

    @Operation(summary = "文件下载", description = "根据文件路径下载文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "下载成功"),
        @ApiResponse(responseCode = "404", description = "文件不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/download/{dateFolder}/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "日期文件夹") @PathVariable String dateFolder,
            @Parameter(description = "文件名") @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get(uploadPath).resolve(dateFolder).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "删除文件", description = "根据文件路径删除文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "文件不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/delete/{dateFolder}/{filename}")
    public FormatResponseData deleteFile(
            @Parameter(description = "日期文件夹") @PathVariable String dateFolder,
            @Parameter(description = "文件名") @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get(uploadPath).resolve(dateFolder).resolve(filename);
            
            if (Files.exists(filePath)) {
                // 删除物理文件
                Files.delete(filePath);
                
                // 删除数据库中的文件信息
                String filePathStr = dateFolder + "/" + filename;
                Optional<FileInfo> fileInfo = fileInfoService.findByFilePath(filePathStr);
                if (fileInfo.isPresent()) {
                    fileInfoService.deleteFileInfo(fileInfo.get().getId());
                }
                
                return new FormatResponseData(ResponseStatus.SUCCESS, "文件删除成功");
            } else {
                return new FormatResponseData(ResponseStatus.FAILURE, "文件不存在");
            }
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