package com.cudo.pixelviewer.bo.service;

import com.cudo.pixelviewer.bo.mapper.LedconMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.LedconVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LedconServiceImpl implements LedconService {

    final LedconMapper ledconMapper;

    @Override
    public Map<String, Object> getLedconList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<LedconVo> ledconVoList = ledconMapper.getLedconList();

        if(ledconVoList.size() > 0){
            resultMap.put("data", ledconVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postLedcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

//        int ledconCheck = ledconMapper.postLedconValid(param);
        int ledconCheck = 0;

        if(ledconCheck == 0){ // Not Exist : 0
            int postLedconResult = ledconMapper.postLedcon(param);

            if(postLedconResult == 1){ // Success : 1
                dataMap.put("ledControllerId", param.get("ledControllerId"));
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                resultMap.put("data", dataMap);
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_INSERT_LEDCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_INSERT_LEDCON.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_DUPLICATE_LEDCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_DUPLICATE_LEDCON.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteLedcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int ledconCheck = ledconMapper.deleteLedconValid(param);

        if(ledconCheck == 1){  // Exist : 1
            int deleteLedconResult = ledconMapper.deleteLedcon(param);

            if(deleteLedconResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_DELETE_LEDCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_DELETE_LEDCON.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_LEDCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_LEDCON.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putLedcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int ledconCheck = ledconMapper.putLedconValid(param);

        if(ledconCheck == 1){  // Exist : 1
            int putLedconResult = ledconMapper.putLedcon(param);

            if(putLedconResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_LEDCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_LEDCON.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_LEDCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_LEDCON.getMessage());
        }
        return resultMap;
    }
}