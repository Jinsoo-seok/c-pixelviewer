package com.cudo.pixelviewer.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PresetStatusRunVo implements Serializable {

    private static final long serialVersionUID = 7368358119494497992L;

    private Integer presetId;

    private Integer screenId;

    private String presetNm;

    private String presetStatus;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyyMMddHHmmss")
    private Date updateDate;
}