package com.cudo.pixelviewer.operate.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeviceMapper {
    List<Map<String, Object>> getDeviceIpPort();
}
