package com.cudo.pixelviewer.setting.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface SettingMapper {

    Map<String, Object> getSetting ();

    int patchSettingImageDefaultPlaytime(Map<String, Object> param);

}
