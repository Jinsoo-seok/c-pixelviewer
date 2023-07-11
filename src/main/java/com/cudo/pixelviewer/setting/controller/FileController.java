package com.cudo.pixelviewer.setting.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class FileController {

    private final ResourceLoader resourceLoader;

    public FileController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/file/{fileType}/{fileName}")
    public ResponseEntity<Resource> getFileAgent(@PathVariable String fileType, @PathVariable String fileName) {
        Resource resource = null;
        HttpHeaders headers = new HttpHeaders();
        if(fileType.equals("agent") || fileType.equals("thumbnails") || fileType.equals("weather")){

            resource = resourceLoader.getResource("file:" + System.getProperty("user.home") + "/Desktop/" + fileType + "/" + fileName);

            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
        }
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
