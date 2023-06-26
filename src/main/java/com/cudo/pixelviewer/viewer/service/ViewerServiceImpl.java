package com.cudo.pixelviewer.viewer.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ViewerServiceImpl implements ViewerService {

    final LayerMapper layerMapper;

//    final ViewerMapper viewerMapper;

    @Override
    public Map<String, Object> getPlayInfo(String screenId, String presetId, String layerId) {
        Map<String, Object> resultMap = new HashMap<>();

        Map<String, Object> layerMap = layerMapper.getLayer(layerId);

        if(layerMap != null){
            List<Map<String, Object>> layerObject = layerMapper.getLayerLayerObject(layerId);
            if (layerObject != null) {
                ArrayList<Map<String, Object>> layerObjectList = new ArrayList<>();
                for (Map<String, Object> lo : layerObject) {
                    int type = (int) lo.get("type");
                    Map<String, Object> subMap = new HashMap<>();

                    switch (type) {
                        case 10:
                            Map<String, Object> videoTemp = layerMapper.getLayerObjectExternalVideo((Integer) lo.get("object_id"));
                            resultMap.put("externalVideo", videoTemp);
//                            subMap.put("type", "외부영상");
//                            subMap.put("data", videoTemp);

//                            layerObjectList.add(subMap);
                            break;
                        case 20:
                            Map<String, Object> weatherInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                            resultMap.put("weatherForm", weatherInfoTemp);
//                            subMap.put("type", "외부정보");
//                            subMap.put("data", infoTemp);
//                            layerObjectList.add(subMap);
                            break;
                        case 30:
                            Map<String, Object> subtitleTemp = layerMapper.getLayerObjectExternalSubtitle((Integer) lo.get("object_id"));
                            String temp = (String) subtitleTemp.get("subtitle_style");

                            try {
                                JSONParser parser = new JSONParser();
                                Object obj = parser.parse(temp);
//                                subtitleTemp.put("subtitleStyle", obj);
                                resultMap.put("subtitleStyleArray", obj);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
//                            resultMap.put("subtitleStyleArray", subtitleTemp);

//                            subMap.put("type", "자막");
//                            subMap.put("data", subtitleTemp);
//                            layerObjectList.add(subMap);
                            break;
                        case 40:
                            Map<String, Object> airInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                            resultMap.put("airForm", airInfoTemp);
//                            subMap.put("type", "외부정보(먼지)");
//                            subMap.put("data", dustInfoTemp);
//                            layerObjectList.add(subMap);
                            break;
                        default:
                            System.out.println("type Unknown");
                            // TODO : 예외처리
                            // 알 수 없는 타입에 대한 처리 로직 추가
                            break;
                    }
                }
//                layerMap.put("layerObjectList", layerObjectList);
            }

//            resultMap.put("data", layerMap);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            
            // TODO : 레이어의 y/n 체크 추가
            // TODO : PlayList 추가
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }
}