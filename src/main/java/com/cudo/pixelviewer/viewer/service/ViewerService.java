package com.cudo.pixelviewer.viewer.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ViewerService {

    Map<String, Object> getPlayInfo(String screenId, String presetId, String layerId);

    Map<String, Object> putUpdateAndHealthCheck(Map<String, Object> param);

    Map<String, Object> postPreviewImgUpload(String screenId, MultipartFile file);

}