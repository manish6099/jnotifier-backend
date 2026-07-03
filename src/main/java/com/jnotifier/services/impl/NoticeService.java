package com.jnotifier.services.impl;

import com.jnotifier.entity.Notice;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.AddNewNoticeRequest;
import com.jnotifier.payload.request.UpdateNoticeRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.NoticeRepository;
import com.jnotifier.services.INoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NoticeService implements INoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public ServiceReply addNewNotice(AddNewNoticeRequest request, MultipartFile noticeAdvertisement) throws IOException {
        if (noticeAdvertisement.isEmpty()) throw new GenericException(ApiResponse.error("FILE_EMPTY", "Please upload notice advertisement file"));

        String advFilename = noticeAdvertisement.getOriginalFilename();
        String advContentType = noticeAdvertisement.getContentType();
        boolean hasMdExtension = advFilename != null && advFilename.toLowerCase().endsWith(".md");
        boolean hasMdMimeType = "text/markdown".equalsIgnoreCase(advContentType);

        if (!hasMdExtension && !hasMdMimeType)
            throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a markdown file."));

        long fileSize = noticeAdvertisement.getSize() / (1024 * 1024);

        if (fileSize >=5)
            throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

        byte[] fileBytes = noticeAdvertisement.getBytes();
        String markdownContent = new String(fileBytes, StandardCharsets.UTF_8);
        request.setNoticeAdvertisement(markdownContent);

        Notice notice = new Notice(request.getNoticeTitle(), request.getNoticeDesc(), request.getNoticeTags(), request.getNoticeAdvertisement());
        Map<String, Object> map = new HashMap<>();

        noticeRepository.save(notice);
        map.put("message", "Notice added");
        map.put("data", notice);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply updateNotice(UpdateNoticeRequest request) {
        Notice notice = noticeRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_NOTICE", "This notice does not exist")));
        Map<String, Object> map = new HashMap<>();

        notice.setTitle(Optional.of(request.getNoticeTitle()).orElse(notice.getTitle()));
        notice.setNoticeDescription(Optional.of(request.getNoticeDesc()).orElse(notice.getNoticeDescription()));
        notice.setNoticeDetailedAdv(Optional.of(request.getNoticeAdvertisement()).orElse(notice.getNoticeDetailedAdv()));
        notice.setTags(Optional.of(request.getNoticeTags()).orElse(notice.getTags()));

        noticeRepository.save(notice);
        map.put("message", "Notice updated");
        map.put("data", notice);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_NOTICE", "This notice does not exist")));
        Map<String, Object> map = new HashMap<>();

        notice.setIsDeleted(true);
        noticeRepository.save(notice);
        map.put("message", "Notice deleted");
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply markAsArchived(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_NOTICE", "This notice does not exist")));
        Map<String, Object> map = new HashMap<>();

        notice.setIsActive(true);
        noticeRepository.save(notice);
        map.put("message", "Notice marked as archived.");
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllActiveNotices(String createdBy, Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<Notice> notices = noticeRepository.findByIsActive(true, createdBy, pageable);

        map.put("message", "All active notices are fetched successfully");
        map.put("list", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllDeletedNotices(String createdBy, Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<Notice> notices = noticeRepository.findByIsDeleted(true, createdBy, pageable);

        map.put("message", "All deleted notices are fetched successfully");
        map.put("list", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getNotice(Long id) {
        Map<String, Object> map = new HashMap<>();
        Notice notices = noticeRepository.findById(id).orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_NOTICE", "This notice does not exists")));

        map.put("message", "Notice found");
        map.put("notice", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllArchivedNotices(String createdBy, Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<Notice> notices = noticeRepository.findByIsActive(false, createdBy, pageable);

        map.put("message", "All archived notices are fetched successfully");
        map.put("list", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }
}
