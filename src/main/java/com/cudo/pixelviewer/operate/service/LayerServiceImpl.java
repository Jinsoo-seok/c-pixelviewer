package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LayerVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LayerServiceImpl implements LayerService {

    final LayerMapper layerMapper;

    @Override
    public Map<String, Object> getLayerList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<LayerVo> layerVoList = layerMapper.getLayerList();

        if(layerVoList.size() > 0){
            resultMap.put("data", layerVoList);
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

        LayerVo layerVo = layerMapper.getLayer(layerId);

        if(layerVo != null){
            resultMap.put("data", layerVo);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    // TODO : POST 중복 체크
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postLayer(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

//        int layerCheck = layerMapper.postLayerValid(param);
        int layerCheck = 0;

        if(layerCheck == 0){ // Not Exist : 0
            int postLayerResult = layerMapper.postLayer(param);

            if(postLayerResult == 1){ // Success : 1
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

        if(layerCheck == 1){  // Exist : 1
            int deleteLayerResult = layerMapper.deleteLayer(param);

            if(deleteLayerResult == 1){ // Success : 1
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

        if(layerCheck == 1){  // Exist : 1
            int putLayerResult = layerMapper.putLayer(param);

            if(putLayerResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
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
