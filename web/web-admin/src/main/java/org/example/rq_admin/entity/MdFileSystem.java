package org.example.rq_admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.rq_admin.enums.MdFileStatus;

import java.time.LocalDateTime;

@Schema(description = "md文件系统的配置类")
@Data
@TableName("md_file_system")
public class MdFileSystem {

    @TableId
    @Schema(description = "文件id", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "文件名，最长10个字符")
    private String fileName;

    @Schema(description = "文件图标")
    private String fileIcon;

    @Schema(description = "文件内容")
    private String fileContent;

    @Schema(description = "创建人ID")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long creatorId;

    @Schema(description = "创建人姓名")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long creatorName;

    @Schema(description = "文件描述，63-255个字符")
    private String description;

    @Schema(description = "文件状态")
    @TableField(fill = FieldFill.INSERT)
    private MdFileStatus status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @JsonIgnore
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updateTime;
}
