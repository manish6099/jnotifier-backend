package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;

public class UpdateNoticeRequest {
    @NotNull(message = "Notice Id is required.")
    private Long noticeId;

    private String noticeTitle;

    private String noticeDesc;

    private String noticeAdvertisement;

    private String noticeTags;

    public UpdateNoticeRequest() {
    }

    public UpdateNoticeRequest(Long noticeId, String title, String desc, String advertisement, String tags) {
        this.noticeTitle = title;
        this.noticeDesc = desc;
        this.noticeAdvertisement = advertisement;
        this.noticeTags = tags;
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeDesc() {
        return noticeDesc;
    }

    public void setNoticeDesc(String noticeDesc) {
        this.noticeDesc = noticeDesc;
    }

    public String getNoticeAdvertisement() {
        return noticeAdvertisement;
    }

    public void setNoticeAdvertisement(String noticeAdvertisement) {
        this.noticeAdvertisement = noticeAdvertisement;
    }

    public String getNoticeTags() {
        return noticeTags;
    }

    public void setNoticeTags(String noticeTags) {
        this.noticeTags = noticeTags;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }
}
