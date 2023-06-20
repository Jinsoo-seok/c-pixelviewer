package com.cudo.pixelviewer.bo.mapper;

import com.cudo.pixelviewer.vo.PwrconVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PwrconMapper {

    List<PwrconVo> getPwrconList();

    int postPwrconValid(Map<String, Object> param);
    int postPwrcon(Map<String, Object> param);

    int deletePwrconValid(Map<String, Object> param);
    int deletePwrcon(Map<String, Object> param);

    int putPwrconValid(Map<String, Object> param);
    int putPwrcon(Map<String, Object> param);

}