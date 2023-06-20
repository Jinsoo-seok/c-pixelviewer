package com.cudo.pixelviewer.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ScreenVo implements Serializable {

    private static final long serialVersionUID = 6840738662192251226L;

    private Integer screenId;

    private String screenNm;

    private Integer rowsize;

    private Integer columnsize;
}
