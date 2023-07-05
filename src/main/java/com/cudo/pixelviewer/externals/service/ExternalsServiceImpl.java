package com.cudo.pixelviewer.externals.service;

import com.cudo.pixelviewer.bo.mapper.AdminSettingMapper;
import com.cudo.pixelviewer.externals.mapper.ExternalsMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
public class ExternalsServiceImpl implements ExternalsService {

    final AdminSettingMapper adminSettingMapper;
    
    final ExternalsMapper externalsMapper;

    @Override
    public Map<String, Object> getExternalWeather() {
        Map<String, Object> resultMap = new HashMap<>();

        String coordsKey = "coords";
        String coords = adminSettingMapper.getValue(coordsKey);
        String[] coordsSplit = coords.split(",");

        String apisDataUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String serviceKey = "q77RhkytbzG8HvFIAtXpukhksHil87J9cVpObUMQbt%2BuJC98K9pAbt2VylRK4oQidtBVIe1wPTXbTaFBK1Y8NA%3D%3D";
        String pageNo = "1";
        String numOfRows = "1";
        String dataType = "JSON";
        String baseDate = "20230705";
        String baseTime = "0500";
        String nx = coordsSplit[0];
        String ny = coordsSplit[1];


        String tempUrl = apisDataUrl + "?" +
                "ServiceKey=" + serviceKey +
                "&pageNo=" + pageNo +
                "&numOfRows=" + numOfRows +
                "&dataType=" + dataType +
                "&base_date=" + baseDate +
                "&base_time=" + baseTime +
                "&nx=" + nx +
                "&ny=" + ny;

        String originalUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?" +
                "ServiceKey=q77RhkytbzG8HvFIAtXpukhksHil87J9cVpObUMQbt%2BuJC98K9pAbt2VylRK4oQidtBVIe1wPTXbTaFBK1Y8NA%3D%3D" +
                "&pageNo=1" +
                "&numOfRows=1" +
                "&dataType=JSON" +
                "&base_date=20230705" +
                "&base_time=0500" +
                "&nx=60" +
                "&ny=127";

        // WebClient 생성
        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
//                .baseUrl(tempUrl)
                .baseUrl(originalUrl)
                .build();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Mono<String> responseMono = webClient.method(HttpMethod.GET)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(String.class);

        responseMono.subscribe(response -> {
            String data = response.toString();
//            System.out.println("data = " + data);

            Map<String, Object> responseMap = new HashMap<>();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                responseMap = objectMapper.readValue(data, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("responseMap = " + responseMap);
//            if(responseMap.get("code").equals(200)){
//                // TODO : success
//            }
//            else{
//                // TODO : FAIL
//            }
        });

        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        return resultMap;
    }

    @Override
    public Map<String, Object> getExternalAir() {
        Map<String, Object> resultMap = new HashMap<>();

        String apisDataUrl = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty";
        String serviceKey = "q77RhkytbzG8HvFIAtXpukhksHil87J9cVpObUMQbt%2BuJC98K9pAbt2VylRK4oQidtBVIe1wPTXbTaFBK1Y8NA%3D%3D";
        String returnType = "JSON";
        String stationName = "서초구";
        String dataTerm = "DAILY";
        String ver = "1.3";

        String tempUrl = apisDataUrl + "?"
                + "serviceKey=" + serviceKey
                + "&returnType=" + returnType
                + "&stationName=" + stationName
                + "&dataTerm=" + dataTerm
                + "&ver=" + ver;

        String originalUrl = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?" +
                "serviceKey=q77RhkytbzG8HvFIAtXpukhksHil87J9cVpObUMQbt%2BuJC98K9pAbt2VylRK4oQidtBVIe1wPTXbTaFBK1Y8NA%3D%3D" +
                "&returnType=json" +
                "&stationName=서초구" +
                "&dataTerm=DAILY";

        // WebClient 생성
        WebClient webClient2 = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .baseUrl(tempUrl)
//                .baseUrl(originalUrl)
                .build();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Mono<String> responseMono = webClient2.method(HttpMethod.GET)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(String.class);

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        responseMono.subscribe(response -> {
            String data = response.toString();
//            System.out.println("data = " + data);

            Map<String, Object> responseMap = new HashMap<>();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                responseMap = objectMapper.readValue(data, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("responseMap = " + responseMap);

            Map<String, Object> responseJsonMap = (Map<String, Object>) responseMap.get("response");
            Map<String, Object> responseBody = (Map<String, Object>) responseJsonMap.get("body");
            List<Map<String, Object>> responseBodyItems = (List<Map<String, Object>>) responseBody.get("items");
            Map<String, Object> airInfoLatest = responseBodyItems.get(0);

            String[] keyNames = {"coFlag", "pm10Flag", "pm25Flag", "no2Flag", "o3Flag", "so2Flag"
                    , "dataTime", "mangName"
                    , "khaiGrade", "khaiValue"
                    , "pm10Value24", "pm25Value24", "pm10Grade1h", "pm25Grade1h", };

            for (String key : keyNames) {
                airInfoLatest.remove(key);
            }
            String mapperJson = "";
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapperJson = mapper.writeValueAsString(airInfoLatest);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            String type = "대기";
            externalsMapper.putExternalsInfos(type, mapperJson);

            System.out.println("airInfoLatest = " + airInfoLatest);

//            if(responseMap.get("code").equals(200)){
//                // TODO : success
//            }
//            else{
//                // TODO : FAIL
//            }
        });

        resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        return resultMap;
    }

}