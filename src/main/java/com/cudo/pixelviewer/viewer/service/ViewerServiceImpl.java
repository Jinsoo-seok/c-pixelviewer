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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
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
    
    final Environment environment;

    @Value("${values.protocol}")
    private String protocol;

    @Value("${values.was.ip}")
    private String wasIp;
    @Value("${values.was.port}")
    private String wasPort;

//    private static final String protocol = "http://";
//
//    private static final String wasIp = "106.245.226.42";
//    private static final String wasPort = "9898";

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
//            Map<String, Object> playlist = playlistMapper.getPlaylist(layerId);
            Map<String, Object> playlist = playlistMapper.getPlaylistAboutLayer(layerId);

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
        Map<String, Object> tempPresetMap = new HashMap<>();

        Map<String, Object> tempViewerMap = (Map<String, Object>) param.get("viewerStatus");
        String viewerCurrentTime = (String) tempViewerMap.get("currentTime");
        String viewerStatus = (String) tempViewerMap.get("playState");

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatterViewer = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String localCurrentTime = localDateTime.format(formatterViewer);

        LocalDateTime viewerTime = LocalDateTime.parse(viewerCurrentTime, formatterViewer);
        LocalDateTime localTime = LocalDateTime.parse(localCurrentTime, formatterViewer);

        Duration duration = Duration.between(viewerTime, localTime);
        long minutesDifference = duration.toMinutes();

        Integer diffCheckMin = 3;
        Boolean diffYn = false;

        diffYn = minutesDifference < diffCheckMin;

        Map<String, Object> viewerParam = new HashMap<>();
        viewerParam.put("viewerStatus", viewerStatus);
        viewerParam.put("viewerYn", diffYn);
        viewerParam.put("layerId", param.get("layerId"));

        int updateViewerStatusResult = layerMapper.updateViewerStatus(viewerParam);
        if(updateViewerStatusResult > 0){
            log.info("[SUCCESS] [UPDATE] - Viewer Status");
        }
        else{
            log.info("[FAIL] [UPDATE] - Viewer Status");
        }

        String presetId = String.valueOf(param.get("presetId"));
        PresetVo presetVo = presetMapper.getPreset(presetId);

        if(presetVo != null){
            Date presetUpdateDate = presetVo.getUpdateDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = formatter.format(presetUpdateDate);

            tempPresetMap.put("presetStatus", presetVo.getPresetStatus());
            tempPresetMap.put("playInfoVersion", dateString);
            dataMap.put("preset", tempPresetMap);

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
                            // TODO : [미완료] 시간별 분기처리
                            Map<String, Object> tempMap = (Map<String, Object>) obj;
                            Map<String, Object> weatherMap = (Map<String, Object>) tempMap.get("weather12");

                            String weatherPath = "weather";
                            String weatherImage = weatherImageBranch(weatherMap.get("rainStatus"), weatherMap.get("skyStatus"));

                            weatherMap.put("imagePath", protocol + wasIp + ":" + wasPort + "/" + weatherPath + "/" + adminSettingMapper.getValue(weatherImage));
                            dataMap.put("weatherInfo", weatherMap);
                        }
                        else if (externalType.equals("대기")) {
                            Map<String, Object> tempMap = (Map<String, Object>) obj;

                            String airImage = airImageBranch(tempMap.get("pm10Grade"));
                            String airPath = "air";

                            tempMap.put("imagePath", protocol + wasIp + ":" + wasPort + "/" + airPath + "/" + airImage + ".jpg");
                            dataMap.put("airInfo", obj);
                        }
                    }
                }
            }
            resultMap.put("data", dataMap);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
