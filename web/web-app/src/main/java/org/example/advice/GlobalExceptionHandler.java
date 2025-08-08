package org.example.advice;

import org.example.common.R;
import org.example.exception.BizException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public R error(Exception e) {
        return R.error(e.getMessage());
    }

    /**
     * 业务处理异常
     */
    @ExceptionHandler(BizException.class)
    public R error(BizException e) {
        return R.error(e);
    }

    /**
     * 数学计算异常
     */
    @ExceptionHandler(ArithmeticException.class)
    public R error(ArithmeticException e) {
        return R.error(e.getMessage());
    }

    /**
     * 文件不存在异常
     */
    @ExceptionHandler(FileNotFoundException.class)
    public R error(FileNotFoundException e) {
        return R.error(e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<HashMap<String, String>> error(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        HashMap<String, String> errorMap = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return R.error("参数校验失败", errorMap);
    }
}
