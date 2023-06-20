package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PwrconVo implements Serializable {

    private static final long serialVersionUID = -2668084646621348141L;

    private Integer condeviceId;

    private String ip;
    private Integer port;

    private String modelNm;

    private String serialNo;

    private Integer mpcmuCnt;
    private Integer dpcmuCnt;

    private String state;
    private String spec1;
    private String spec2;
    private String spec3;

    private Date regDt;
    private Date chgDt;
    private Date updateDt;

    private Integer ledPwr;
    private Integer temp;
    private Integer humi;

    private String ledKey;

}