package org.example.advice;

import org.example.common.R;
import org.example.exception.BizException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;

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
}
