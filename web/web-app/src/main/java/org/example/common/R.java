package org.example.common;

import lombok.Data;
import org.example.exception.BizException;

/**
 * 统一的数据响应格式
 */
@Data
//@NoArgsConstructor
public class R<T> {
    // 请求状态码
    private int code;

    // 响应信息
    private String message;

    // 响应数据
    private T data;

    /**
     * 响应成功
     */
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setData(data);
        r.setMessage("请求成功");
        return r;
    }

    public static <T> R<T> success() {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("请求成功");
        return r;
    }

    /**
     * 响应失败
     */
    public static R error(String message) {
        R r = new R();
        r.setCode(201);
        r.setMessage(message);
        return r;
    }

    /**
     * 响应失败
     */
    public static R error(BizException bizException) {
        R r = new R();
        r.setCode(bizException.getCode());
        r.setMessage(bizException.getMessage());
        return r;
    }
}
