package org.example.rq_admin.config;

import lombok.Getter;

/**
 * 定义全局的异常信息
 */
@Getter
public enum BizExceptionEnum {

    ID_NOT_FOUND(400, "id不存在"),
    VARIABLE_NOT_NULL(203, "参数不合法"),
    GOODS_EXPIRED(202, "商品已经过期");

    private final int code;

    private final String message;

    BizExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
