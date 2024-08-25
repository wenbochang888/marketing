package com.wenbo.marketing.exception;

import com.wenbo.marketing.model.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public CommonResult handleHttpMessageNotReadableException(RuntimeException e) {
		log.warn("e = {}", e.getMessage(), e);
		return CommonResult.fail(e.getMessage());
	}

}
