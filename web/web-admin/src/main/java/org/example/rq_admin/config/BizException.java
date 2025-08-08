package org.example.rq_admin.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BizException extends RuntimeException {
    // 异常状态码
    private Integer code;

    // 异常信息
    private String msg;

    public BizException(String msg) {
        super(msg);

        this.code = 201;
        this.msg = msg;
    }

    public BizException(BizExceptionEnum bizExceptionEnum) {
        super(bizExceptionEnum.getMessage());

        this.code = bizExceptionEnum.getCode();
        this.msg = bizExceptionEnum.getMessage();
    }
}
