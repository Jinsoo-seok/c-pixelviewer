package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseVo implements Serializable {

    private static final long serialVersionUID = -1505593769061996352L;

    private Integer code;
    private String message;
}
