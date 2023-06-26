package com.cudo.pixelviewer.viewer.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            Map<String, Object> videoTemp = layerMapper.getLayerObjectExternalVideo((Integer) lo.get("object_id"));
                            resultMap.put("externalVideo", videoTemp);
                            break;
                        case 20:
                            Map<String, Object> weatherInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                            resultMap.put("weatherForm", weatherInfoTemp);
                            break;
                        case 30:
                            Map<String, Object> subtitleTemp = layerMapper.getLayerObjectExternalSubtitle((Integer) lo.get("object_id"));
                            String temp = (String) subtitleTemp.get("subtitle_style");

                            try {
                                JSONParser parser = new JSONParser();
                                Object obj = parser.parse(temp);
                                resultMap.put("subtitleStyleArray", obj);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 40:
                            Map<String, Object> airInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                            resultMap.put("airForm", airInfoTemp);
                            break;
                        default:
                            System.out.println("type Unknown");
                            // TODO : 예외처리
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
            
            // TODO : 레이어의 y/n 체크 추가
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }
}