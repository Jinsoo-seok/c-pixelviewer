package com.cudo.pixelviewer.bo.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.AdminSettingVo;
import com.cudo.pixelviewer.vo.DisplaySettingVo;
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

        Boolean type = (Boolean) param.get("type");

        int patchLayerTopMostResult = adminSettingMapper.patchLayerTopMost(param);

        if (patchLayerTopMostResult == 1) { // Success : 1
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

            String status = (type == true) ? ENABLED_TEXT : DISABLED_TEXT;
            resultMap.put("data", status);
        }
        else {
            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_VIEW_TOP_MOST_EN;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchTempHumi(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        Boolean type = (Boolean) param.get("type");

        int patchLayerTopMostResult = adminSettingMapper.patchTempHumi(param);

        if (patchLayerTopMostResult == 1) { // Success : 1
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

            String status = (type == true) ? ENABLED_TEXT : DISABLED_TEXT;
            resultMap.put("data", status);
        }
        else {
            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_VIEW_TEMP_HUMI_EN;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchControlType(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        String type = (String) param.get("type");

        String unsupportedTypeText = "Possible Types : TCP, USB";
        String[] controlTypeList = {"TCP", "USB"};

        if(type.equals("TCP") || type.equals("USB") ) {

            int patchLayerTopMostResult = adminSettingMapper.patchControlType(param);

            if (patchLayerTopMostResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

                resultMap.put("data", type);
            }
            else {
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

        Boolean type = (Boolean) param.get("type");

        int patchLayerTopMostResult = adminSettingMapper.patchLedPresetEnable(param);

        if (patchLayerTopMostResult == 1) { // Success : 1
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

            String status = (type == true) ? ENABLED_TEXT : DISABLED_TEXT;
            resultMap.put("data", status);
        }
        else {
            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_PRESET_EN;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
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

        Boolean type = (Boolean) param.get("type");

        int patchLayerTopMostResult = adminSettingMapper.patchLedInputEnable(param);

        if (patchLayerTopMostResult == 1) { // Success : 1
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

            String status = (type == true) ? ENABLED_TEXT : DISABLED_TEXT;
            resultMap.put("data", status);
        }
        else {
            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_INPUT_SELECT_EN;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }


    @Override
    public Map<String, Object> getDisplayInfoList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<DisplaySettingVo> displaySettingVoList = adminSettingMapper.getDisplayInfoList();

        if(displaySettingVoList.size() > 0){
            resultMap.put("data", displaySettingVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getDisplayInfo(String displayId) {
        Map<String, Object> resultMap = new HashMap<>();

        DisplaySettingVo displaySettingVo = adminSettingMapper.getDisplayInfo(displayId);

        if(displaySettingVo != null){
            resultMap.put("data", displaySettingVo);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postDisplayInfo(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int patchLayerTopMostResult = adminSettingMapper.postDisplayInfo(param);

        if (patchLayerTopMostResult == 1) { // Success : 1
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        } else {
            ResponseCode failCode = ResponseCode.FAIL_INSERT_DISPLAY_SETTING;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putDisplayInfo(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int putDisplayInfoValid = adminSettingMapper.putDisplayInfoValid(param);

        if(putDisplayInfoValid == 1) {
            int patchLayerTopMostResult = adminSettingMapper.putDisplayInfo(param);

            if (patchLayerTopMostResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

            } else {
                ResponseCode failCode = ResponseCode.FAIL_UPDATE_DISPLAY_SETTING;
                resultMap.put("code", failCode.getCode());
                resultMap.put("message", failCode.getMessage());
            }
        }
        else{
            ResponseCode failCode = ResponseCode.FAIL_NOT_EXIST_DISPLAY_SETTING;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteDisplayInfo(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int putDisplayInfoValid = adminSettingMapper.deleteDisplayInfoValid(param);

        if(putDisplayInfoValid == 1) {
            int patchLayerTopMostResult = adminSettingMapper.deleteDisplayInfo(param);

            if (patchLayerTopMostResult == 1) { // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

            } else {
                ResponseCode failCode = ResponseCode.FAIL_DELETE_DISPLAY_SETTING;
                resultMap.put("code", failCode.getCode());
                resultMap.put("message", failCode.getMessage());
            }
        }
        else{
            ResponseCode failCode = ResponseCode.FAIL_NOT_EXIST_DISPLAY_SETTING;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }
}