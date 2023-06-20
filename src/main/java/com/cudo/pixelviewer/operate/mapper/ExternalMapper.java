package com.cudo.pixelviewer.operate.mapper;

import com.cudo.pixelviewer.vo.ExternalVideoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface ExternalMapper {

    ExternalVideoVo getExternalVideo(String externalId);

    Integer postExternalVideoValid(Map<String, Object> param);
    int postExternalVideo(Map<String, Object> param);
    int putExternalVideo(Map<String, Object> param);

    Integer postExternalInfoValid(Map<String, Object> param);
    int postExternalInfo(Map<String, Object> param);
    int putExternalInfo(Map<String, Object> param);

}