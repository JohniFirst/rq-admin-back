package org.example.rq_admin.enums;

import lombok.Getter;

/**
 * 是否启用 1/0
 */
@Getter
public enum IsEnabled {
    ENABLED(1),
    DISABLED(0);

    private final int statusCode;

    IsEnabled(int statusCode) {
        this.statusCode = statusCode;
    }

}
