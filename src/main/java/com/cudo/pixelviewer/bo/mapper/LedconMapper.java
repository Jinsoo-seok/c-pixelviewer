package com.cudo.pixelviewer.bo.mapper;

import com.cudo.pixelviewer.vo.LedconVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LedconMapper {

    List<LedconVo> getLedconList();

    int postLedconValid(Map<String, Object> param);
    int postLedcon(Map<String, Object> param);

    int deleteLedconValid(Map<String, Object> param);
    int deleteLedcon(Map<String, Object> param);

    int putLedconValid(Map<String, Object> param);
    int putLedcon(Map<String, Object> param);

    List<String> postIpLedPwrValid(String ip);
    Map<String, Object> putLedConValid(Integer ledControllerId);

}