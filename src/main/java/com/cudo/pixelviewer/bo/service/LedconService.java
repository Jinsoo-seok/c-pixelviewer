package com.cudo.pixelviewer.bo.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface LedconService {

    Map<String, Object> getLedconList();

    Map<String, Object> postLedcon(Map<String, Object> param) throws ExecutionException, InterruptedException, TimeoutException;

    Map<String, Object> deleteLedcon(Map<String, Object> param);

    Map<String, Object> putLedcon(Map<String, Object> param) throws ExecutionException, InterruptedException, TimeoutException;

}