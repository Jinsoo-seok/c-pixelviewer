package com.cudo.pixelviewer.bo.service;

import com.cudo.pixelviewer.bo.mapper.PwrconMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.PwrconVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PwrconServiceImpl implements PwrconService {

    final PwrconMapper pwrconMapper;

    @Override
    public Map<String, Object> getPwrconList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<PwrconVo> pwrconVoList = pwrconMapper.getPwrconList();

        if(pwrconVoList.size() > 0){
            resultMap.put("data", pwrconVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postPwrcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

//        int pwrconCheck = pwrconMapper.postPwrconValid(param);
        int pwrconCheck = 0;

        if(pwrconCheck == 0){ // Not Exist : 0
            int postPwrconResult = pwrconMapper.postPwrcon(param);

            if(postPwrconResult == 1){ // Success : 1
                dataMap.put("condeviceId", param.get("condeviceId"));
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                resultMap.put("data", dataMap);
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_INSERT_PWRCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_INSERT_PWRCON.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_DUPLICATE_PWRCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_DUPLICATE_PWRCON.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deletePwrcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int pwrconCheck = pwrconMapper.deletePwrconValid(param);

        if(pwrconCheck == 1){  // Exist : 1
            int deletePwrconResult = pwrconMapper.deletePwrcon(param);

            if(deletePwrconResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_DELETE_PWRCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_DELETE_PWRCON.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PWRCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PWRCON.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putPwrcon(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int pwrconCheck = pwrconMapper.putPwrconValid(param);

        if(pwrconCheck == 1){  // Exist : 1
            int patchPwrconNameResult = pwrconMapper.putPwrcon(param);

            if(patchPwrconNameResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_PWRCON.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_PWRCON.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PWRCON.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PWRCON.getMessage());
        }
        return resultMap;
    }
}