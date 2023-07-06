package com.cudo.pixelviewer.externals.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExternalsMapper {

    List<Map<String, Object>> getExternalInfos ();

    int putExternalsInfos(String type, String info);

}
