package org.example.enums;

import lombok.Getter;

/**
 * 文件上传状态
 */
@Getter
public enum FileUploadStatusForAntd {
    ERROR("error"),
    DONE("done"),
    UPLOADING("uploading"),
    REMOVED("removed");

    private final String status;

    FileUploadStatusForAntd(final String status) {
        this.status = status;
    }
}
