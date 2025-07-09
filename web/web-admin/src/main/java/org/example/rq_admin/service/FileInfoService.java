package org.example.rq_admin.service;

import org.apache.commons.lang3.ObjectUtils;
import org.example.enums.IsEnabled;
import org.example.enums.ResponseStatus;
import org.example.rq_admin.entity.FileInfo;
import org.example.rq_admin.repository.FileInfoRepository;
import org.example.rq_admin.response_format.FormatResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FileInfoService {

    private final FileInfoRepository fileInfoRepository;

    public FileInfoService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    /**
     * 保存文件信息
     */
    public FileInfo saveFileInfo(FileInfo fileInfo) {
        return fileInfoRepository.save(fileInfo);
    }

    /**
     * 根据ID查找文件信息
     */
    public Optional<FileInfo> findById(String id) {
        return fileInfoRepository.findById(id);
    }

    /**
     * 根据文件名查找文件信息
     */
    public Optional<FileInfo> findByFilename(String filename) {
        return fileInfoRepository.findByFilename(filename);
    }

    /**
     * 根据文件路径查找文件信息
     */
    public Optional<FileInfo> findByFilePath(String filePath) {
        return fileInfoRepository.findByFilePath(filePath);
    }

    /**
     * 获取所有文件信息
     */
    public List<FileInfo> findAll() {
        return fileInfoRepository.findAll();
    }

    /**
     * 分页获取所有文件信息
     */
    public Page<FileInfo> findAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("uploadTime").descending());
        return fileInfoRepository.findAll(pageable);
    }

    /**
     * 根据上传用户ID获取文件信息
     */
    public List<FileInfo> findByUploadUserId(Long uploadUserId) {
        return fileInfoRepository.findByUploadUserId(uploadUserId);
    }

    /**
     * 根据上传用户ID分页获取文件信息
     */
    public Page<FileInfo> findByUploadUserId(Long uploadUserId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("uploadTime").descending());
        return fileInfoRepository.findByUploadUserId(uploadUserId, pageable);
    }

    /**
     * 根据文件类型获取文件信息
     */
    public List<FileInfo> findByFileType(String fileType) {
        return fileInfoRepository.findByFileType(fileType);
    }

    /**
     * 根据文件类型分页获取文件信息
     */
    public Page<FileInfo> findByFileType(String fileType, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("uploadTime").descending());
        return fileInfoRepository.findByFileType(fileType, pageable);
    }

    /**
     * 根据状态获取文件信息
     */
    public List<FileInfo> findByIsEnabled(IsEnabled isEnabled) {
        return fileInfoRepository.findByIsEnabled(isEnabled);
    }

    /**
     * 根据状态分页获取文件信息
     */
    public Page<FileInfo> findByIsEnabled(IsEnabled isEnabled, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("uploadTime").descending());
        return fileInfoRepository.findByIsEnabled(isEnabled, pageable);
    }

    /**
     * 根据上传时间范围获取文件信息
     */
    public List<FileInfo> findByUploadTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return fileInfoRepository.findByUploadTimeBetween(startTime, endTime);
    }

    /**
     * 根据上传时间范围分页获取文件信息
     */
    public Page<FileInfo> findByUploadTimeBetween(LocalDateTime startTime, LocalDateTime endTime, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("uploadTime").descending());
        return fileInfoRepository.findByUploadTimeBetween(startTime, endTime, pageable);
    }

    /**
     * 根据原始文件名模糊查询
     */
    public List<FileInfo> findByOriginalFilenameContaining(String originalFilename) {
        return fileInfoRepository.findByOriginalFilenameContaining(originalFilename);
    }

    /**
     * 根据原始文件名模糊查询并分页
     */
    public Page<FileInfo> findByOriginalFilenameContaining(String originalFilename, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("uploadTime").descending());
        return fileInfoRepository.findByOriginalFilenameContaining(originalFilename, pageable);
    }

    /**
     * 根据多个条件查询文件信息
     */
    public Page<FileInfo> findByConditions(Long uploadUserId, String fileType, IsEnabled isEnabled,
                                           LocalDateTime startTime, LocalDateTime endTime,
                                           String originalFilename, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("uploadTime").descending());
        return fileInfoRepository.findByConditions(uploadUserId, fileType, isEnabled,
                startTime, endTime, originalFilename, pageable);
    }

    /**
     * 更新文件信息
     */
    public FileInfo updateFileInfo(FileInfo fileInfo) {
        if (fileInfo.getId() == null) {
            throw new IllegalArgumentException("文件信息ID不能为空");
        }
        return fileInfoRepository.save(fileInfo);
    }

    /**
     * 更新文件状态
     */
    public FormatResponseData<ObjectUtils.Null> updateFileStatus(String fileId, IsEnabled isEnabled) {
        Optional<FileInfo> optionalFileInfo = fileInfoRepository.findById(fileId);
        if (optionalFileInfo.isPresent()) {
            FileInfo fileInfo = optionalFileInfo.get();
            fileInfo.setIsEnabled(isEnabled);
            fileInfoRepository.save(fileInfo);
            return new FormatResponseData<>(ResponseStatus.SUCCESS, "文件状态更新成功");
        } else {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "文件不存在");
        }
    }

    /**
     * 更新文件描述
     */
    public FormatResponseData<ObjectUtils.Null> updateFileDescription(String fileId, String description) {
        Optional<FileInfo> optionalFileInfo = fileInfoRepository.findById(fileId);
        if (optionalFileInfo.isPresent()) {
            FileInfo fileInfo = optionalFileInfo.get();
            fileInfo.setDescription(description);
            fileInfoRepository.save(fileInfo);
            return new FormatResponseData<>(ResponseStatus.SUCCESS, "文件描述更新成功");
        } else {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "文件不存在");
        }
    }

    /**
     * 删除文件信息
     */
    public FormatResponseData<ObjectUtils.Null> deleteFileInfo(String fileId) {
        Optional<FileInfo> optionalFileInfo = fileInfoRepository.findById(fileId);
        if (optionalFileInfo.isPresent()) {
            fileInfoRepository.deleteById(fileId);
            return new FormatResponseData<>(ResponseStatus.SUCCESS, "文件信息删除成功");
        } else {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "文件不存在");
        }
    }

    /**
     * 批量删除文件信息
     */
    public FormatResponseData<ObjectUtils.Null> deleteFileInfos(List<String> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return new FormatResponseData<>(ResponseStatus.FAILURE, "请选择要删除的文件");
        }

        fileInfoRepository.deleteAllById(fileIds);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, "批量删除成功");
    }

    /**
     * 统计指定用户的文件数量
     */
    public long countByUploadUserId(Long uploadUserId) {
        return fileInfoRepository.countByUploadUserId(uploadUserId);
    }

    /**
     * 统计指定类型的文件数量
     */
    public long countByFileType(String fileType) {
        return fileInfoRepository.countByFileType(fileType);
    }

    /**
     * 统计指定状态的文件数量
     */
    public long countByIsEnabled(IsEnabled isEnabled) {
        return fileInfoRepository.countByIsEnabled(isEnabled);
    }

    /**
     * 统计指定时间范围内的文件数量
     */
    public long countByUploadTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return fileInfoRepository.countByUploadTimeBetween(startTime, endTime);
    }

    /**
     * 统计总文件数量
     */
    public long countAll() {
        return fileInfoRepository.count();
    }
} 