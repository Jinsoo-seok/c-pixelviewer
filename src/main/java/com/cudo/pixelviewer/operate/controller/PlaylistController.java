package com.cudo.pixelviewer.operate.controller;

import com.cudo.pixelviewer.config.ParamException;
import com.cudo.pixelviewer.operate.service.PlaylistService;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.cudo.pixelviewer.util.ParameterUtils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api-manager/operate/playlist")
public class PlaylistController {

    final PlaylistService playlistService;

    @GetMapping
    public Map<String, Object> getPlaylistList(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = playlistService.getPlaylistList();
        }
        catch (Exception exception) {
            log.error("[Exception][getPlaylistList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @GetMapping("/{layerId}")
    public Map<String, Object> getPlaylist(HttpServletRequest request, @PathVariable String layerId) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, layerId);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = playlistService.getPlaylist(layerId);
        }
        catch (Exception exception) {
            log.error("[Exception][getPlaylist] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }


    @PostMapping
    public Map<String, Object> postPlaylist(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"playlistNm", "layerId", "presetId", "screenId"};

        try {
            parameterValidation(param, keyList);
            parameterString("playlistNm", param.get("playlistNm"), true, 0, null);
            parameterInt("layerId", param.get("layerId"), true);
            parameterInt("presetId", param.get("presetId"), true);
            parameterInt("screenId", param.get("screenId"), true);

            responseMap = playlistService.postPlaylist(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postPlaylist] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postPlaylist] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @PutMapping
    public Map<String, Object> putPlaylist(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"playlistId", "playlistNm", "contentIdList"};

        try {
            parameterValidation(param, keyList);
            parameterInt("playlistId", param.get("playlistId"), true);
            parameterString("playlistNm", param.get("playlistNm"), true, 0, null);
            parameterArray("contentIdList", param.get("contentIdList"), true);

            responseMap = playlistService.putPlaylist(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][putPlaylist] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][putPlaylist] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @DeleteMapping
    public Map<String, Object> deletePlaylist(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"playlistId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("playlistId", param.get("playlistId"), true);

            responseMap = playlistService.deletePlaylist(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][deletePlaylist] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][deletePlaylist] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

/////////////////////////////////////////////////////////////////////////////////contents
    @GetMapping("/contents")
    public Map<String, Object> getPlaylistContentsList(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}]", apiInfo, startTime);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = playlistService.getPlaylistContentsList();
        }
        catch (Exception exception) {
            log.error("[Exception][getPlaylistContentsList] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @GetMapping("/contents/{Id}")
    public Map<String, Object> getPlaylistContents(HttpServletRequest request, @PathVariable String Id) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, Id);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        try {
            responseMap = playlistService.getPlaylistContents(Id);
        }
        catch (Exception exception) {
            log.error("[Exception][getPlaylistContents] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }


    @PostMapping("/contents")
    public Map<String, Object> postPlaylistContents(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"type", "ctsNm", "ctsPath", "playtime", "thumbnailPath"};

        try {
            parameterValidation(param, keyList);
            parameterString("type", param.get("type"), true, 0, null);
            parameterString("ctsNm", param.get("ctsNm"), true, 0, null);
            parameterString("ctsPath", param.get("ctsPath"), true, 0, null);
            parameterInt("playtime", param.get("playtime"), true);
            parameterString("thumbnailPath", param.get("thumbnailPath"), true, 0, null);

            responseMap = playlistService.postPlaylistContents(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][postPlaylistContents] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][postPlaylistContents] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @DeleteMapping("/contents")
    public Map<String, Object> deletePlaylistContents(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"contentId"};

        try {
            parameterValidation(param, keyList);
            parameterInt("contentId", param.get("contentId"), true);

            responseMap = playlistService.deletePlaylistContents(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][deletePlaylistContents] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][deletePlaylistContents] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @PatchMapping("/contents/name")
    public Map<String, Object> patchContentsName(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"contentId", "ctsNm"};

        try {
            parameterValidation(param, keyList);
            parameterInt("contentId", param.get("contentId"), true);
            parameterString("ctsNm", param.get("ctsNm"), true, 0, null);

            responseMap = playlistService.patchContentsName(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchContentsName] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchContentsName] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }

    @PatchMapping("/contents/playtime")
    public Map<String, Object> patchContentsPlaytime(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        long startTime = System.currentTimeMillis();
        String apiInfo = "["+ request.getRequestURI() + "] [" + request.getMethod() + "]";
        log.info("{} [START] [{}] - {}", apiInfo, startTime, param);
        Map<String, Object> responseMap = ParameterUtils.responseOption(ResponseCode.FAIL.getCodeName());


        String[] keyList = {"contentId", "playtime"};

        try {
            parameterValidation(param, keyList);
            parameterInt("contentId", param.get("contentId"), true);
            parameterInt("playtime", param.get("playtime"), true);

            responseMap = playlistService.patchContentsPlaytime(param);
        }
        catch (ParamException paramException){
            log.error("[paramException][patchContentsPlaytime] - {}", paramException.getMessage());
            responseMap.put("code", paramException.getCode());
            responseMap.put("message", paramException.getMessage());
        }
        catch (Exception exception) {
            log.error("[Exception][patchContentsPlaytime] - {}", exception.getMessage());
            responseMap.put("exceptionMessage", exception.getMessage());
        }

        log.info("{} [END] [{}] - {}", apiInfo, (System.currentTimeMillis()-startTime), responseMap.get("code"));
        return responseMap;
    }
}
