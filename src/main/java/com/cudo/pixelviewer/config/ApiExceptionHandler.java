package com.cudo.pixelviewer.config;

import com.cudo.pixelviewer.vo.ResponseVo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseVo> noHandlerFoundException(NoHandlerFoundException ex) {
        ResponseVo responseVO = new ResponseVo();
        responseVO.setCode(HttpStatus.NOT_FOUND.value());
        responseVO.setMessage(ex.getMessage());

        return new ResponseEntity(responseVO, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseVo> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ResponseVo responseVO = new ResponseVo();
        responseVO.setCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        responseVO.setMessage(ex.getMessage());

        return new ResponseEntity(responseVO, HttpStatus.METHOD_NOT_ALLOWED);
    }

}
