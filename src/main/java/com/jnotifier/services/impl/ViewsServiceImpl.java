package com.jnotifier.services.impl;

import com.jnotifier.entity.Views;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.ViewsRepository;
import com.jnotifier.services.IViewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ViewsServiceImpl implements IViewsService {
    @Autowired
    private ViewsRepository viewsRepository;

    @Override
    public ServiceReply addView(String ipAddress) {
        Map<String, Object> map = new HashMap<>();
        viewsRepository.save(new Views(ipAddress));

        map.put("message", "Thanks for visiting JNotifier.");
        return new ServiceReply()
                .build(HttpStatusCode.valueOf(200), map);
    }
}
