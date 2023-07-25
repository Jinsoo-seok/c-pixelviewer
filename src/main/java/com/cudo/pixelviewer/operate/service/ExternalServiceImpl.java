package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.ExternalMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.ExternalVideoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {

    final ExternalMapper externalMapper;

    final PresetMapper presetMapper;


    @Override
    public Map<String, Object> getExternalVideo(String externalId) {
        Map<String, Object> resultMap = new HashMap<>();

        ExternalVideoVo externalVideoVo = externalMapper.getExternalVideo(externalId);

        if(externalVideoVo != null){
            resultMap.put("data", externalVideoVo);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postExternalVideo(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        if(param.get("type").equals(10)) {
            Integer externalCheck = externalMapper.postExternalVideoValid(param);

            // 없으면 insert (LayerObjects, ExternalVideo)
            if (externalCheck == null) {
                int postExternalResult = externalMapper.postExternalVideo(param);

                if (postExternalResult == 1) {
                    dataMap.put("externalId", param.get("externalId"));
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    resultMap.put("data", dataMap);
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_EXTERNAL_VIDEO.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_EXTERNAL_VIDEO.getMessage());
                }
            }
            // 있으면 update
            else {
                param.put("objectId", externalCheck);
                int putExternalResult = externalMapper.putExternalVideo(param);

                if (putExternalResult == 1) {
                    int refreshPresetUpdateDate = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                    dataMap.put("externalId", param.get("externalId"));
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    resultMap.put("data", dataMap);
                } else {
                    resultMap.put("code", ResponseCode.FAIL_UPDATE_EXTERNAL_VIDEO.getCode());
                    resultMap.put("message", ResponseCode.FAIL_UPDATE_EXTERNAL_VIDEO.getMessage());
                }
            }
            int refreshPresetUpdateDateResult = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_UNSUPPORTED_TYPE_EXTERNAL_VIDEO.getCode());
            resultMap.put("message", ResponseCode.FAIL_UNSUPPORTED_TYPE_EXTERNAL_VIDEO.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postExternalInfo(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        if(param.get("type").equals(20) || param.get("type").equals(40)) {
            Integer externalCheck = externalMapper.postExternalInfoValid(param);

            // 없으면 insert (LayerObjects, External_info)
            if (externalCheck == null) {
                int postExternalResult = externalMapper.postExternalInfo(param);

                if (postExternalResult == 1) {
                    dataMap.put("externalId", param.get("externalId"));
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    resultMap.put("data", dataMap);
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_EXTERNAL_INFO.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_EXTERNAL_INFO.getMessage());
                }
            }
            // 있으면 update
            else {
                param.put("objectId", externalCheck);
                int putExternalResult = externalMapper.putExternalInfo(param);

                if (putExternalResult == 1) {
                    dataMap.put("externalId", param.get("externalId"));
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    resultMap.put("data", dataMap);
                } else {
                    resultMap.put("code", ResponseCode.FAIL_UPDATE_EXTERNAL_INFO.getCode());
                    resultMap.put("message", ResponseCode.FAIL_UPDATE_EXTERNAL_INFO.getMessage());
                }
            }
            int refreshPresetUpdateDateResult = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_UNSUPPORTED_TYPE_EXTERNAL_INFO.getCode());
            resultMap.put("message", ResponseCode.FAIL_UNSUPPORTED_TYPE_EXTERNAL_INFO.getMessage());
        }
        return resultMap;
    }
}
