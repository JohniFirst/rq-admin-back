package org.example.rq_admin.repository;

import org.example.rq_admin.entity.FileInfo;
import org.example.enums.IsEnabled;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    
    /**
     * 根据文件名查找文件信息
     */
    Optional<FileInfo> findByFilename(String filename);
    
    /**
     * 根据文件路径查找文件信息
     */
    Optional<FileInfo> findByFilePath(String filePath);
    
    /**
     * 根据上传用户ID查找文件信息
     */
    List<FileInfo> findByUploadUserId(Long uploadUserId);
    
    /**
     * 根据文件类型查找文件信息
     */
    List<FileInfo> findByFileType(String fileType);
    
    /**
     * 根据状态查找文件信息
     */
    List<FileInfo> findByIsEnabled(IsEnabled isEnabled);
    
    /**
     * 根据上传时间范围查找文件信息
     */
    List<FileInfo> findByUploadTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据原始文件名模糊查询
     */
    List<FileInfo> findByOriginalFilenameContaining(String originalFilename);
    
    /**
     * 分页查询所有启用的文件信息
     */
    Page<FileInfo> findByIsEnabled(IsEnabled isEnabled, Pageable pageable);
    
    /**
     * 根据上传用户ID分页查询文件信息
     */
    Page<FileInfo> findByUploadUserId(Long uploadUserId, Pageable pageable);
    
    /**
     * 根据文件类型分页查询文件信息
     */
    Page<FileInfo> findByFileType(String fileType, Pageable pageable);
    
    /**
     * 根据上传时间范围分页查询文件信息
     */
    Page<FileInfo> findByUploadTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据原始文件名模糊查询并分页
     */
    Page<FileInfo> findByOriginalFilenameContaining(String originalFilename, Pageable pageable);
    
    /**
     * 统计指定用户的文件数量
     */
    long countByUploadUserId(Long uploadUserId);
    
    /**
     * 统计指定类型的文件数量
     */
    long countByFileType(String fileType);
    
    /**
     * 统计指定状态的文件数量
     */
    long countByIsEnabled(IsEnabled isEnabled);
    
    /**
     * 统计指定时间范围内的文件数量
     */
    long countByUploadTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据多个条件查询文件信息
     */
    @Query("SELECT f FROM FileInfo f WHERE " +
           "(:uploadUserId IS NULL OR f.uploadUserId = :uploadUserId) AND " +
           "(:fileType IS NULL OR f.fileType = :fileType) AND " +
           "(:isEnabled IS NULL OR f.isEnabled = :isEnabled) AND " +
           "(:startTime IS NULL OR f.uploadTime >= :startTime) AND " +
           "(:endTime IS NULL OR f.uploadTime <= :endTime) AND " +
           "(:originalFilename IS NULL OR f.originalFilename LIKE %:originalFilename%)")
    Page<FileInfo> findByConditions(
            @Param("uploadUserId") Long uploadUserId,
            @Param("fileType") String fileType,
            @Param("isEnabled") IsEnabled isEnabled,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("originalFilename") String originalFilename,
            Pageable pageable);
} 