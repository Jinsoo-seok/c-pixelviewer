package com.cudo.pixelviewer.external.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExternalsMapper {

    List<Map<String, Object>> getExternalInfos ();

}
