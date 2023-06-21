package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.DisplayMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.DisplayVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DisplayServiceImpl implements DisplayService {

    final DisplayMapper displayMapper;


    @Override
    public Map<String, Object> getDisplayList(String screenId) {
        Map<String, Object> resultMap = new HashMap<>();

        List<DisplayVo> displayVoList = displayMapper.getDisplayList(screenId);

        if(displayVoList.size() > 0){
            resultMap.put("data", displayVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getDisplay(String displayId) {
        Map<String, Object> resultMap = new HashMap<>();

        DisplayVo displayVo = displayMapper.getDisplay(displayId);

        if(displayVo != null){
            resultMap.put("data", displayVo);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getDisplayPortlist() {
        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> displayPortlist = displayMapper.getDisplayPortlist();

        if(displayPortlist.size() > 0){
            resultMap.put("data", displayPortlist);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchDisplayTestpattern(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int displayCheck = displayMapper.patchDisplayTestpatternValid(param);

        if(displayCheck == 1){  // Exist : 1

            // TODO : +TEST PATTERN LOGIC START
            // TODO : 테스트 패턴에 대한 내용
            int patchDisplayNameResult = displayMapper.patchDisplayTestpattern(param);

            if(patchDisplayNameResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_DISPLAY.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_DISPLAY.getMessage());
            }
            // TODO : TEST PATTERN LOGIC END
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_DISPLAY.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_DISPLAY.getMessage());
        }
        return resultMap;
    }

}