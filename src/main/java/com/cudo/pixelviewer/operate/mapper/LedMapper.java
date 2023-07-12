package com.cudo.pixelviewer.operate.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LedMapper {
    Integer postLedPreset(List<Map<String, Object>> param);
    Integer deleteLedPreset();

    Integer putLedPreset(String presetNumber);

    String getLastLoadPreset();

    List<Map<String, Object>> getLedPresetList();

    Integer putLedPresetName(Map<String, Object> param);
}
