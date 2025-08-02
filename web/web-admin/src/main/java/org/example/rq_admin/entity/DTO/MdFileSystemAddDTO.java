package org.example.rq_admin.entity.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MdFileSystemAddDTO {
    @Schema(description = "文件名，最长10个字符")
    public String fileName;

    @Schema(description = "文件图标")
    public String fileIcon;

    @Schema(description = "文件内容")
    public String fileContent;

    @Schema(description = "文件描述，63-255个字符")
    public String description;
}
