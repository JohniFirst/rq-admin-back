package org.example.rq_admin.response_format;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.enums.ResponseStatus;
import org.example.rq_admin.calendar.CalendarEvent;

import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormatResponseData {
    // 请求是否成功
    private int code;
    // 响应提示信息
    private String message;
    // 响应数据
    private Object data;

    public FormatResponseData(ResponseStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    public FormatResponseData(ResponseStatus status, String message) {
        this.code = status.getCode();
        this.message = message;
    }

    public FormatResponseData(ResponseStatus status, String message, Object data) {
        this.code = status.getCode();
        this.message = message;
        this.data = data;
    }

    public FormatResponseData(ResponseStatus status, Object data) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = data;
    }
}
