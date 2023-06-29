package com.cudo.pixelviewer.operate.service;

import java.util.Map;

public interface PlaylistService {

    Map<String, Object> getPlaylistList();

    Map<String, Object> getPlaylist(String layerId);

    Map<String, Object> postPlaylist(Map<String, Object> param);

    Map<String, Object> putPlaylist(Map<String, Object> param);

    Map<String, Object> deletePlaylist(Map<String, Object> param);


    // Contents
    Map<String, Object> getPlaylistContentsList();

    Map<String, Object> getPlaylistContents(String Id);

    Map<String, Object> postPlaylistContents(Map<String, Object> param);

    Map<String, Object> deletePlaylistContents(Map<String, Object> param);

    Map<String, Object> patchContentsName(Map<String, Object> param);

    Map<String, Object> patchContentsPlaytime(Map<String, Object> param);

}