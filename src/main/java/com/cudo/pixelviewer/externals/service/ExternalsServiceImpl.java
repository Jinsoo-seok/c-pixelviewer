package com.cudo.pixelviewer.externals.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.externals.mapper.ExternalsMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
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
    @Override
    public Map<String, Object> getExternalWeather() {
        Map<String, Object> resultMap = new HashMap<>();

//        String[] baseTimes = {"0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"};

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String localDateTime = dateTime.format(formatter);

        String coordsKey = "coords";
        String coords = adminSettingMapper.getValue(coordsKey);
        String[] coordsSplit = coords.split(",");

        String apisDataUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String serviceKey = "NA%2B2mZ6YHlKo2jNmEfOmsmrL2HY0ulBt9v3GUhfHtIV40HGjglABV1Zq1qCcjGJar4c1RAjcTuVI%2Blnx%2FTmkSw%3D%3D";
        String serviceKey2 = "NA+2mZ6YHlKo2jNmEfOmsmrL2HY0ulBt9v3GUhfHtIV40HGjglABV1Zq1qCcjGJar4c1RAjcTuVI+lnx/TmkSw==";
        String pageNo = "1";
        String numOfRows = "36";
        String dataType = "JSON";
        String baseDate = localDateTime.substring(0, 8);
        String baseTime = localDateTime.substring(8, 10) + "00";
//        String baseTime = "2000";
        String nx = coordsSplit[0];
        String ny = coordsSplit[1];


        URI weatherRequestUrl = UriComponentsBuilder.fromHttpUrl(apisDataUrl)
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


        String type = "날씨";
        System.out.println("[@Scheduled]weatherRequestUrl = " + weatherRequestUrl);
        Map<String, Object> webClientResponseMap = webClientFunction(type, weatherRequestUrl);

        if(webClientResponseMap != null){
            System.out.println("[First]");
            if(webClientResponseMap.get("webClient").equals(false)){
                webClientResponseMap = webClientFunction(type, weatherRequestUrl);
                System.out.println("[Second]");
            }
            if(!webClientResponseMap.get("webClient").equals(false)) {
                List<Map<String, Object>> tempWeather12 = (List<Map<String, Object>>) webClientResponseMap.get("tempFirst");
                List<Map<String, Object>> tempWeather24 = (List<Map<String, Object>>) webClientResponseMap.get("tempSecond");
                List<Map<String, Object>> tempWeather36 = (List<Map<String, Object>>) webClientResponseMap.get("tempThird");

                Map<String, Object> weatherResultMap = new HashMap<>();
                weatherResultMap.put("weather12", processWeatherData(tempWeather12));
                weatherResultMap.put("weather24", processWeatherData(tempWeather24));
                weatherResultMap.put("weather36", processWeatherData(tempWeather36));

                String mapperJson = "";
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapperJson = mapper.writeValueAsString(weatherResultMap);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                int putExternalsInfosResult = externalsMapper.putExternalsInfos(type, mapperJson);
                if (putExternalsInfosResult > 0) {
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_EXTERNALS_WEATHER.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_EXTERNALS_WEATHER.getMessage());
                }
                System.out.println("[success]webClientResponseMap = " + webClientResponseMap);
            }
            else{
                System.out.println("[fail]webClientResponseMap = " + webClientResponseMap);
                resultMap.put("code", ResponseCode.FAIL_EXTERNALS_WEATHER.getCode());
                resultMap.put("message", ResponseCode.FAIL_EXTERNALS_WEATHER.getMessage());
                resultMap.put("data", webClientResponseMap.get("data"));
            }
        }
        return resultMap;
    }

    @Scheduled(cron = "0 0 0/1 1/1 * ?")
    @Override
    public Map<String, Object> getExternalAir() {
        Map<String, Object> resultMap = new HashMap<>();

        String apisDataUrl = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty";
        String serviceKey = "NA%2B2mZ6YHlKo2jNmEfOmsmrL2HY0ulBt9v3GUhfHtIV40HGjglABV1Zq1qCcjGJar4c1RAjcTuVI%2Blnx%2FTmkSw%3D%3D";
        String serviceKey2 = "NA+2mZ6YHlKo2jNmEfOmsmrL2HY0ulBt9v3GUhfHtIV40HGjglABV1Zq1qCcjGJar4c1RAjcTuVI+lnx/TmkSw==";
        String returnType = "json";
        String stationName = "서초구";
        String encodedStationName = URLEncoder.encode(stationName, StandardCharsets.UTF_8);
        String dataTerm = "DAILY";
        String ver = "1.3";

        String tempUrl = apisDataUrl + "?"
                + "serviceKey=" + serviceKey
                + "&returnType=" + returnType
                + "&stationName=" + stationName
                + "&dataTerm=" + dataTerm
                + "&ver=" + ver;

        URI airRequestUrl = UriComponentsBuilder.fromHttpUrl(apisDataUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("returnType", returnType)
                .queryParam("stationName", encodedStationName)
                .queryParam("dataTerm", dataTerm)
                .queryParam("ver", ver)
                .build(true)
                .toUri();

        String type = "대기";
        System.out.println("[@Scheduled]airRequestUrl = " + tempUrl);
//        System.out.println("[@Scheduled]airRequestUrl = " + airRequestUrl);
        Map<String, Object> webClientResponseMap = webClientFunction(type, tempUrl);

        if(webClientResponseMap != null){
            System.out.println("[First]");
            if(webClientResponseMap.get("webClient").equals(false)){
                webClientResponseMap = webClientFunction(type, tempUrl);
                System.out.println("[Second]");
            }
            if(!webClientResponseMap.get("webClient").equals(false)) {
                int putExternalsInfosResult = externalsMapper.putExternalsInfos(type, (String) webClientResponseMap.get("airInfo"));
                if (putExternalsInfosResult > 0) {
                    resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                } else {
                    resultMap.put("code", ResponseCode.FAIL_INSERT_EXTERNALS_AIR.getCode());
                    resultMap.put("message", ResponseCode.FAIL_INSERT_EXTERNALS_AIR.getMessage());
                }
            }
            else{
                System.out.println("[fail]webClientResponseMap = " + webClientResponseMap);
                resultMap.put("code", ResponseCode.FAIL_EXTERNALS_AIR.getCode());
                resultMap.put("message", ResponseCode.FAIL_EXTERNALS_AIR.getMessage());
                resultMap.put("data", webClientResponseMap.get("data"));
            }
        }

        return resultMap;
    }

    public Map<String, Object> webClientFunction(String type, URI url){
        Map<String, Object> returnMap = new HashMap<>();

        WebClient webClient2 = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .baseUrl(url.toString())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String responseMono = webClient2.method(HttpMethod.GET)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if(responseMono.startsWith("<")){
            returnMap.put("webClient", false);
            returnMap.put("data", responseMono);
            return returnMap;
        }

        Map<String, Object> responseMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            responseMap = objectMapper.readValue(responseMono, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> responseJsonMap = (Map<String, Object>) responseMap.get("response");
        Map<String, Object> responseBody = (Map<String, Object>) responseJsonMap.get("body");
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

    public Map<String, Object> webClientFunction(String type, String url){
        Map<String, Object> returnMap = new HashMap<>();

        WebClient webClient2 = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .baseUrl(url)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String responseMono = webClient2.method(HttpMethod.GET)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if(responseMono.startsWith("<")){
            returnMap.put("webClient", false);
            returnMap.put("data", responseMono);
            return returnMap;
        }

        Map<String, Object> responseMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            responseMap = objectMapper.readValue(responseMono, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> responseJsonMap = (Map<String, Object>) responseMap.get("response");
        Map<String, Object> responseBody = (Map<String, Object>) responseJsonMap.get("body");
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
                    if (fcstValue.equals("강수없음")) {
                        weatherResult.put("precipitation", 0);
                    } else {
//                        weatherResult.put("precipitation", Integer.parseInt(fcstValue));
                        weatherResult.put("precipitation", fcstValue);
                    }
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
}