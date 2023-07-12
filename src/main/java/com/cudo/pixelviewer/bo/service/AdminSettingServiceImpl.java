package com.cudo.pixelviewer.bo.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.operate.mapper.LedMapper;
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

    final AdminSettingMapper adminSettingMapper;

    final LedMapper ledMapper;

    final static String PRESET_NAME_PREFIX = "프리셋";

    @Override
    public Map<String, Object> getAdminSettingList() {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();

        List<Map<String, Object>> adminSettingVoList = adminSettingMapper.getAdminSettingList();


        for (Map<String, Object> element : adminSettingVoList) {
            String settingKey = (String) element.get("settingKey");
            Object settingValue = element.get("settingValue");

            if (settingKey.equals("imgDefaultPlaytime") || settingKey.equals("ledPresetCount")) {
                returnMap.put(settingKey, Integer.valueOf((String) settingValue));
            }
            else if (settingKey.equals("coords")) {
                String temp = (String) settingValue;
                String[] tempSplit = temp.split(",");

                returnMap.put("nx", Integer.valueOf(tempSplit[0]));
                returnMap.put("ny", Integer.valueOf(tempSplit[1]));
            }
            else if (settingValue.equals("1") || settingValue.equals("0") ) {
                boolean boolValue = settingValue.equals("1");
                returnMap.put(settingKey, boolValue);
            }
            else {
                returnMap.put(settingKey, settingValue.toString());
            }
        }

        if(adminSettingVoList.size() > 0){
            resultMap.put("data", returnMap);
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
                queryMap.put("settingValue", value.equals(true) ? "1" : "0");
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

        if(param.containsKey("ledPresetCount")) {
            List<Map<String, Object>> presetList = new ArrayList<>();
            Integer presetCount = Integer.parseInt(String.valueOf(param.get("ledPresetCount")));

            if (presetCount > 0) {
                for (int i = 0; i < presetCount; i++) {
                    Map<String, Object> presetInfo = new HashMap<>();

                    presetInfo.put("presetNumber", String.format("%02X", i));
                    presetInfo.put("presetName", PRESET_NAME_PREFIX + (i + 1));
                    presetList.add(presetInfo);
                }
                Integer deletePresetCount = ledMapper.deleteLedPreset();
                Integer insertPresetCount = ledMapper.postLedPreset(presetList);
            }
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