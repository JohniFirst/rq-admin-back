package org.example.enums;

import lombok.Getter;

@Getter
public enum ResponseStatus {
    SUCCESS(200, "请求成功"),
    FAILURE(500, "请求失败");

    private final int code;
    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
