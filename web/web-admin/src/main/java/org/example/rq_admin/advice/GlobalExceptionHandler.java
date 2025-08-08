package org.example.rq_admin.advice;

import io.swagger.v3.oas.annotations.Hidden;
import org.example.rq_admin.config.BizException;
import org.example.rq_admin.response_format.FormatResponseData;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.util.HashMap;

@Hidden // 解决knife4j文档显示异常的问题
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public FormatResponseData error(Exception e) {
        return FormatResponseData.error(e.getMessage());
    }

    /**
     * 业务处理异常
     */
    @ExceptionHandler(BizException.class)
    public FormatResponseData error(BizException e) {
        return FormatResponseData.error(e);
    }

    /**
     * 数学计算异常
     */
    @ExceptionHandler(ArithmeticException.class)
    public FormatResponseData error(ArithmeticException e) {
        return FormatResponseData.error(e.getMessage());
    }

    /**
     * 文件不存在异常
     */
    @ExceptionHandler(FileNotFoundException.class)
    public FormatResponseData error(FileNotFoundException e) {
        return FormatResponseData.error(e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public FormatResponseData<HashMap<String, String>> error(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        HashMap<String, String> errorMap = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return FormatResponseData.error("参数校验失败", errorMap);
    }
}
