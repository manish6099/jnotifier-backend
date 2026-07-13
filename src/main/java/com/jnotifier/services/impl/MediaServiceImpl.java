package com.jnotifier.services.impl;

import com.jnotifier.entity.Media;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.AddNewMediaRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.MediaRepository;
import com.jnotifier.services.IMediaService;
import com.jnotifier.services.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class MediaServiceImpl implements IMediaService {
    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public ServiceReply addMedia(MultipartFile file) {
        if (file == null)
            throw new GenericException(ApiResponse.error("FILE_ERR", "File is required"));

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        if (contentType == null)
            throw new GenericException(ApiResponse.error("FILE_ERR", "File content type is required"));

        final String contentTypeRegex = "^(application/pdf|image/.*)$";
        boolean hasInvalidExtension = !contentType.matches(contentTypeRegex);

        if (hasInvalidExtension)
            throw new GenericException(ApiResponse.error("FILE_TYPE_ERR", "Invalid file extension, unsupported file detected"));

        long fileSize = file.getSize() / (1024 * 1024);

        if (fileSize >= 5)
            throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

        String fileUri = fileStorageService.saveFile(file);

        Media media = new Media(fileName, contentType.toLowerCase(), fileSize, fileUri);
        mediaRepository.save(media);

        Map<String, Object> map = new HashMap<>();

        map.put("message", "File successfully uploaded.");
        map.put("fileUri", "/uploads/" + fileUri);

        return new ServiceReply().build(HttpStatusCode.valueOf(201), map);
    }

    @Override
    public ServiceReply listActiveMedia(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Media> mediaList = mediaRepository.findAllActiveMedia(pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("mediaList", mediaList);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply listAllMedia(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Media> mediaList = mediaRepository.findAll(pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("mediaList", mediaList);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply changeMediaVisibility(Long mediaId, Boolean visibility) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new GenericException(ApiResponse.error("MEDIA_ERR", "Invalid media")));

        media.setIsPublic(visibility);
        mediaRepository.save(media);

        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply deleteMedia(Long mediaId) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new GenericException(ApiResponse.error("MEDIA_ERR", "Invalid media")));

        media.setIsDeleted(true);
        mediaRepository.save(media);

        return new ServiceReply().build(HttpStatusCode.valueOf(200));
    }
}
