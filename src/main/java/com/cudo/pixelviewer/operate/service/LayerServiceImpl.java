package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LayerServiceImpl implements LayerService {

    final LayerMapper layerMapper;

    final PresetMapper presetMapper;

    @Override
    public Map<String, Object> getLayerList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> layerList = layerMapper.getLayerList();

        if(layerList.size() > 0){
            for (Map<String, Object> layer : layerList) {
                for (String key : layer.keySet()) {
                    if (key.endsWith("En")) {
                        Object value = layer.get(key);
                        if (value instanceof String) {
                            String strValue = (String) value;
                            if (strValue.equals("1") || strValue.equals("0")) {
                                boolean booleanValue = strValue.equals("1");
                                layer.put(key, booleanValue);
                            }
                        } else if (value instanceof Integer) {
                            int intValue = (int) value;
                            if (intValue == 1 || intValue == 0) {
                                boolean booleanValue = intValue == 1;
                                layer.put(key, booleanValue);
                            }
                        }
                    }
                }

                List<Integer> playList = layerMapper.getLayerListPlayList((Integer) layer.get("layerId"));
                layer.put("playList", playList);
            }
            resultMap.put("data", layerList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getLayer(String layerId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        Map<String, Object> layerMap = layerMapper.getLayer(layerId);

        for (String key : layerMap.keySet()) {
            if (key.endsWith("En")) {
                Object value = layerMap.get(key);
                if (value instanceof String) {
                    String strValue = (String) value;
                    if (strValue.equals("1") || strValue.equals("0")) {
                        boolean booleanValue = strValue.equals("1");
                        layerMap.put(key, booleanValue);
                    }
                } else if (value instanceof Integer) {
                    int intValue = (int) value;
                    if (intValue == 1 || intValue == 0) {
                        boolean booleanValue = intValue == 1;
                        layerMap.put(key, booleanValue);
                    }
                }
            }
        }

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
                            subMap.put("type", "외부영상");
                            subMap.put("data", videoTemp);
                            layerObjectList.add(subMap);
                            break;
                        case 20:
                            Map<String, Object> weatherInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                            subMap.put("type", "외부정보(날씨)");
                            subMap.put("data", weatherInfoTemp);
                            layerObjectList.add(subMap);
                            break;
                        case 30:
                            Map<String, Object> subtitleTemp = layerMapper.getLayerObjectExternalSubtitle((Integer) lo.get("object_id"));
                            String temp = (String) subtitleTemp.get("subtitleStyle");

                            try {
                                JSONParser parser = new JSONParser();
                                Object obj = parser.parse(temp);
                                subtitleTemp.put("subtitleStyle", obj);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            subMap.put("type", "자막");
                            subMap.put("data", subtitleTemp);
                            layerObjectList.add(subMap);
                            break;
                        case 40:
                            Map<String, Object> airInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                            subMap.put("type", "외부정보(먼지)");
                            subMap.put("data", airInfoTemp);
                            layerObjectList.add(subMap);
                            break;
                        default:
                            log.error("type Unknown");
                            break;
                    }
                }
                layerMap.put("layerObjectList", layerObjectList);
            }

            resultMap.put("data", layerMap);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postLayer(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        int layerCheck = 0;

        if(layerCheck == 0){
            int postLayerResult = layerMapper.postLayer(param);

            if(postLayerResult == 1){
                dataMap.put("layerId", param.get("layerId"));
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                resultMap.put("data", dataMap);
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_INSERT_LAYER.getCode());
                resultMap.put("message", ResponseCode.FAIL_INSERT_LAYER.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_DUPLICATE_LAYER.getCode());
            resultMap.put("message", ResponseCode.FAIL_DUPLICATE_LAYER.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteLayer(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int layerCheck = layerMapper.deleteLayerValid(param);

        if(layerCheck == 1){
            int deleteLayerResult = layerMapper.deleteLayer(param);

            if(deleteLayerResult > 0){
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_DELETE_LAYER.getCode());
                resultMap.put("message", ResponseCode.FAIL_DELETE_LAYER.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_LAYER.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_LAYER.getMessage());
        }
        return resultMap;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putLayer(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int layerCheck = layerMapper.putLayerValid(param);

        if(layerCheck == 1){
            int putLayerResult = layerMapper.putLayer(param);

            if(putLayerResult == 1){

                //Preset Version UPDATE
                int refreshPresetUpdateDateResult = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                if(refreshPresetUpdateDateResult > 0){
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                }
                else{
                    resultMap.put("code", ResponseCode.FAIL_UPDATE_PRESET.getCode());
                    resultMap.put("message", ResponseCode.FAIL_UPDATE_PRESET.getMessage());
                }
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_LAYER.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_LAYER.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_LAYER.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_LAYER.getMessage());
        }
        return resultMap;
    }
}
