package com.jnotifier.services;

import com.jnotifier.payload.response.ServiceReply;

import java.util.Map;

public interface IViewsService {
    public ServiceReply addView(Map<String,String> clientDetails);
}
