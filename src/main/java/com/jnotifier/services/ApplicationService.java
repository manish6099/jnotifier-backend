package com.jnotifier.services;

import java.util.List;
import org.springframework.data.domain.Page;
import com.jnotifier.entity.Application;
import com.jnotifier.payload.request.ApplicationRequest;

public interface ApplicationService {
  Application save(ApplicationRequest request);
  Application update(Long id, ApplicationRequest request);
  Application updateStatus(Long id, Boolean status);
  List<Application> findAll();
  Application findById(Long id);
  List<Application> findActiveApplications();
  Page<Application> findActiveApplications(int page, int size);
  Page<Application> findApplicationsForUser(String username, boolean isSuperAdmin, int page, int size);
}
