package com.cudo.pixelviewer.viewer.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewerServiceImpl implements ViewerService {

    final LayerMapper layerMapper;

    final PlaylistMapper playlistMapper;

//    final ViewerMapper viewerMapper;

    @Override
    public Map<String, Object> getPlayInfo(String screenId, String presetId, String layerId) {
        Map<String, Object> resultMap = new HashMap<>();

        Map<String, Object> layerMap = layerMapper.getLayer(layerId);

        if(layerMap != null){
            List<Map<String, Object>> layerObject = layerMapper.getLayerLayerObject(layerId);
            if (layerObject != null) {
                for (Map<String, Object> lo : layerObject) {
                    int type = (int) lo.get("type");

                    switch (type) {
                        case 10:
                            if(layerMap.get("exVideoEn").equals(1)) {
                                Map<String, Object> videoTemp = layerMapper.getLayerObjectExternalVideo((Integer) lo.get("object_id"));
                                resultMap.put("externalVideo", videoTemp);
                            }
                            break;
                        case 20:
                            if(layerMap.get("weatherEn").equals(1)) {
                                Map<String, Object> weatherInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                                resultMap.put("weatherForm", weatherInfoTemp);
                            }
                            break;
                        case 30:
                            if(layerMap.get("subFirstEn").equals(1) || layerMap.get("subSecondEn").equals(1)) {
                                Map<String, Object> subtitleTemp = layerMapper.getLayerObjectExternalSubtitle((Integer) lo.get("object_id"));
                                String temp = (String) subtitleTemp.get("subtitle_style");

                                try {
                                    JSONParser parser = new JSONParser();
                                    Object obj = parser.parse(temp);
                                    JSONArray jsonArrayTemp = (JSONArray) obj;

                                    if(layerMap.get("subFirstEn").equals(0)){
                                        jsonArrayTemp.remove(0);
                                    }
                                    else if(layerMap.get("subSecondEn").equals(0)){
                                        jsonArrayTemp.remove(1);
                                    }
                                    else{
                                        //둘 다 활성화
                                    }
                                    resultMap.put("subtitleStyleArray", jsonArrayTemp);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case 40:
                            if(layerMap.get("airEn").equals(1)) {
                                Map<String, Object> airInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                                resultMap.put("airForm", airInfoTemp);
                            }
                            break;
                        default:
                            log.info("[VIEWER PLAYINFO][UNSUPPORTED TYPE] - [{}]", type);
                            break;
                    }
                }
            }
            Map<String, Object> playlist = playlistMapper.getPlaylist(layerId);

            if(playlist != null) {
                String contentIdList = (String) playlist.get("contentIdList");
                String queryTemp = "(" + contentIdList + ")";

                List<Map<String, Object>> playlistContentList = playlistMapper.getPlaylistContentList(queryTemp);
                if (playlistContentList.size() != 0) {
                    resultMap.put("playlist", playlistContentList);
                }
            }
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }
}