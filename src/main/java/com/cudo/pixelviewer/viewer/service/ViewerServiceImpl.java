package com.cudo.pixelviewer.viewer.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.externals.mapper.ExternalsMapper;
import com.cudo.pixelviewer.operate.mapper.LayerMapper;
import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.PresetVo;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewerServiceImpl implements ViewerService {

    final PresetMapper presetMapper;

    final LayerMapper layerMapper;

    final PlaylistMapper playlistMapper;

    final ExternalsMapper externalsMapper;

    final AdminSettingMapper adminSettingMapper;

//    final ViewerMapper viewerMapper;

    @Override
    public Map<String, Object> getPlayInfo(String screenId, String presetId, String layerId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

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
                                dataMap.put("externalVideo", exVideoMap);
                            }
                            break;
                        case 20:
                            if(layerMap.get("weatherEn").equals(1)) {
                                Map<String, Object> weatherInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                                String updateDate = convertTimestampToString(weatherInfoTemp.get("updateDate"));

                                Map<String, Object> exInfoWeatherMap = new HashMap<>();
                                exInfoWeatherMap.put("updateDate", updateDate);
                                exInfoWeatherMap.put("weatherFormInfo", createTempMap(weatherInfoTemp));

                                dataMap.put("weatherForm", exInfoWeatherMap);
                            }
                            break;
                        case 30:
                            if(layerMap.get("subFirstEn").equals(1) || layerMap.get("subSecondEn").equals(1)) {
                                Map<String, Object> subtitleTemp = layerMapper.getLayerObjectExternalSubtitle((Integer) lo.get("object_id"));
                                String temp = (String) subtitleTemp.get("subtitleStyle");
                                String updateDate = convertTimestampToString(subtitleTemp.get("updateDate"));

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
                                    dataMap.put("subtitle", subtitleMap);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case 40:
                            if(layerMap.get("airEn").equals(1)) {
                                Map<String, Object> airInfoTemp = layerMapper.getLayerObjectExternalInfo((Integer) lo.get("object_id"));
                                String updateDate = convertTimestampToString(airInfoTemp.get("updateDate"));

                                Map<String, Object> exInfoAirMap = new HashMap<>();
                                exInfoAirMap.put("updateDate", updateDate);
                                exInfoAirMap.put("airFormInfo", createTempMap(airInfoTemp));

                                dataMap.put("airForm", exInfoAirMap);
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
                Map<String, Object> playlistResultMap = new HashMap<>();
                String contentIdList = (String) playlist.get("contentIdList");
                String queryTemp = "(" + contentIdList + ")";
                List<Map<String, Object>> tempContentIdList = new ArrayList<>();

                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(contentIdList);
                    playlist.put("contentIdList", obj);
                    tempContentIdList = (List<Map<String, Object>>) obj;

                    StringBuilder contentIds = new StringBuilder();

                    if(tempContentIdList != null){
                        for (Map<String, Object> item : tempContentIdList) {
                            Long contentId = (Long) item.get("contentId");
                            contentIds.append(contentId).append(",");
                        }
                    }
                    if (contentIds.length() > 0) {
                        contentIds.setLength(contentIds.length() - 1);
                    }
                    queryTemp = "(" + contentIds + ")";
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                String updateDate = convertTimestampToString(playlist.get("updateDate"));

                playlistResultMap.put("playlistId", playlist.get("playlistId"));
                playlistResultMap.put("playlistNm", playlist.get("playlistNm"));
                playlistResultMap.put("updateDate", updateDate);

                List<Map<String, Object>> playlistContentList = playlistMapper.getPlaylistContentList(queryTemp);
                if (playlistContentList.size() != 0) {
                    playlistResultMap.put("playlistContents", playlistContentList);
                    dataMap.put("playlist", playlistResultMap);
                }
                else{
                    dataMap.put("playlist", playlistResultMap);
                }
            }
            if(dataMap != null){
                resultMap.put("data", dataMap);
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
        Map<String, Object> dataMap = new HashMap<>();
        String presetId = String.valueOf(param.get("presetId"));

        PresetVo presetVo = presetMapper.getPreset(presetId);

        if(presetVo != null){
            Date presetUpdateDate = presetVo.getUpdateDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = formatter.format(presetUpdateDate);

            dataMap.put("presetStatus", presetVo.getPresetStatus());
            dataMap.put("playInfoVersion", dateString);

            List<Map<String, Object>> externalInfos = externalsMapper.getExternalInfos();

            if (externalInfos != null) {
                for (Map<String, Object> externalInfo : externalInfos) {
                    String externalType = (String) externalInfo.get("externalType");
                    String externalData = (String) externalInfo.get("externalData");

                    Object obj = new Object();
                    try {
                        JSONParser parser = new JSONParser();
                        obj = parser.parse(externalData);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(obj != null) {
                        if (externalType.equals("날씨")) {
                            dataMap.put("weatherInfo", obj);
                        }
                        else if (externalType.equals("대기")) {
                            dataMap.put("airInfo", obj);
                        }
                    }
                }
            }
            // TODO : 날씨, 대기 정보 조회
            resultMap.put("data", dataMap);

            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> postPreviewImgUpload(String type, String name, MultipartFile file) {
        Map<String, Object> resultMap = new HashMap<>();

        if (file != null && !file.isEmpty()) {
            try {
                String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
                String originalFilename = file.getOriginalFilename();
                String filename = originalFilename.substring(0, originalFilename.lastIndexOf("."));
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filePath = desktopPath + File.separator;

                LocalDateTime localDateTime = LocalDateTime.now();
                String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

                Boolean contentsType = false;
                Boolean weatherType = false;

                switch(type) {
                    // Agent Capture Img
                    case "10": {
                        filePath += "agent" + File.separator + name + extension;
                        break;
                    }
                    // Contents Thumbnails
                    case "20": {
                        contentsType = true;
                        filePath += "thumbnails"  + File.separator + filename + "_" + formattedDateTime + extension;
                        break;
                    }
                    // Weather Img
                    case "30": case "40": case "50":
                    case "60": case "70": case "80":
                    case "90": case "100":
                    {
                        weatherType = true;
                        filePath += "weather"  + File.separator + originalFilename;
                        break;
                    }
                    default: {
                        break;
                    }
                }

                File destFile = new File(filePath);
                FileUtils.copyInputStreamToFile(file.getInputStream(), destFile);

                Boolean weatherImgDB = false;
                if(weatherType){
                    String settingKey = "";
                    if (type.equals("30")) {
                        settingKey = "weatherSunny";
                    } else if (type.equals("40")) {
                        settingKey = "weatherManyCloudy";
                    } else if (type.equals("50")) {
                        settingKey = "weatherCloudy";
                    } else if (type.equals("60")) {
                        settingKey = "weatherRainSnow";
                    } else if (type.equals("70")) {
                        settingKey = "weatherSnow";
                    } else if (type.equals("80")) {
                        settingKey = "weatherRain";
                    } else if (type.equals("90")) {
                        settingKey = "weatherShower";
                    }

                    int weatherImgResult = adminSettingMapper.patchWeatherImg(settingKey, originalFilename);
                    if(weatherImgResult > 0){
                        weatherImgDB = true;
                    }
                }

                if(weatherImgDB) {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("filePath", filePath);
                    resultMap.put("data", dataMap);
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                }
                else{
                    resultMap.put("code", ResponseCode.FAIL_UPDATE_ADMIN_SETTING_WEATHER_IMG.getCode());
                    resultMap.put("message", ResponseCode.FAIL_UPDATE_ADMIN_SETTING_WEATHER_IMG.getMessage());
                }
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

    private Map<String, Object> createTempMap(Map<String, Object> dataMap){
        Map<String, Object> returnMap = new HashMap<>();
        Map<String, Object> tempStyleMap = new HashMap<>();
        Map<String, Object> tempPositionMap = new HashMap<>();

        tempStyleMap.put("fontNm", dataMap.get("fontNm"));
        tempStyleMap.put("fontSize", dataMap.get("fontSize"));
        tempStyleMap.put("forecolor", dataMap.get("forecolor"));

        tempPositionMap.put("posX", dataMap.get("posX"));
        tempPositionMap.put("posY", dataMap.get("posY"));
        tempPositionMap.put("width", dataMap.get("width"));
        tempPositionMap.put("height", dataMap.get("height"));

        returnMap.put("ord", dataMap.get("ord"));
        returnMap.put("type", dataMap.get("type"));
        returnMap.put("position", tempPositionMap);
        returnMap.put("fontStyle", tempStyleMap);

        return returnMap;
    }
}