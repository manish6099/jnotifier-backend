package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;

public class AddNewNoticeRequest {
    @NotNull(message = "Notice title is required.")
    private String noticeTitle;

    @NotNull(message = "Notice title is required.")
    private String noticeDesc;

    private String noticeAdvertisement;

    @NotNull(message = "Notice tags is required")
    private String noticeTags;

    public AddNewNoticeRequest() {
    }

    public AddNewNoticeRequest(String title, String desc, String advertisement, String tags) {
        this.noticeTitle = title;
        this.noticeDesc = desc;
        this.noticeAdvertisement = advertisement;
        this.noticeTags = tags;
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
}
