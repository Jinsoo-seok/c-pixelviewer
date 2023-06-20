package com.cudo.pixelviewer.bo.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.AdminSettingVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminSettingServiceImpl implements AdminSettingService {

    private static final String ENABLED_TEXT = "enable";
    private static final String DISABLED_TEXT = "disable";
    private static final String UNSUPPORTED_TYPE_TEXT = "10:enable, 20:disable";


    final AdminSettingMapper adminSettingMapper;

    @Override
    public Map<String, Object> getAdminSettingList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<AdminSettingVo> adminSettingVoList = adminSettingMapper.getAdminSettingList();

        if(adminSettingVoList.size() > 0){
            resultMap.put("data", adminSettingVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchLayerTopMost(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        Integer type = (Integer) param.get("type");

        if (type == 10 || type == 20) {
            int patchLayerTopMostResult = adminSettingMapper.patchLayerTopMost(param);

            if (patchLayerTopMostResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

                String status = (type == 10) ? ENABLED_TEXT : DISABLED_TEXT;
                resultMap.put("data", status);

            } else {
                ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_VIEW_TOP_MOST_EN;
                resultMap.put("code", failCode.getCode());
                resultMap.put("message", failCode.getMessage());
            }
        }
        else{
            ResponseCode unsupportedCode = ResponseCode.FAIL_UNSUPPORTED_TYPE_SETTING;
            resultMap.put("code", unsupportedCode.getCode());
            resultMap.put("message", unsupportedCode.getMessage());
            resultMap.put("data", UNSUPPORTED_TYPE_TEXT);
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchTempHumi(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        Integer type = (Integer) param.get("type");

        if (type == 10 || type == 20) {
            int patchLayerTopMostResult = adminSettingMapper.patchTempHumi(param);

            if (patchLayerTopMostResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

                String status = (type == 10) ? ENABLED_TEXT : DISABLED_TEXT;
                resultMap.put("data", status);

            } else {
                ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_VIEW_TEMP_HUMI_EN;
                resultMap.put("code", failCode.getCode());
                resultMap.put("message", failCode.getMessage());
            }
        }
        else{
            ResponseCode unsupportedCode = ResponseCode.FAIL_UNSUPPORTED_TYPE_SETTING;
            resultMap.put("code", unsupportedCode.getCode());
            resultMap.put("message", unsupportedCode.getMessage());
            resultMap.put("data", UNSUPPORTED_TYPE_TEXT);
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchControlType(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        Integer type = (Integer) param.get("type");
        String unsupportedTypeText = "10:TCP, 20:USB";
        String[] controlTypeList = {"TCP", "USB"};

        if (type == 10 || type == 20) {
            int patchLayerTopMostResult = adminSettingMapper.patchControlType(param);

            if (patchLayerTopMostResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

                String status = (type == 10) ? controlTypeList[0] : controlTypeList[1];
                resultMap.put("data", status);

            } else {
                ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_COMM_TYPE;
                resultMap.put("code", failCode.getCode());
                resultMap.put("message", failCode.getMessage());
            }
        }
        else{
            ResponseCode unsupportedCode = ResponseCode.FAIL_UNSUPPORTED_TYPE_SETTING;
            resultMap.put("code", unsupportedCode.getCode());
            resultMap.put("message", unsupportedCode.getMessage());
            resultMap.put("data", unsupportedTypeText);
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchLedPresetEnable(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        Integer type = (Integer) param.get("type");

        if (type == 10 || type == 20) {
            int patchLayerTopMostResult = adminSettingMapper.patchLedPresetEnable(param);

            if (patchLayerTopMostResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

                String status = (type == 10) ? ENABLED_TEXT : DISABLED_TEXT;
                resultMap.put("data", status);

            } else {
                ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_PRESET_EN;
                resultMap.put("code", failCode.getCode());
                resultMap.put("message", failCode.getMessage());
            }
        }
        else{
            ResponseCode unsupportedCode = ResponseCode.FAIL_UNSUPPORTED_TYPE_SETTING;
            resultMap.put("code", unsupportedCode.getCode());
            resultMap.put("message", unsupportedCode.getMessage());
            resultMap.put("data", UNSUPPORTED_TYPE_TEXT);
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchLedPresetCount(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int patchLayerTopMostResult = adminSettingMapper.patchLedPresetCount(param);

        if (patchLayerTopMostResult == 1) { // Success : 1
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            resultMap.put("data", param.get("presetCount"));
        } else {
            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_PRESET_COUNT;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchLedInputEnable(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        Integer type = (Integer) param.get("type");

        if (type == 10 || type == 20) {
            int patchLayerTopMostResult = adminSettingMapper.patchLedInputEnable(param);

            if (patchLayerTopMostResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

                String status = (type == 10) ? ENABLED_TEXT : DISABLED_TEXT;
                resultMap.put("data", status);

            } else {
                ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_INPUT_SELECT_EN;
                resultMap.put("code", failCode.getCode());
                resultMap.put("message", failCode.getMessage());
            }
        }
        else{
            ResponseCode unsupportedCode = ResponseCode.FAIL_UNSUPPORTED_TYPE_SETTING;
            resultMap.put("code", unsupportedCode.getCode());
            resultMap.put("message", unsupportedCode.getMessage());
            resultMap.put("data", UNSUPPORTED_TYPE_TEXT);
        }
        return resultMap;
    }
}