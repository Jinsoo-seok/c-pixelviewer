package com.cudo.pixelviewer.setting.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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

import java.io.File;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class FileController {

    private final ResourceLoader resourceLoader;

    private final Environment environment;

    public FileController(ResourceLoader resourceLoader, Environment environment) {
        this.resourceLoader = resourceLoader;
        this.environment = environment;
    }

    @GetMapping("/file/{fileType}/{fileName}")
    public ResponseEntity<Resource> getFileAgent(@PathVariable String fileType, @PathVariable String fileName) {
        Resource resource = null;
        HttpHeaders headers = new HttpHeaders();
        if(fileType.equals("agent") || fileType.equals("thumbnails") || fileType.equals("weather")){

            String os = environment.getProperty("os.name");

            if(os.equals("Linux")){
                // Linux
                resource = resourceLoader.getResource("file:" + "/usr/local/tomcat/webapps/" + fileType + File.separator + fileName);
            }
            else{
                // Windows
                resource = resourceLoader.getResource("file:" + System.getProperty("user.home") + "/Desktop/" + fileType + "/" + fileName);
            }

            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
        }
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
