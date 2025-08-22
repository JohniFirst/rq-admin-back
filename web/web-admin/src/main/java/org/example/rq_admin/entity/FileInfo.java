package org.example.rq_admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.rq_admin.enums.IsEnabled;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "file_info")
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "filename", nullable = false, unique = true)
    private String filename;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    @Column(name = "upload_user_id")
    private Long uploadUserId;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_enabled", nullable = false)
    private IsEnabled isEnabled = IsEnabled.ENABLED;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 构造函数
    public FileInfo() {
        this.createTime = LocalDateTime.now();
        this.uploadTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 