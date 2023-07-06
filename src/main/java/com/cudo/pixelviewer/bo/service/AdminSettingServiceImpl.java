package com.cudo.pixelviewer.bo.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.DisplaySettingVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

//        List<AdminSettingVo> adminSettingVoList = adminSettingMapper.getAdminSettingList();
        List<Map<String, Object>> adminSettingVoList = adminSettingMapper.getAdminSettingList();
        Map<String, Object> returnMap = new HashMap<>();


        for (Map<String, Object> element : adminSettingVoList) {
            String settingKey = (String) element.get("settingKey");
            Object settingValue = element.get("settingValue");

            if (settingKey.equals("imgDefaultPlaytime") || settingKey.equals("ledPresetCount")) {
                Integer temp = Integer.parseInt((String) settingValue);
                returnMap.put(settingKey, temp);
            }
            else if (settingValue.equals("1") || settingValue.equals("0") ) {
                if(settingValue.equals("1")){
                    returnMap.put(settingKey, true);
                }
                else if(settingValue.equals("0")){
                    returnMap.put(settingKey, false);
                }
            }
            else {
                returnMap.put(settingKey, settingValue.toString());
            }
        }

        if(adminSettingVoList.size() > 0){
            resultMap.put("data", returnMap);
//            resultMap.put("data", adminSettingVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putAdminSetting(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> tempArray = new ArrayList<>();

        param.put("coords", param.get("nx") + "," + param.get("ny"));
        String[] removeKey = {"nx", "ny"};
        for (String key : removeKey) {
            param.remove(key);
        }

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            Map<String, Object> queryMap = new HashMap<>();
            String key = entry.getKey();
            Object value = entry.getValue();

            queryMap.put("settingKey", key);

            if (value instanceof Boolean) {
                if(value.equals(true)){
                    queryMap.put("settingValue", "1");
                }
                else{
                    queryMap.put("settingValue", "0");
                }
            }
            else if (value instanceof Integer) {
                queryMap.put("settingValue", value.toString());
            }
            else if (value instanceof String) {
                queryMap.put("settingValue", value);
            }
            else {
                System.out.println(key + " is of an unknown type: " + value);
            }
            tempArray.add(queryMap);
        }

        int putAdminSettingResult = adminSettingMapper.putAdminSetting(tempArray);

        if (putAdminSettingResult == 1) { // Success : 1
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else {
            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_VALUES;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> patchLayerTopMost(Map<String, Object> param) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        Boolean type = (Boolean) param.get("type");
//
//        int patchLayerTopMostResult = adminSettingMapper.patchLayerTopMost(param);
//
//        if (patchLayerTopMostResult == 1) { // Success : 1
//            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//
//            String status = (type == true) ? ENABLED_TEXT : DISABLED_TEXT;
//            resultMap.put("data", status);
//        }
//        else {
//            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_VIEW_TOP_MOST_EN;
//            resultMap.put("code", failCode.getCode());
//            resultMap.put("message", failCode.getMessage());
//        }
//        return resultMap;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> patchTempHumi(Map<String, Object> param) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        Boolean type = (Boolean) param.get("type");
//
//        int patchLayerTopMostResult = adminSettingMapper.patchTempHumi(param);
//
//        if (patchLayerTopMostResult == 1) { // Success : 1
//            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//
//            String status = (type == true) ? ENABLED_TEXT : DISABLED_TEXT;
//            resultMap.put("data", status);
//        }
//        else {
//            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_VIEW_TEMP_HUMI_EN;
//            resultMap.put("code", failCode.getCode());
//            resultMap.put("message", failCode.getMessage());
//        }
//        return resultMap;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> patchControlType(Map<String, Object> param) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        String type = (String) param.get("type");
//
//        String unsupportedTypeText = "Possible Types : TCP, USB";
//        String[] controlTypeList = {"TCP", "USB"};
//
//        if(type.equals("TCP") || type.equals("USB") ) {
//
//            int patchLayerTopMostResult = adminSettingMapper.patchControlType(param);
//
//            if (patchLayerTopMostResult == 1) { // Success : 1
//                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//
//                resultMap.put("data", type);
//            }
//            else {
//                ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_COMM_TYPE;
//                resultMap.put("code", failCode.getCode());
//                resultMap.put("message", failCode.getMessage());
//            }
//        }
//        else{
//            ResponseCode unsupportedCode = ResponseCode.FAIL_UNSUPPORTED_TYPE_SETTING;
//            resultMap.put("code", unsupportedCode.getCode());
//            resultMap.put("message", unsupportedCode.getMessage());
//            resultMap.put("data", unsupportedTypeText);
//        }
//        return resultMap;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> patchLedPresetEnable(Map<String, Object> param) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        Boolean type = (Boolean) param.get("type");
//
//        int patchLayerTopMostResult = adminSettingMapper.patchLedPresetEnable(param);
//
//        if (patchLayerTopMostResult == 1) { // Success : 1
//            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//
//            String status = (type == true) ? ENABLED_TEXT : DISABLED_TEXT;
//            resultMap.put("data", status);
//        }
//        else {
//            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_PRESET_EN;
//            resultMap.put("code", failCode.getCode());
//            resultMap.put("message", failCode.getMessage());
//        }
//        return resultMap;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> patchLedPresetCount(Map<String, Object> param) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        int patchLayerTopMostResult = adminSettingMapper.patchLedPresetCount(param);
//
//        if (patchLayerTopMostResult == 1) { // Success : 1
//            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//            resultMap.put("data", param.get("presetCount"));
//        } else {
//            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_PRESET_COUNT;
//            resultMap.put("code", failCode.getCode());
//            resultMap.put("message", failCode.getMessage());
//        }
//        return resultMap;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> patchLedInputEnable(Map<String, Object> param) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        Boolean type = (Boolean) param.get("type");
//
//        int patchLayerTopMostResult = adminSettingMapper.patchLedInputEnable(param);
//
//        if (patchLayerTopMostResult == 1) { // Success : 1
//            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//
//            String status = (type == true) ? ENABLED_TEXT : DISABLED_TEXT;
//            resultMap.put("data", status);
//        }
//        else {
//            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_LED_INPUT_SELECT_EN;
//            resultMap.put("code", failCode.getCode());
//            resultMap.put("message", failCode.getMessage());
//        }
//        return resultMap;
//    }


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

        int deleteDisplayInfoValid = adminSettingMapper.deleteDisplayInfoValid(param);

        if(deleteDisplayInfoValid == 1) {
            String displayUsedCheck = adminSettingMapper.displayUsedCheck(param);

            if(displayUsedCheck == null){
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
                resultMap.put("data", displayUsedCheck);
                ResponseCode failCode = ResponseCode.FAIL_USED_DISPLAY_SETTING;
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