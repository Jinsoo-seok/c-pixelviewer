package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.SubtitleMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubtitleServiceImpl implements SubtitleService {

    final SubtitleMapper subtitleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postSubtitle(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        if(param.get("type").equals(30)) {
            Integer subtitleCheck = subtitleMapper.postSubtitleValid(param);

            try {
                ObjectMapper mapper = new ObjectMapper();
                String mapperJson = mapper.writeValueAsString(param.get("subtitleStyleArray"));
                param.put("subtitleStyle", mapperJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            // 없으면 insert (LayerObjects, Subtitles)
            if (subtitleCheck == null) {
                int postExternalResult = subtitleMapper.postSubtitle(param);

                if (postExternalResult == 1) { // Success : 1
                    dataMap.put("subtitleId", param.get("subtitleId"));
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    resultMap.put("data", dataMap);
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_SUBTITLE.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_SUBTITLE.getMessage());
                }
            }
            // 있으면 update
            else {
                param.put("objectId", subtitleCheck);
                int putExternalResult = subtitleMapper.putSubtitle(param);

                if (putExternalResult == 1) { // Success : 1
                    dataMap.put("subtitleId", param.get("subtitleId"));
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    resultMap.put("data", dataMap);
                } else {
                    resultMap.put("code", ResponseCode.FAIL_UPDATE_SUBTITLE.getCode());
                    resultMap.put("message", ResponseCode.FAIL_UPDATE_SUBTITLE.getMessage());
                }
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_UNSUPPORTED_TYPE_SUBTITLE.getCode());
            resultMap.put("message", ResponseCode.FAIL_UNSUPPORTED_TYPE_SUBTITLE.getMessage());
        }
        return resultMap;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchSubtitleName(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int subtitleCheck = subtitleMapper.patchSubtitleNameValid(param);

        if(subtitleCheck == 1){  // Exist : 1
            int patchSubtitleNameResult = subtitleMapper.patchSubtitleName(param);

            if(patchSubtitleNameResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_SUBTITLE.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_SUBTITLE.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_SUBTITLE.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_SUBTITLE.getMessage());
        }
        return resultMap;
    }

}
