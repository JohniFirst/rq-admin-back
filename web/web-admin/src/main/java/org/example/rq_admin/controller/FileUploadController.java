package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.apache.commons.lang3.ObjectUtils;
import org.example.PaginationConfig;
import org.example.enums.IsEnabled;
import org.example.enums.ResponseStatus;
import org.example.rq_admin.entity.FileInfo;
import org.example.rq_admin.response_format.FormatResponseData;
import org.example.rq_admin.service.FileInfoService;
import org.example.rq_admin.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "文件上传", description = "文件上传相关的接口，包括单文件上传、多文件上传、文件下载等功能")
@RestController
@RequestMapping("/file")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final FileInfoService fileInfoService;
    private final FileUploadService fileUploadService;
    private final String uploadPath;

    public FileUploadController(FileInfoService fileInfoService,
                                FileUploadService fileUploadService,
                                @Value("${file.upload.path:uploads}") String uploadPath) {
        this.fileInfoService = fileInfoService;
        this.fileUploadService = fileUploadService;
        this.uploadPath = uploadPath;
    }

    @Operation(summary = "单文件上传", description = "上传单个文件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功"),
            @ApiResponse(responseCode = "400", description = "文件为空或格式不支持"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/upload")
    public FormatResponseData<FileUploadService.FileUploadResponse> uploadFile(
            @Parameter(description = "要上传的文件") @RequestParam("file") MultipartFile file) {
        return fileUploadService.uploadFile(file);
    }

    @Operation(summary = "多文件上传", description = "批量上传多个文件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功"),
            @ApiResponse(responseCode = "400", description = "文件为空或格式不支持"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/upload/multiple")
    public FormatResponseData<List<FileUploadService.FileUploadResponse>> uploadMultipleFiles(
            @Parameter(description = "要上传的文件列表") @RequestParam("files") MultipartFile[] files) {
        return fileUploadService.uploadMultipleFiles(files);
    }

    @Operation(summary = "文件下载", description = "根据文件ID下载文件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "下载成功"),
            @ApiResponse(responseCode = "404", description = "文件不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/download/{fileId}")
    public void downloadFile(
            @Parameter(description = "文件ID") @PathVariable String fileId,
            HttpServletResponse response) {

        try {
            Optional<FileInfo> fileInfo = fileInfoService.findById(fileId);
            if (fileInfo.isEmpty()) {
                logger.warn("文件不存在，ID: {}", fileId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Path filePath = Paths.get(uploadPath).resolve(fileInfo.get().getFilePath());
            logger.debug("尝试下载文件: {}", filePath);

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
                logger.warn("文件不存在或不可读: {}", filePath);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("下载文件时发生错误，文件ID: {}", fileId, e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                logger.warn("设置HTTP状态码时发生异常", ex);
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

        String filePath = null;
        try {
            // 获取请求路径中的文件路径
            String requestURI = request.getRequestURI();
            filePath = requestURI.substring(requestURI.indexOf("/preview/") + 9);

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
            logger.error("预览文件时发生错误，文件路径: {}", filePath, e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                logger.warn("设置HTTP状态码时发生异常", ex);
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

        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".bmp" -> "image/bmp";
            case ".webp" -> "image/webp";
            case ".pdf" -> "application/pdf";
            case ".txt" -> "text/plain";
            case ".html", ".htm" -> "text/html";
            case ".css" -> "text/css";
            case ".js" -> "application/javascript";
            case ".json" -> "application/json";
            case ".xml" -> "application/xml";
            case ".doc" -> "application/msword";
            case ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls" -> "application/vnd.ms-excel";
            case ".xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".ppt" -> "application/vnd.ms-powerpoint";
            case ".pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case ".zip" -> "application/zip";
            case ".rar" -> "application/x-rar-compressed";
            case ".mp4" -> "video/mp4";
            case ".mp3" -> "audio/mpeg";
            default -> "application/octet-stream";
        };
    }

    @Operation(summary = "删除文件", description = "根据文件ID删除文件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "文件不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/delete/{fileId}")
    public FormatResponseData<ObjectUtils.Null> deleteFile(
            @Parameter(description = "文件ID") @PathVariable String fileId) {

        try {
            Optional<FileInfo> fileInfo = fileInfoService.findById(fileId);
            if (fileInfo.isEmpty()) {
                return new FormatResponseData<>(ResponseStatus.FAILURE, "文件不存在");
            }

            // 删除物理文件
            Path filePath = Paths.get(uploadPath).resolve(fileInfo.get().getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // 删除数据库中的文件信息
            fileInfoService.deleteFileInfo(fileId);

            return new FormatResponseData<>(ResponseStatus.SUCCESS, "文件删除成功");
        } catch (IOException e) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "文件删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取文件列表", description = "分页获取所有文件信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/list")
    public FormatResponseData<PaginationConfig<FileInfo>> getFileList(
            @Parameter(description = "页码（从1开始）") @RequestParam(defaultValue = "1") Integer pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        PaginationConfig<FileInfo> fileList = fileInfoService.findAll(pageNumber, pageSize);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, fileList);
    }

    @Operation(summary = "根据ID获取文件信息", description = "根据文件ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @GetMapping("/info/{fileId}")
    public FormatResponseData<FileInfo> getFileInfo(
            @Parameter(description = "文件ID") @PathVariable String fileId) {

        Optional<FileInfo> fileInfo = fileInfoService.findById(fileId);
        return fileInfo.map(info -> new FormatResponseData<>(ResponseStatus.SUCCESS, info)).orElseGet(() -> new FormatResponseData<>(ResponseStatus.FAILURE, "文件不存在"));
    }

    @Operation(summary = "更新文件状态", description = "启用或禁用文件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @PutMapping("/status/{fileId}")
    public FormatResponseData<ObjectUtils.Null> updateFileStatus(
            @Parameter(description = "文件ID") @PathVariable String fileId,
            @Parameter(description = "文件状态") @RequestParam IsEnabled isEnabled) {

        return fileInfoService.updateFileStatus(fileId, isEnabled);
    }

    @Operation(summary = "更新文件描述", description = "更新文件的描述信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @PutMapping("/description/{fileId}")
    public FormatResponseData<ObjectUtils.Null> updateFileDescription(
            @Parameter(description = "文件ID") @PathVariable String fileId,
            @Parameter(description = "文件描述") @RequestParam String description) {

        return fileInfoService.updateFileDescription(fileId, description);
    }

    @Operation(summary = "删除文件信息", description = "根据文件ID删除文件信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @DeleteMapping("/info/{fileId}")
    public FormatResponseData<ObjectUtils.Null> deleteFileInfo(
            @Parameter(description = "文件ID") @PathVariable String fileId) {

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
            return new FormatResponseData<>(ResponseStatus.FAILURE, "文件不存在");
        }
    }

    @Operation(summary = "批量删除文件", description = "批量删除多个文件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @DeleteMapping("/batch")
    public FormatResponseData<ObjectUtils.Null> batchDeleteFiles(
            @Parameter(description = "文件ID列表") @RequestBody List<String> fileIds) {

        if (fileIds == null || fileIds.isEmpty()) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "请选择要删除的文件");
        }

        // 删除物理文件
        for (String fileId : fileIds) {
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
}