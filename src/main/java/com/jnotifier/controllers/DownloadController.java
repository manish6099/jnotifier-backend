package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.services.impl.MediaServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/downloads")
public class DownloadController {
    @Autowired
    private MediaServiceImpl mediaService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> download(@PathVariable String filename, HttpServletRequest request)
            throws IOException{
        return mediaService.downloadMedia(filename, request);
    }
}
