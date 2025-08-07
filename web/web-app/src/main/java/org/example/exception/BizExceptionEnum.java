package org.example.exception;

import lombok.Getter;

/**
 * 定义全局的异常信息
 */
public enum BizExceptionEnum {

    ID_NOT_FOUND(400, "id不存在"),
    VARIABLE_NOT_NULL(203, "参数不合法"),
    GOODS_EXPIRED(202, "商品已经过期");

    @Getter
    private final int code;

    @Getter
    private final String message;

    BizExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
