package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.PlaylistContentsVo;
import com.cudo.pixelviewer.vo.PlaylistVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    final PlaylistMapper playlistMapper;

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

//        PlaylistVo playlistVo = playlistMapper.getPlaylist(layerId);
        Map<String, Object> playlist = playlistMapper.getPlaylist(layerId);

        if(playlist != null){
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

            // TODO : Order By(order 기준)
            List<Map<String, Object>> playlistContentList = playlistMapper.getPlaylistContentList(queryTemp);
            Long order = 1L;
            if(playlistContentList.size() != 0){
                List<Map<String, Object>> tempContentArray = new ArrayList<>();

                for (int i = 0; i < tempContentIdList.size(); i++) {
                    for(int x = 0; x < tempContentIdList.size(); x++) {
                        Long tempOrder = (Long) tempContentIdList.get(x).get("order");
                        if (tempOrder == order) {
                            Long orderContentId = (Long) tempContentIdList.get(x).get("contentId");
                            for (Map<String, Object> playlistContent : playlistContentList) {
                                Integer dbContentId = (Integer) playlistContent.get("contentId");
                                if (orderContentId.equals(dbContentId.longValue())) {
                                    System.out.println("orderContentId == dbContentId");
                                    playlistContent.put("weatherFl", tempContentIdList.get(x).get("weatherFl"));
                                    playlistContent.put("airInfoFl", tempContentIdList.get(x).get("airInfoFl"));
                                    playlistContent.put("stretch", tempContentIdList.get(x).get("stretch"));
                                    playlistContent.put("order", tempContentIdList.get(x).get("order"));
                                    tempContentArray.add(playlistContent);
                                }
                            }
                            order++;
                        }
                    }
                }
                playlist.put("contentArray", tempContentArray);
                String removeKey = "contentIdList";
                playlist.remove(removeKey);
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

        if(playlistCheck == 0){ // Not Exist : 0
            int postPlaylistResult = playlistMapper.postPlaylist(param);

            if(postPlaylistResult == 1){ // Success : 1
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

        if(playlistCheck == 1){ // Success : 1
            try {
                ObjectMapper mapper = new ObjectMapper();
                String mapperJson = mapper.writeValueAsString(param.get("contentIdList"));
                param.put("contentIdList", mapperJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            int putPlaylistResult = playlistMapper.putPlaylist(param);

            if(putPlaylistResult == 1){ // Success : 1
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

        if(playlistCheck == 1){  // Exist : 1
            int deletePlaylistResult = playlistMapper.deletePlaylist(param);

            if(deletePlaylistResult == 1){ // Success : 1
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
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> patchPlaylistName(Map<String, Object> param) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        int playlistCheck = playlistMapper.patchPlaylistNameValid(param);
//
//        if(playlistCheck == 1){  // Exist : 1
//            int patchPlaylistNameResult = playlistMapper.patchPlaylistName(param);
//
//            if(patchPlaylistNameResult == 1){ // Success : 1
//                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//            }
//            else{
//                resultMap.put("code", ResponseCode.FAIL_UPDATE_PLAYLIST.getCode());
//                resultMap.put("message", ResponseCode.FAIL_UPDATE_PLAYLIST.getMessage());
//            }
//        }
//        else{
//            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PLAYLIST.getCode());
//            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PLAYLIST.getMessage());
//        }
//        return resultMap;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> putPlaylistSet(Map<String, Object> param) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        int playlistCheck = playlistMapper.putPlaylistValid(param);
//
//        if(playlistCheck == 1){  // Exist : 1
//            int putPlaylistResult = playlistMapper.putPlaylist(param);
//
//            if(putPlaylistResult == 1){ // Success : 1
//                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//            }
//            else{
//                resultMap.put("code", ResponseCode.FAIL_UPDATE_PLAYLIST.getCode());
//                resultMap.put("message", ResponseCode.FAIL_UPDATE_PLAYLIST.getMessage());
//            }
//        }
//        else{
//            resultMap.put("code", ResponseCode.FAIL_NOT_EXIST_PLAYLIST.getCode());
//            resultMap.put("message", ResponseCode.FAIL_NOT_EXIST_PLAYLIST.getMessage());
//        }
//        return resultMap;
//    }

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

        if(playlistCheck == 0){ // Not Exist : 0
            int postPlaylistContentsResult = playlistMapper.postPlaylistContents(param);

            if(postPlaylistContentsResult == 1){ // Success : 1
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

        int playlistCheck = playlistMapper.deletePlaylistContentsValid(param);

        if(playlistCheck == 1){  // Exist : 1
            int deletePlaylistContentsResult = playlistMapper.deletePlaylistContents(param);

            if(deletePlaylistContentsResult == 1){ // Success : 1
                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
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

        if(playlistCheck == 1){  // Exist : 1
            int patchPlaylistContentsNameResult = playlistMapper.patchContentsName(param);

            if(patchPlaylistContentsNameResult == 1){ // Success : 1
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

        if(playlistCheck == 1){  // Exist : 1
            int patchPlaylistContentsNameResult = playlistMapper.patchContentsPlaytime(param);

            if(patchPlaylistContentsNameResult == 1){ // Success : 1
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

}