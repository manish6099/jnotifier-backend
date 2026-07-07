package com.jnotifier.services.impl;

import com.jnotifier.entity.Views;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.ViewsRepository;
import com.jnotifier.services.IViewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ViewsServiceImpl implements IViewsService {
    @Autowired
    private ViewsRepository viewsRepository;

    @Override
    public ServiceReply addView(Map<String, String> clientDetails) {
        Map<String, Object> map = new HashMap<>();

        String ipAddress = clientDetails.get("ipAddress");
        String visitedPage = clientDetails.get("visitedPage");
        String browserName = clientDetails.get("browserName");
        String browserVersion = clientDetails.get("browserVersion");
        String osName = clientDetails.get("osName");
        String deviceType = clientDetails.get("deviceType");
        String deviceVendor = clientDetails.get("deviceVendor");
        String visitedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        viewsRepository.save(new Views(ipAddress, visitedDate, visitedPage, browserName, browserVersion, osName,
                deviceType, deviceVendor));

        map.put("message", "Thanks for visiting JNotifier.");
        return new ServiceReply()
                .build(HttpStatusCode.valueOf(200), map);
    }
}
