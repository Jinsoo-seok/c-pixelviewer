package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LedconVo implements Serializable {

    private static final long serialVersionUID = 803513035433603651L;

    private Integer ledControllerId;
    private Integer port;
    private String modelNm;
    private String version;
    private String serialNo;
    private String firmwareVer;
    private Integer state;
    private String spec1;
    private String spec2;
    private String spec3;
    private Integer connectFl;
    private Date useDt;
    private Date regDt;
    private Date chgDt;
    private Date updDt;
    private String videoInputMode;
    private String inputResolutionWidth;
    private String inputResolutionHeight;
    private Integer frame;
    private Integer colorDepth;
    private String colorMode;
    private Integer hdrEn;
    private Integer preset;
    private Integer brightness;
    private Integer colorTemp;
    private String ip;

}