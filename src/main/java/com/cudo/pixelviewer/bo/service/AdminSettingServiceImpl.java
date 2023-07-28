package com.cudo.pixelviewer.bo.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.operate.mapper.LedMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.DisplaySettingVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSettingServiceImpl implements AdminSettingService {

    final AdminSettingMapper adminSettingMapper;

    final LedMapper ledMapper;

    final static String PRESET_NAME_PREFIX = "프리셋";

    @Override
    public Map<String, Object> getAdminSettingList() {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();

        List<Map<String, Object>> adminSettingVoList = adminSettingMapper.getAdminSettingList();


        for (Map<String, Object> element : adminSettingVoList) {
            String settingKey = (String) element.get("settingKey");
            Object settingValue = element.get("settingValue");

            if (settingKey.equals("imgDefaultPlaytime") || settingKey.equals("ledPresetCount")|| settingKey.equals("testPatternTime")) {
                returnMap.put(settingKey, Integer.valueOf((String) settingValue));
            }
            else if (settingKey.equals("coords")) {
                String temp = (String) settingValue;
                String[] tempSplit = temp.split(",");

                returnMap.put("nx", Integer.valueOf(tempSplit[0]));
                returnMap.put("ny", Integer.valueOf(tempSplit[1]));
            }
            else if (settingValue.equals("1") || settingValue.equals("0") ) {
                boolean boolValue = settingValue.equals("1");
                returnMap.put(settingKey, boolValue);
            }
            else {
                returnMap.put(settingKey, settingValue.toString());
            }
        }

        if(adminSettingVoList.size() > 0){
            resultMap.put("data", returnMap);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putAdminSetting(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> tempArray = new ArrayList<>();

        param.put("coords", param.get("nx") + "," + param.get("ny"));
        String[] removeKey = {"nx", "ny"};
        for (String key : removeKey) {
            param.remove(key);
        }

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            Map<String, Object> queryMap = new HashMap<>();
            String key = entry.getKey();
            Object value = entry.getValue();

            queryMap.put("settingKey", key);

            if (value instanceof Boolean) {
                queryMap.put("settingValue", value.equals(true) ? "1" : "0");
            }
            else if (value instanceof Integer) {
                queryMap.put("settingValue", value.toString());
            }
            else if (value instanceof String) {
                queryMap.put("settingValue", value);
            }
            else {
                System.out.println(key + " is of an unknown type: " + value);
            }
            tempArray.add(queryMap);
        }
        if(param.containsKey("externalinfoArea")){
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("settingKey", "stationName");
            queryMap.put("settingValue", getAirStationName(extractDistrict((String) param.get("externalinfoArea"))));

            tempArray.add(queryMap);
        }

        if(param.containsKey("ledPresetCount")) {
            List<Map<String, Object>> presetList = new ArrayList<>();
            Integer presetCount = Integer.parseInt(String.valueOf(param.get("ledPresetCount")));

            if (presetCount > 0) {
                for (int i = 0; i < presetCount; i++) {
                    Map<String, Object> presetInfo = new HashMap<>();

                    presetInfo.put("presetNumber", String.format("%02X", i));
                    presetInfo.put("presetName", PRESET_NAME_PREFIX + (i + 1));
                    presetList.add(presetInfo);
                }
                Integer deletePresetCount = ledMapper.deleteLedPreset();
                Integer insertPresetCount = ledMapper.postLedPreset(presetList);
            }
        }

        int putAdminSettingResult = adminSettingMapper.putAdminSetting(tempArray);

        if (putAdminSettingResult == 1) {
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else {
            ResponseCode failCode = ResponseCode.FAIL_UPDATE_SETTING_VALUES;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }


    @Override
    public Map<String, Object> getDisplayInfoList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<DisplaySettingVo> displaySettingVoList = adminSettingMapper.getDisplayInfoList();

        if(displaySettingVoList.size() > 0){
            resultMap.put("data", displaySettingVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getDisplayInfo(String displayId) {
        Map<String, Object> resultMap = new HashMap<>();

        DisplaySettingVo displaySettingVo = adminSettingMapper.getDisplayInfo(displayId);

        if(displaySettingVo != null){
            resultMap.put("data", displaySettingVo);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postDisplayInfo(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int patchLayerTopMostResult = adminSettingMapper.postDisplayInfo(param);

        if (patchLayerTopMostResult == 1) {
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

        } else {
            ResponseCode failCode = ResponseCode.FAIL_INSERT_DISPLAY_SETTING;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putDisplayInfo(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int putDisplayInfoValid = adminSettingMapper.putDisplayInfoValid(param);

        if(putDisplayInfoValid == 1) {
            int patchLayerTopMostResult = adminSettingMapper.putDisplayInfo(param);

            if (patchLayerTopMostResult == 1) {
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

            } else {
                ResponseCode failCode = ResponseCode.FAIL_UPDATE_DISPLAY_SETTING;
                resultMap.put("code", failCode.getCode());
                resultMap.put("message", failCode.getMessage());
            }
        }
        else{
            ResponseCode failCode = ResponseCode.FAIL_NOT_EXIST_DISPLAY_SETTING;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteDisplayInfo(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int deleteDisplayInfoValid = adminSettingMapper.deleteDisplayInfoValid(param);

        if(deleteDisplayInfoValid == 1) {
            String displayUsedCheck = adminSettingMapper.displayUsedCheck(param);

            if(displayUsedCheck == null){
                int patchLayerTopMostResult = adminSettingMapper.deleteDisplayInfo(param);

                if (patchLayerTopMostResult == 1) {
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

                } else {
                    ResponseCode failCode = ResponseCode.FAIL_DELETE_DISPLAY_SETTING;
                    resultMap.put("code", failCode.getCode());
                    resultMap.put("message", failCode.getMessage());
                }
            }
            else{
                resultMap.put("data", displayUsedCheck);
                ResponseCode failCode = ResponseCode.FAIL_USED_DISPLAY_SETTING;
                resultMap.put("code", failCode.getCode());
                resultMap.put("message", failCode.getMessage());
            }
        }
        else{
            ResponseCode failCode = ResponseCode.FAIL_NOT_EXIST_DISPLAY_SETTING;
            resultMap.put("code", failCode.getCode());
            resultMap.put("message", failCode.getMessage());
        }
        return resultMap;
    }

    public String getAirStationName(String externalinfoArea){

        URI urlAddrToXY = urlAddrToXY(externalinfoArea);
        Map<String, Object> webClientResponseFirst = restTemplateFunction("addrToXY", urlAddrToXY);

        URI urlXYToStationName = urlXYToStationName((String) webClientResponseFirst.get("tmX"), (String) webClientResponseFirst.get("tmY"));
        Map<String, Object> webClientResponseSecond = restTemplateFunction("XYToStationName", urlXYToStationName);

        return (String) webClientResponseSecond.get("stationName");
    }

    public Map<String, Object> restTemplateFunction(String type, URI uri){
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "*/*;q=0.9"); // HTTP_ERROR 방지
        HttpEntity<String> httpRequest = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();

        HttpStatus httpStatus = null;
        ResponseEntity<Map> httpResponse = null;

        httpResponse = restTemplate.exchange(uri, HttpMethod.GET, httpRequest, Map.class);
        responseMap = (Map<String, Object>) httpResponse.getBody().get("response");
        Map<String, Object> tempMap = (Map<String, Object>) responseMap.get("body");
        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) tempMap.get("items");

        if(type.equals("addrToXY")){
            returnMap = itemsList.get(0);
        }
        else if(type.equals("XYToStationName")){
            returnMap = itemsList.get(0);
        }

        return returnMap;
    }

    public static String extractDistrict(String address) {
        Pattern pattern = Pattern.compile("(.*?[구동])");
        Matcher matcher = pattern.matcher(address);
        String district = null;

        if (matcher.find()) {
            district = matcher.group(1);
        }
        else{
            String[] addressSplit = address.split(" ");
            if (addressSplit.length >= 3) {
                district = addressSplit[0] + " " + addressSplit[1] + addressSplit[2].substring(2);
            }
        }
        return convertToFullRegionName(district);
    }

    private static String convertToFullRegionName(String district) {
        String[] districtSplit = district.split(" ");
        String tempRegionName = districtSplit[0];

        if (tempRegionName.equals("서울"))
            tempRegionName = "서울특별시";
        else if (tempRegionName.equals("부산"))
            tempRegionName = "부산광역시";
        else if (tempRegionName.equals("대구"))
            tempRegionName = "대구광역시";
        else if (tempRegionName.equals("인천"))
            tempRegionName = "인천광역시";
        else if (tempRegionName.equals("광주"))
            tempRegionName = "광주광역시";
        else if (tempRegionName.equals("대전"))
            tempRegionName = "대전광역시";
        else if (tempRegionName.equals("울산"))
            tempRegionName = "울산광역시";
        else if (tempRegionName.equals("세종"))
            tempRegionName = "세종특별자치시";
        else if (tempRegionName.equals("경기"))
            tempRegionName = "경기도";
        else if (tempRegionName.equals("강원"))
            tempRegionName = "강원도";
        else if (tempRegionName.equals("충북"))
            tempRegionName = "충청북도";
        else if (tempRegionName.equals("충남"))
            tempRegionName = "충청남도";
        else if (tempRegionName.equals("전북"))
            tempRegionName = "전라북도";
        else if (tempRegionName.equals("전남"))
            tempRegionName = "전라남도";
        else if (tempRegionName.equals("경북"))
            tempRegionName = "경상북도";
        else if (tempRegionName.equals("경남"))
            tempRegionName = "경상남도";
        else if (tempRegionName.equals("제주"))
            tempRegionName = "제주특별자치도";

        if (districtSplit.length > 1) {
            for (int i = 1; i < districtSplit.length; i++) {
                tempRegionName += " " + districtSplit[i];
            }
        }
        return tempRegionName;
    }

    public URI urlAddrToXY(String umdName){
        String apisDataUrl = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getTMStdrCrdnt";
        String serviceKey = "NA%2B2mZ6YHlKo2jNmEfOmsmrL2HY0ulBt9v3GUhfHtIV40HGjglABV1Zq1qCcjGJar4c1RAjcTuVI%2Blnx%2FTmkSw%3D%3D";
        String returnType = "json";
        String numOfRows = "1";
        String pageNo = "1";

        String encodedUmdName = null;
        try {
            encodedUmdName = URLEncoder.encode(umdName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return UriComponentsBuilder.fromHttpUrl(apisDataUrl)
                .queryParam("ServiceKey", serviceKey)
                .queryParam("returnType", returnType)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("umdName", encodedUmdName)
                .build(true)
                .toUri();
    }

    public URI urlXYToStationName(String tmX, String tmY){
        String apisDataUrl = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList";
        String serviceKey = "NA%2B2mZ6YHlKo2jNmEfOmsmrL2HY0ulBt9v3GUhfHtIV40HGjglABV1Zq1qCcjGJar4c1RAjcTuVI%2Blnx%2FTmkSw%3D%3D";
        String returnType = "json";
        String ver = "1.1";

        return UriComponentsBuilder.fromHttpUrl(apisDataUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("returnType", returnType)
                .queryParam("tmX", tmX)
                .queryParam("tmY", tmY)
                .queryParam("ver", ver)
                .build(true)
                .toUri();
    }
}