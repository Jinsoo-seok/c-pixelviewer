package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.PresetMapper;
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

    final PresetMapper presetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postSubtitle(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        Boolean YN = false;

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
                int postSubtitleResult = subtitleMapper.postSubtitle(param);

                if (postSubtitleResult == 1) {
                    YN = true;
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_SUBTITLE.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_SUBTITLE.getMessage());
                }
            }
            // 있으면 update
            else {
                param.put("objectId", subtitleCheck);
                int putSubtitleResult = subtitleMapper.putSubtitle(param);

                if (putSubtitleResult == 1) {
                    int refreshPresetUpdateDate = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                    YN = true;
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.put("code", ResponseCode.FAIL_UPDATE_SUBTITLE.getCode());
                    resultMap.put("message", ResponseCode.FAIL_UPDATE_SUBTITLE.getMessage());
                }
            }
            if(YN){
                int postSubtitleLayerResult = subtitleMapper.postSubtitleLayer(param);
                if (postSubtitleLayerResult == 1) {
                    int refreshPresetUpdateDateResult = presetMapper.refreshPresetUpdateDate(param.get("presetId"));

                    dataMap.put("subtitleId", param.get("subtitleId"));
                    resultMap.put("data", dataMap);
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.put("code", ResponseCode.FAIL_UPDATE_SUBTITLE_LAYER.getCode());
                    resultMap.put("message", ResponseCode.FAIL_UPDATE_SUBTITLE_LAYER.getMessage());
                }
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_UNSUPPORTED_TYPE_SUBTITLE.getCode());
            resultMap.put("message", ResponseCode.FAIL_UNSUPPORTED_TYPE_SUBTITLE.getMessage());
        }
        return resultMap;
    }

}
