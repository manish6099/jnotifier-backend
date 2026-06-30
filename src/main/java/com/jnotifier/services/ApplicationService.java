package com.jnotifier.services;

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.jnotifier.entity.Application;
import com.jnotifier.payload.request.ApplicationRequest;
import com.jnotifier.payload.request.ApplicationUpdateRequest;

import jakarta.validation.Valid;

public interface ApplicationService {
	Application updateStatus(Long id, Boolean status);

	List<Application> findAll();

	Application findById(Long id);

	List<Application> findActiveApplications();

	Page<Application> findActiveApplications(int page, int size);

	Page<Application> findApplicationsForUser(String username, boolean isSuperAdmin, int page, int size);

	Application saveApplication(ApplicationRequest request, MultipartFile file, MultipartFile advFile)
			throws IOException;

	Application updateApplication(ApplicationUpdateRequest request, MultipartFile file, MultipartFile advFile)
			throws IOException;
}
