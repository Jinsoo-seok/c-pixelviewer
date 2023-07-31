package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.operate.mapper.PresetMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.PlaylistContentsVo;
import com.cudo.pixelviewer.vo.PlaylistVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    final PlaylistMapper playlistMapper;

    final PresetMapper presetMapper;

    final Environment environment;

    @Override
    public Map<String, Object> getPlaylistList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<PlaylistVo> playlistVoList = playlistMapper.getPlaylistList();

        if(playlistVoList.size() > 0){
            resultMap.put("data", playlistVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getPlaylist(String layerId) {
        Map<String, Object> resultMap = new HashMap<>();

        Map<String, Object> playlist = playlistMapper.getPlaylist(layerId);

        if(playlist != null){
            String contentIdList = (String) playlist.get("contentIdList");
            if(!contentIdList.equals("NULL") && !contentIdList.equals("[{}]")) {
                String queryTemp = "(" + contentIdList + ")";
                List<Map<String, Object>> tempContentIdList = new ArrayList<>();
                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(contentIdList);
                    playlist.put("contentIdList", obj);
                    tempContentIdList = (List<Map<String, Object>>) obj;

                    StringBuilder contentIds = new StringBuilder();

                    if (tempContentIdList != null) {
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

                List<Map<String, Object>> playlistContentList = playlistMapper.getPlaylistContentList(queryTemp);
                Long order = 1L;

                if(playlistContentList.size() > 0){
                    List<Map<String, Object>> resultContentList = contentMatchingAndOrderByOrder(tempContentIdList, playlistContentList);
                    if(resultContentList.size() > 0){
                        playlist.put("contentIdList", resultContentList);
                    }
                    else{
                        playlist.put("contentIdList", tempContentIdList);
                        log.info("[FAIL][getPlaylist] resultContentList");
                    }
                }
            }
            else{
                playlist.put("contentIdList", "NULL");
            }
            resultMap.put("data", playlist);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postPlaylist(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

//        int playlistCheck = playlistMapper.postPlaylistValid(param);
        int playlistCheck = 0;

        if(playlistCheck == 0){
            int postPlaylistResult = playlistMapper.postPlaylist(param);

            if(postPlaylistResult == 1){
                dataMap.put("playlistId", param.get("playlistId"));
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                resultMap.put("data", dataMap);
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_INSERT_PLAYLIST.getCode());
                resultMap.put("message", ResponseCode.FAIL_INSERT_PLAYLIST.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_DUPLICATE_PLAYLIST.getCode());
            resultMap.put("message", ResponseCode.FAIL_DUPLICATE_PLAYLIST.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> putPlaylist(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        int playlistCheck = playlistMapper.putPlaylistValid(param);

        if(playlistCheck == 1){
            try {
                ObjectMapper mapper = new ObjectMapper();
                String mapperJson = mapper.writeValueAsString(param.get("contentIdList"));
                param.put("contentIdList", mapperJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            int putPlaylistResult = playlistMapper.putPlaylist(param);

            if(putPlaylistResult == 1){
                int refreshPresetUpdateDate = presetMapper.refreshPresetUpdateDate(param.get("presetId"));
                dataMap.put("playlistId", param.get("playlistId"));
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                resultMap.put("data", dataMap);
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_PLAYLIST.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_PLAYLIST.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PLAYLIST.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PLAYLIST.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deletePlaylist(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int playlistCheck = playlistMapper.deletePlaylistValid(param);

        if(playlistCheck == 1){
            int deletePlaylistResult = playlistMapper.deletePlaylist(param);

            if(deletePlaylistResult == 1){
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_DELETE_PLAYLIST.getCode());
                resultMap.put("message", ResponseCode.FAIL_DELETE_PLAYLIST.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PLAYLIST.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PLAYLIST.getMessage());
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getPlaylistContentsList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<PlaylistContentsVo> playlistVoList = playlistMapper.getPlaylistContentsList();

        if(playlistVoList.size() > 0){
            resultMap.put("data", playlistVoList);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getPlaylistContents(String Id) {
        Map<String, Object> resultMap = new HashMap<>();

        PlaylistContentsVo playlistVo = playlistMapper.getPlaylistContents(Id);

        if(playlistVo!= null) {
            Integer contentId = playlistVo.getContentId();
            String checkContentId = "\"contentId\":" + contentId;

            List<PlaylistVo> playlistVoList = playlistMapper.getPlaylistList();
            StringBuilder playlistIds = new StringBuilder();

            for (PlaylistVo tempPlaylistVo : playlistVoList) {
                String tempContentIdList = tempPlaylistVo.getContentIdList();
                if (tempContentIdList.contains(checkContentId)) {
                    playlistIds.append(tempPlaylistVo.getPlaylistId()).append(",");
                }
            }
            String playlistIdString = playlistIds.toString();
            if (playlistIdString.endsWith(",")) {
                playlistIdString = playlistIdString.substring(0, playlistIdString.length() - 1);
            }

            List<Map<String, Object>> includedList = new ArrayList<>();
            if (!playlistIdString.equals("")) {
                String queryIdList = "(" + playlistIdString + ")";
                includedList = playlistMapper.getIncludedList(queryIdList);

                resultMap.put("deleteYn", false);
                resultMap.put("data", includedList);
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("deleteYn", true);
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
        }
        else{
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.NO_CONTENT.getCodeName()));
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postPlaylistContents(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

//        int playlistCheck = playlistMapper.postPlaylistContentsValid(param);
        int playlistCheck = 0;

        if(playlistCheck == 0){
            int postPlaylistContentsResult = playlistMapper.postPlaylistContents(param);

            if(postPlaylistContentsResult == 1){
                dataMap.put("contentId", param.get("contentId"));
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
                resultMap.put("data", dataMap);
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_INSERT_PLAYLIST_CONTENTS.getCode());
                resultMap.put("message", ResponseCode.FAIL_INSERT_PLAYLIST_CONTENTS.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_DUPLICATE_PLAYLIST_CONTENTS.getCode());
            resultMap.put("message", ResponseCode.FAIL_DUPLICATE_PLAYLIST_CONTENTS.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deletePlaylistContents(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        PlaylistContentsVo playlistCheck = playlistMapper.deletePlaylistContentsValid(param);

        if(playlistCheck != null){
            int deletePlaylistContentsResult = playlistMapper.deletePlaylistContents(param);

            if(deletePlaylistContentsResult == 1){
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));

                String os = environment.getProperty("os.name");

                String filePath = null;
                if(os.equals("Linux")){
                    String LinuxPath = "/usr/local/tomcat/webapps";
                    filePath = LinuxPath;
                }
                else{
                    String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
                    filePath = desktopPath + File.separator;
                }

                String thumbnailPath = filePath + playlistCheck.getThumbnailPath();
                File fileToDelete = new File(thumbnailPath);

                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        log.info("File deleted successfully - {}", thumbnailPath);
                    } else {
                        log.info("Failed to delete the file - {}", thumbnailPath);
                    }
                } else {
                    log.info("File not found - {}", thumbnailPath);
                }
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_DELETE_PLAYLIST_CONTENTS.getCode());
                resultMap.put("message", ResponseCode.FAIL_DELETE_PLAYLIST_CONTENTS.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PLAYLIST_CONTENTS.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PLAYLIST_CONTENTS.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchContentsName(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int playlistCheck = playlistMapper.patchContentsNameValid(param);

        if(playlistCheck == 1){
            int patchPlaylistContentsNameResult = playlistMapper.patchContentsName(param);

            if(patchPlaylistContentsNameResult == 1){
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_PLAYLIST_CONTENTS.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_PLAYLIST_CONTENTS.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PLAYLIST_CONTENTS.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PLAYLIST_CONTENTS.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> patchContentsPlaytime(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        int playlistCheck = playlistMapper.patchContentsPlaytimeValid(param);

        if(playlistCheck == 1){
            int patchPlaylistContentsNameResult = playlistMapper.patchContentsPlaytime(param);

            if(patchPlaylistContentsNameResult == 1){
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
            }
            else{
                resultMap.put("code", ResponseCode.FAIL_UPDATE_PLAYLIST_CONTENTS.getCode());
                resultMap.put("message", ResponseCode.FAIL_UPDATE_PLAYLIST_CONTENTS.getMessage());
            }
        }
        else{
            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PLAYLIST_CONTENTS.getCode());
            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PLAYLIST_CONTENTS.getMessage());
        }
        return resultMap;
    }

    ////////////////////////////////////////////////////////////////////////////////////Functions.
    public List<Map<String, Object>> contentMatchingAndOrderByOrder (List<Map<String, Object>> playlistContentList,  List<Map<String, Object>> realContentList){
        for(Map<String, Object> playlistContent : playlistContentList){
            for(Map<String, Object> realContent : realContentList){
                Integer tempId = (Integer) realContent.get("contentId");
                Long realContentId = tempId != null ? tempId.longValue() : null;
                if(playlistContent.get("contentId") == realContentId ){
                    playlistContent.putAll(realContent);
                }
            }
        }
        List<Map<String, Object>> sortedList = playlistContentList.stream()
                .sorted(Comparator.comparingInt(m -> Math.toIntExact((Long) m.get("order"))))
                .collect(Collectors.toList());

        return sortedList;
    }

}