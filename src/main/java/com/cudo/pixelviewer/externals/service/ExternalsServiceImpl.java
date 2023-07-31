package com.cudo.pixelviewer.externals.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.externals.mapper.ExternalsMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class ExternalsServiceImpl implements ExternalsService {

    final AdminSettingMapper adminSettingMapper;
    
    final ExternalsMapper externalsMapper;

    @Scheduled(cron = "0 0 2-23/3 1/1 * ?")
    public void scheduleGetExternalWeather() {
        Map<String, Object> resultMap = new HashMap<>();

        String type = "날씨";
        String coords = "schedule";
        URI weatherURI = urlWeather(coords);
        log.info("[@Scheduled] weatherRequestUrl = {}", weatherURI);

        Map<String, Object> restTemplateResponseMap = restTemplateFunction(type, weatherURI);

        if(restTemplateResponseMap != null){
            if(!restTemplateResponseMap.get("webClient").equals(false)) {
                List<Map<String, Object>> tempWeather12 = (List<Map<String, Object>>) restTemplateResponseMap.get("tempFirst");
                List<Map<String, Object>> tempWeather24 = (List<Map<String, Object>>) restTemplateResponseMap.get("tempSecond");
                List<Map<String, Object>> tempWeather36 = (List<Map<String, Object>>) restTemplateResponseMap.get("tempThird");

                Map<String, Object> weatherResultMap = new HashMap<>();
                weatherResultMap.put("weather0", processWeatherData(tempWeather12));
                weatherResultMap.put("weather1", processWeatherData(tempWeather24));
                weatherResultMap.put("weather2", processWeatherData(tempWeather36));

                String mapperJson = "";
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapperJson = mapper.writeValueAsString(weatherResultMap);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                int putExternalsInfosResult = externalsMapper.putExternalsInfos(type, mapperJson);
                if (putExternalsInfosResult > 0) {
                    log.info("[SUCCESS] restTemplateResponseMap = {}", restTemplateResponseMap);
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_EXTERNALS_WEATHER.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_EXTERNALS_WEATHER.getMessage());
                }
            }
            else{
                log.info("[FAIL] restTemplateResponseMap = {}", restTemplateResponseMap);
                resultMap.put("code", ResponseCode.FAIL_EXTERNALS_WEATHER.getCode());
                resultMap.put("message", ResponseCode.FAIL_EXTERNALS_WEATHER.getMessage());
                resultMap.put("data", restTemplateResponseMap.get("data"));
            }
        }
        log.info("[ScheduledGetExternalWeather] returnMap = {}", resultMap);
    }

    @Scheduled(cron = "0 0 0/1 1/1 * ?")
    public void scheduleGetExternalAir() {
        Map<String, Object> resultMap = new HashMap<>();

        String type = "대기";
        String scheduleType = "schedule";
        URI airURI = urlAir(scheduleType);
        log.info("[@Scheduled] airRequestUrl = {}", airURI);

        Map<String, Object> restTemplateResponseMap = restTemplateFunction(type, airURI);

        if(restTemplateResponseMap != null){
            if(!restTemplateResponseMap.get("webClient").equals(false)) {
                int putExternalsInfosResult = externalsMapper.putExternalsInfos(type, (String) restTemplateResponseMap.get("airInfo"));
                if (putExternalsInfosResult > 0) {
                    log.info("[SUCCESS] restTemplateResponseMap = {}", restTemplateResponseMap);
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_EXTERNALS_AIR.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_EXTERNALS_AIR.getMessage());
                }
            }
            else{
                log.info("[FAIL] restTemplateResponseMap = {}", restTemplateResponseMap);
                resultMap.put("code", ResponseCode.FAIL_EXTERNALS_AIR.getCode());
                resultMap.put("message", ResponseCode.FAIL_EXTERNALS_AIR.getMessage());
                resultMap.put("data", restTemplateResponseMap.get("data"));
            }
        }

        log.info("[scheduleGetExternalAir] returnMap = {}", resultMap);
    }

    @Override
    public void getExternalWeather(String coords) {
        Map<String, Object> resultMap = new HashMap<>();

        String type = "날씨";
        String scheduleType = coords;
        URI weatherURI = urlWeather(scheduleType);
        log.info("[@Called] weatherRequestUrl = {}", weatherURI);

        Map<String, Object> restTemplateResponseMap = restTemplateFunction(type, weatherURI);

        if(restTemplateResponseMap != null){
            if(!restTemplateResponseMap.get("webClient").equals(false)) {
                List<Map<String, Object>> tempWeather12 = (List<Map<String, Object>>) restTemplateResponseMap.get("tempFirst");
                List<Map<String, Object>> tempWeather24 = (List<Map<String, Object>>) restTemplateResponseMap.get("tempSecond");
                List<Map<String, Object>> tempWeather36 = (List<Map<String, Object>>) restTemplateResponseMap.get("tempThird");

                Map<String, Object> weatherResultMap = new HashMap<>();
                weatherResultMap.put("weather0", processWeatherData(tempWeather12));
                weatherResultMap.put("weather1", processWeatherData(tempWeather24));
                weatherResultMap.put("weather2", processWeatherData(tempWeather36));

                String mapperJson = "";
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapperJson = mapper.writeValueAsString(weatherResultMap);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                int putExternalsInfosResult = externalsMapper.putExternalsInfos(type, mapperJson);
                if (putExternalsInfosResult > 0) {
                    log.info("[SUCCESS] restTemplateResponseMap = {}", restTemplateResponseMap);
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_EXTERNALS_WEATHER.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_EXTERNALS_WEATHER.getMessage());
                }
            }
            else{
                log.info("[FAIL] restTemplateResponseMap = {}", restTemplateResponseMap);
                resultMap.put("code", ResponseCode.FAIL_EXTERNALS_WEATHER.getCode());
                resultMap.put("message", ResponseCode.FAIL_EXTERNALS_WEATHER.getMessage());
                resultMap.put("data", restTemplateResponseMap.get("data"));
            }
        }
        log.info("[CalledGetExternalWeather] returnMap = {}", resultMap);
    }

    @Override
    public void getExternalAir(String stationName) {
        Map<String, Object> resultMap = new HashMap<>();

        String type = "대기";
        String scheduleType = stationName;
        URI airURI = urlAir(scheduleType);
        log.info("[@Called] airRequestUrl = {}", airURI);

        Map<String, Object> restTemplateResponseMap = restTemplateFunction(type, airURI);

        if(restTemplateResponseMap != null){
            if(!restTemplateResponseMap.get("webClient").equals(false)) {
                int putExternalsInfosResult = externalsMapper.putExternalsInfos(type, (String) restTemplateResponseMap.get("airInfo"));
                if (putExternalsInfosResult > 0) {
                    log.info("[SUCCESS] restTemplateResponseMap = {}", restTemplateResponseMap);
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_EXTERNALS_AIR.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_EXTERNALS_AIR.getMessage());
                }
            }
            else{
                log.info("[FAIL] restTemplateResponseMap = {}", restTemplateResponseMap);
                resultMap.put("code", ResponseCode.FAIL_EXTERNALS_AIR.getCode());
                resultMap.put("message", ResponseCode.FAIL_EXTERNALS_AIR.getMessage());
                resultMap.put("data", restTemplateResponseMap.get("data"));
            }
        }
        log.info("[CalledGetExternalAir] returnMap = {}", resultMap);
    }

    public Map<String, Object> processWeatherData(List<Map<String, Object>> tempWeatherList) {
        Map<String, Object> weatherResult = new HashMap<>();

        for (Map<String, Object> tempWeather : tempWeatherList) {
            String category = (String) tempWeather.get("category");
            String fcstValue = (String) tempWeather.get("fcstValue");

            switch (category) {
                case "SKY":
                    weatherResult.put("skyStatus", Integer.parseInt(fcstValue));
                    break;
                case "PTY":
                    weatherResult.put("rainStatus", Integer.parseInt(fcstValue));
                    break;
                case "PCP":
                    weatherResult.put("precipitation", fcstValue);
                    break;
                case "TMP":
                    weatherResult.put("tempStatus", Integer.parseInt(fcstValue));
                    break;
                case "REH":
                    weatherResult.put("humiStatus", Integer.parseInt(fcstValue));
                    break;
                case "VEC":
                    weatherResult.put("windWay", Integer.parseInt(fcstValue));
                    break;
                case "WSD":
                    weatherResult.put("windSpeed", Float.parseFloat(fcstValue));
                    break;
                default:
                    // Handle other categories if needed
                    break;
            }
        }
        return weatherResult;
    }

    public Map<String, Object> restTemplateFunction(String type, URI uri){
        Map<String, Object> returnMap = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "*/*;q=0.9"); // HTTP_ERROR 방지
        HttpEntity<String> httpRequest = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();

        HttpStatus httpStatus = null;
        ResponseEntity<Map> httpResponse = null;

        httpResponse = restTemplate.exchange(uri, HttpMethod.GET, httpRequest, Map.class);
        Map<String, Object> responseMap = (Map<String, Object>) httpResponse.getBody().get("response");
        Map<String, Object> responseBody = (Map<String, Object>) responseMap.get("body");

        if(type.equals("대기")) {
            List<Map<String, Object>> responseBodyItems = (List<Map<String, Object>>) responseBody.get("items");
            Map<String, Object> airInfoLatest = responseBodyItems.get(0);

            String[] keyNames = {"coFlag", "pm10Flag", "pm25Flag", "no2Flag", "o3Flag", "so2Flag"
                    , "dataTime", "mangName"
                    , "khaiGrade", "khaiValue"
                    , "pm10Value24", "pm25Value24", "pm10Grade1h", "pm25Grade1h",};

            for (String key : keyNames) {
                airInfoLatest.remove(key);
            }

            for (Map.Entry<String, Object> entry : airInfoLatest.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (key.equals("so2Value") || key.equals("no2Value") || key.equals("coValue") || key.equals("o3Value")) {
                    airInfoLatest.put(key, Float.parseFloat(value.toString()));
                } else {
                    airInfoLatest.put(key, Integer.parseInt(value.toString()));
                }
            }

            String mapperJson = "";
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapperJson = mapper.writeValueAsString(airInfoLatest);
                returnMap.put("webClient", true);
                returnMap.put("airInfo", mapperJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else if(type.equals("날씨")){
            Map<String, Object> responseBodyItems = (Map<String, Object>) responseBody.get("items");
            List<Map<String, Object>> responseBodyItem = (List<Map<String, Object>>) responseBodyItems.get("item");


            List<Map<String, Object>> tempFirst = new ArrayList<>();
            List<Map<String, Object>> tempSecond = new ArrayList<>();
            List<Map<String, Object>> tempThird = new ArrayList<>();

            for (int i = 0; i < responseBodyItem.size(); i++) {
                Map<String, Object> item = responseBodyItem.get(i);

                if (i >= 0 && i < 12) {
                    tempFirst.add(item);
                } else if (i >= 12 && i < 24) {
                    tempSecond.add(item);
                } else if (i >= 24 && i < 36) {
                    tempThird.add(item);
                }
            }
            returnMap.put("webClient", true);

            returnMap.put("tempFirst", tempFirst);
            returnMap.put("tempSecond", tempSecond);
            returnMap.put("tempThird", tempThird);
        }
        return returnMap;
    }

    public URI urlWeather(String scheduleType){
        String nx = null;
        String ny = null;
        if(scheduleType.equals("schedule")){
            String coordsKey = "coords";
            String coords = adminSettingMapper.getValue(coordsKey);
            String[] coordsSplit = coords.split(",");

            nx = coordsSplit[0];
            ny = coordsSplit[1];
        }
        else{
            String coords = scheduleType;
            String[] coordsSplit = coords.split(",");
            nx = coordsSplit[0];
            ny = coordsSplit[1];
        }

        String[] times = {"02", "05", "08", "11", "14", "17", "20", "23"};
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String localDateTime = dateTime.format(formatter);

        String currentTime = localDateTime.substring(8, 10);
        String selectedTime = null;
        for (String time : times) {
            if (currentTime.compareTo(time) >= 0) {
                selectedTime = time;
            } else {
                break;
            }
        }
        if (selectedTime == null) {
            selectedTime = times[0];
        }

        String baseDate = localDateTime.substring(0, 8);
        String baseTime = selectedTime + "00";
        String apisDataUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String serviceKey = "NA%2B2mZ6YHlKo2jNmEfOmsmrL2HY0ulBt9v3GUhfHtIV40HGjglABV1Zq1qCcjGJar4c1RAjcTuVI%2Blnx%2FTmkSw%3D%3D";
        String pageNo = "1";
        String numOfRows = "36";
        String dataType = "JSON";

        URI urlBuilder = UriComponentsBuilder.fromHttpUrl(apisDataUrl)
                .queryParam("ServiceKey", serviceKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("dataType", dataType)
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)
                .toUri();


        try {
            return new URI(urlBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public URI urlAir(String scheduleType){
        String stationName = null;
        if(scheduleType.equals("schedule")){
            stationName = adminSettingMapper.getValue("stationName");
        }
        else{
            stationName = scheduleType;
        }

        String apisDataUrl = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty";
        String serviceKey = "NA%2B2mZ6YHlKo2jNmEfOmsmrL2HY0ulBt9v3GUhfHtIV40HGjglABV1Zq1qCcjGJar4c1RAjcTuVI%2Blnx%2FTmkSw%3D%3D";
        String returnType = "json";
        String encodedStationName = URLEncoder.encode(stationName, StandardCharsets.UTF_8);
        String dataTerm = "DAILY";
        String ver = "1.3";

        return UriComponentsBuilder.fromHttpUrl(apisDataUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("returnType", returnType)
                .queryParam("stationName", encodedStationName)
                .queryParam("dataTerm", dataTerm)
                .queryParam("ver", ver)
                .build(true)
                .toUri();
    }
}