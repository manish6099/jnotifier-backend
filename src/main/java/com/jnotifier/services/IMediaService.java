package com.jnotifier.services;

import com.jnotifier.payload.response.ServiceReply;
import org.springframework.web.multipart.MultipartFile;

public interface IMediaService {
    public ServiceReply addMedia(MultipartFile file);

    public ServiceReply listActiveMedia(int page, int size);

    public ServiceReply listAllMedia(int page, int size);

    public ServiceReply changeMediaVisibility(Long mediaId, Boolean visibility);

    public ServiceReply deleteMedia(Long mediaId);
}
