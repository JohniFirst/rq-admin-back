package org.example.rq_admin.response_format;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.rq_admin.config.BizException;
import org.example.rq_admin.enums.ResponseStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormatResponseData<T> {
    // 请求是否成功
    private int code;
    // 响应提示信息
    private String message;
    // 响应数据
    private T data;

    /**
     * 响应成功的函数
     */
    public static <T> FormatResponseData<T> ok() {
        FormatResponseData<T> R = new FormatResponseData<>();
        R.setCode(ResponseStatus.SUCCESS.getCode());
        R.setMessage(ResponseStatus.SUCCESS.getMessage());

        return R;
    }

    /**
     * 响应成功的函数
     */
    public static <T> FormatResponseData<T> ok(T data) {
        FormatResponseData<T> R = new FormatResponseData<>();
        R.setCode(ResponseStatus.SUCCESS.getCode());
        R.setMessage(ResponseStatus.SUCCESS.getMessage());
        R.setData(data);

        return R;
    }

    /**
     * 响应失败的函数
     */
    public static FormatResponseData error(String message) {
        FormatResponseData<Object> R = new FormatResponseData<>();
        R.setCode(ResponseStatus.FAILURE.getCode());
        R.setMessage(message);
        return R;
    }

    /**
     * 响应失败的函数
     */
    public static <T> FormatResponseData<T> error(String message, T data) {
        FormatResponseData<T> R = new FormatResponseData<>();
        R.setCode(ResponseStatus.FAILURE.getCode());
        R.setMessage(message);
        R.setData(data);
        return R;
    }

    /**
     * 响应失败
     */
    public static FormatResponseData error(BizException bizException) {
        FormatResponseData r = new FormatResponseData();
        r.setCode(bizException.getCode());
        r.setMessage(bizException.getMessage());
        return r;
    }
}
