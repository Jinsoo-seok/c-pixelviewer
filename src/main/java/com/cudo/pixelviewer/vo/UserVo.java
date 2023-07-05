package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = -3653979361713261399L;

    private Integer idx;

    private String userName;
    private String userId;
    private String password;

    private String authCode;
    private Integer state;
    private Integer invalidCnt;

    private Date regDt;
    private Date updateDt;
    private String ledKey;

}