//        return resultMap;
        return dataMap;
    }

    @Override
    public Map<String, Object> postPreviewImgUpload(String type, String name, MultipartFile file) {
        Map<String, Object> resultMap = new HashMap<>();
        
        String os = environment.getProperty("os.name");
        System.out.println("os = " + os);

        if (file != null && !file.isEmpty()) {
            try {
                String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
                String originalFilename = file.getOriginalFilename();
                String filename = originalFilename.substring(0, originalFilename.lastIndexOf("."));
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filePath = desktopPath + File.separator;

                LocalDateTime localDateTime = LocalDateTime.now();
                String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

                Boolean agentCaptureType = false;
                Boolean contentsType = false;
                Boolean weatherType = false;
                Boolean airType = false;

                if(os.equals("Linux")){
                    filePath = "/usr/local/tomcat/webapps/";
                }

                switch(type) {
                    // Agent Capture Img
                    case "10": {
                        agentCaptureType = true;
                        filePath += "agent" + File.separator + name + extension;
                        break;
                    }
                    // Contents Thumbnails
                    case "20": {
                        contentsType = true;
//                        filePath += "thumbnails"  + File.separator + filename + "_" + formattedDateTime + extension;
                        filePath = File.separator + "thumbnails"  + File.separator + filename + "_" + formattedDateTime + extension;
                        break;
                    }
                    // Weather Img
                    case "30": case "40": case "50":
                    case "60": case "70": case "80":
                    case "90": case "100":
                    {
                        weatherType = true;
                        filePath += "weather"  + File.separator + originalFilename;
//                        filePath = File.separator + "weather"  + File.separator + originalFilename;
                        break;
                    }
                    // Air Img
                    case "110": case "120": case "130": case "140":
                    {
                        airType = true;
                        filePath += "air"  + File.separator + originalFilename;
//                        filePath = File.separator + "air"  + File.separator + originalFilename;
                        break;
                    }
                    default: {
                        break;
                    }
                }

                File destFile = new File(filePath);
                FileUtils.copyInputStreamToFile(file.getInputStream(), destFile);


                if(agentCaptureType || contentsType){
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("filePath", filePath);
                    resultMap.put("data", dataMap);
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                }
                else if(weatherType){
                    Boolean weatherImgDB = false;
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
                }
                else if(airType){
                    Boolean airImgDB = false;
                    String settingKey = "";
                    if (type.equals("110")) {
                        settingKey = "airGood";
                    } else if (type.equals("120")) {
                        settingKey = "airNormal";
                    } else if (type.equals("130")) {
                        settingKey = "airBad";
                    } else if (type.equals("140")) {
                        settingKey = "airVeryBad";
                    }

//                    int airImgResult = adminSettingMapper.patchWeatherImg(settingKey, originalFilename);
                    int airImgResult = 1;
                    if(airImgResult > 0){
                        airImgDB = true;
                    }
                    if(airImgDB) {
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("filePath", filePath);
                        resultMap.put("data", dataMap);
                        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                    }
                    else{
                        resultMap.put("code", ResponseCode.FAIL_UPDATE_ADMIN_SETTING_AIR_IMG.getCode());
                        resultMap.put("message", ResponseCode.FAIL_UPDATE_ADMIN_SETTING_AIR_IMG.getMessage());
                    }
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
        tempStyleMap.put("fontFl", dataMap.get("fontFl"));
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

    public String weatherImageBranch (Object rainStatus, Object skyStatus){

        if(rainStatus.equals(0L)){
            if(skyStatus.equals(4L)){
                return "weatherCloudy";
            }
            else if(skyStatus.equals(3L)){
                return "weatherManyCloudy";
            }
            else if(skyStatus.equals(1L)){
                return "weatherSunny";
            }
            else {
                System.out.println("[WARN] skyStatus = " + skyStatus);
                return "weatherSunny";
            }
        }
        else if (rainStatus.equals(1L)){
            return "weatherRain";
        }
        else if (rainStatus.equals(2L)){
            return "weatherRainSnow";
        }
        else if (rainStatus.equals(3L)){
            return "weatherSnow";
        }
        else if (rainStatus.equals(4L)){
            return "weatherShower";
        }
        else{
            System.out.println("[WARN] rainStatus = " + rainStatus);
            return "weatherRain";
        }
    }

    public String airImageBranch (Object pm10Grade){

        if (pm10Grade.equals(1L)){
            return "good";
        }
        else if (pm10Grade.equals(2L)){
            return "normal";
        }
        else if (pm10Grade.equals(3L)){
            return "bad";
        }
        else if (pm10Grade.equals(4L)){
            return "veryBad";
        }
        else{
            System.out.println("[WARN] pm10Grade = " + pm10Grade);
            return "good";
        }
    }
}