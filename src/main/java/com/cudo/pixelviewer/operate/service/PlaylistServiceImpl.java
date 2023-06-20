package com.cudo.pixelviewer.operate.service;

import com.cudo.pixelviewer.operate.mapper.PlaylistMapper;
import com.cudo.pixelviewer.util.ParameterUtils;
import com.cudo.pixelviewer.util.ResponseCode;
import com.cudo.pixelviewer.vo.PlaylistContentsVo;
import com.cudo.pixelviewer.vo.PlaylistVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Map<String, Object> getPlaylist(String Id) {
        Map<String, Object> resultMap = new HashMap<>();

        PlaylistVo playlistVo = playlistMapper.getPlaylist(Id);

        if(playlistVo != null){
            resultMap.put("data", playlistVo);
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

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String, Object> deletePlaylist(Map<String, Object> param) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        int playlistCheck = playlistMapper.deletePlaylistValid(param);
//
//        if(playlistCheck == 1){  // Exist : 1
//            int deletePlaylistResult = playlistMapper.deletePlaylist(param);
//
//            if(deletePlaylistResult == 1){ // Success : 1
//                resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
//            }
//            else{
//                resultMap.put("code", ResponseCode.FAIL_DELETE_PLAYLIST.getCode());
//                resultMap.put("message", ResponseCode.FAIL_DELETE_PLAYLIST.getMessage());
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

        if(playlistVo != null){
            resultMap.put("data", playlistVo);
            resultMap.putAll(ParameterUtils.responseOption(ResponseCode.SUCCESS.getCodeName()));
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
                dataMap.put("itemId", param.get("itemId"));
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