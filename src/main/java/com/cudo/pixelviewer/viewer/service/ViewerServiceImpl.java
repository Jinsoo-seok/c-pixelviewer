package com.cudo.pixelviewer.viewer.service;

import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                                String updateDate = convertTimestampToString(videoTemp.get("updateDate"));

                                String deleteData = "updateDate";
                                videoTemp.remove(deleteData);

                                Map<String, Object> exVideoMap = new HashMap<>();
                                exVideoMap.put("updateDate", updateDate);
                                exVideoMap.put("externalVideoInfo", videoTemp);
                                resultMap.put("externalVideo", exVideoMap);
                            }
                            break;
                        case 20:
//                            if(layerMap.get("weatherEn").equals(1)) {
//                                Map<String, Object> weatherInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
//                                resultMap.put("weatherForm", weatherInfoTemp);
//                            }
                            break;
                        case 30:
                            if(layerMap.get("subFirstEn").equals(1) || layerMap.get("subSecondEn").equals(1)) {
                                Map<String, Object> subtitleTemp = layerMapper.getLayerObjectExternalSubtitle((Integer) lo.get("object_id"));
                                String temp = (String) subtitleTemp.get("subtitle_style");
                                String updateDate = convertTimestampToString(subtitleTemp.get("update_date"));

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
                                    Map<String, Object> subtitleMap = new HashMap<>();
                                    subtitleMap.put("updateDate", updateDate);
                                    subtitleMap.put("subtitleStyleArray", jsonArrayTemp);
                                    resultMap.put("subtitle", subtitleMap);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case 40:
//                            if(layerMap.get("airEn").equals(1)) {
//                                Map<String, Object> airInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
//                                resultMap.put("airForm", airInfoTemp);
//                            }
                            break;
                        default:
                            log.info("[VIEWER PLAYINFO][UNSUPPORTED TYPE] - [{}]", type);
                            break;
                    }
                }
            }
            Map<String, Object> playlist = playlistMapper.getPlaylist(layerId);

            if(playlist != null) {
                Map<String, Object> playlistResultMap = new HashMap<>();
                String contentIdList = (String) playlist.get("contentIdList");
                String queryTemp = "(" + contentIdList + ")";

                String updateDate = convertTimestampToString(playlist.get("updateDate"));

                playlistResultMap.put("playlistId", playlist.get("playlistId"));
                playlistResultMap.put("playlistNm", playlist.get("playlistNm"));
                playlistResultMap.put("updateDate", updateDate);

                List<Map<String, Object>> playlistContentList = playlistMapper.getPlaylistContentList(queryTemp);
                if (playlistContentList.size() != 0) {
                    playlistResultMap.put("playlistContents", playlistContentList);
                    resultMap.put("playlist", playlistResultMap);
                }
                else{
                    resultMap.put("playlist", playlistResultMap);
                }
            }
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> putUpdateAndHealthCheck(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        // TODO : 규격 확인 필요 >> 이후 진행
        if(true){

            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> postPreviewImgUpload(String screenId, MultipartFile file) {
        Map<String, Object> resultMap = new HashMap<>();

        if (file != null && !file.isEmpty()) {
            try {
                String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filePath = desktopPath + File.separator + screenId + extension;

                File destFile = new File(filePath);
                FileUtils.copyInputStreamToFile(file.getInputStream(), destFile);

                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
                log.error("[paramException][postPreviewImgUpload] - {}", ioException.getMessage());
                resultMap.put("code", ResponseCode.FAIL.getCode());
                resultMap.put("message", ioException.getMessage());
            }
        } else {
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    public static String convertTimestampToString(Object timestampObj) {
        if (timestampObj instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) timestampObj;

            LocalDateTime dateTime = timestamp.toLocalDateTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            return dateTime.format(formatter);
        }
        return null;
    }
}