package com.share.co.kcl.security.demo.processor.advice;

import com.share.co.kcl.security.demo.constants.ResultCode;
import com.share.co.kcl.security.demo.exception.AccountException;
import com.share.co.kcl.security.demo.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorAdvice.class);

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(AccountException.class)
    public Result<Void> accountExceptionHandler(AccountException ex) {
        return Result.error(ResultCode.TOAST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> exceptionHandler(Exception ex) throws Exception {
        LOG.warn("接口处理异常", ex);
        throw ex;
    }

}
