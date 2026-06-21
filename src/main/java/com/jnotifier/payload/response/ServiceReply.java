package com.jnotifier.payload.response;

import org.springframework.http.HttpStatusCode;

import java.util.Map;

public class ServiceReply {
    private HttpStatusCode httpStatusCode;
    private Map<String, Object> reply;

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Map<String, Object> getReply() {
        return reply;
    }

    public void setReply(Map<String, Object> reply) {
        this.reply = reply;
    }
}
