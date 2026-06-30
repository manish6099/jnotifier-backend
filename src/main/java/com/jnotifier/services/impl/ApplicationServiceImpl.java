package com.jnotifier.services.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jnotifier.entity.Application;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.ApplicationRequest;
import com.jnotifier.payload.request.ApplicationUpdateRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.repository.ApplicationRepository;
import com.jnotifier.services.ApplicationService;
import com.jnotifier.services.core.FileStorageService;

import jakarta.validation.Valid;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private FileStorageService fileStorageService;

	@Override
	public Application updateStatus(Long id, Boolean status) {
		Application application = findById(id);
		application.setStatus(status);
		return applicationRepository.save(application);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Application> findAll() {
		return applicationRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Application findById(Long id) {
		return applicationRepository.findById(id).orElseThrow(() -> new GenericException(
				ApiResponse.error("RESOURCE_NOT_FOUND", "Application not found with id: " + id)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Application> findActiveApplications() {
		return applicationRepository.findByStatusTrue();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Application> findActiveApplications(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return applicationRepository.findByStatusTrue(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Application> findApplicationsForUser(String username, boolean isSuperAdmin, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		if (isSuperAdmin) {
			return applicationRepository.findAll(pageable);
		} else {
			return applicationRepository.findByCreatedBy(username, pageable);
		}
	}

	@Override
	public Application saveApplication(ApplicationRequest request, MultipartFile file, MultipartFile advFile)
			throws IOException {

		if (file.isEmpty())
			throw new GenericException(ApiResponse.error("FILE_EMPTY", "Please upload a file"));
		if (advFile.isEmpty())
			throw new GenericException(ApiResponse.error("FILE_EMPTY", "Please upload a file"));

		String originalFilename = file.getOriginalFilename();
		String contentType = file.getContentType();

		String advFilename = advFile.getOriginalFilename();
		String advContentType = advFile.getContentType();

		boolean hasMdExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".md");
		boolean hasMdMimeType = "text/markdown".equalsIgnoreCase(contentType);
		boolean hasPdfExtension = advFilename != null && advFilename.toLowerCase().endsWith(".pdf");
		boolean hasPdfMimeType = "application/pdf".equalsIgnoreCase(advContentType);

		long fileSize = file.getSize() / (1024 * 1024);
		long advFileSize = advFile.getSize() / (1024 * 1024);

		if (!hasMdExtension && !hasMdMimeType)
			throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a markdown file."));
		if (!hasPdfExtension && !hasPdfMimeType)
			throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a pdf file."));

		if (fileSize > 5)
			throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));
		if (advFileSize > 50)
			throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

		byte[] fileBytes = file.getBytes();
		String markdownContent = new String(fileBytes, StandardCharsets.UTF_8);

		request.setViewPageDescription(markdownContent);
		request.setAdvFileName("/uploads/" + fileStorageService.saveFile(advFile));

		Application application = new Application();
		application.setTitle(request.getTitle());
		application.setTags(request.getTags());
		application.setApplicationStartDate(request.getApplicationStartDate());
		application.setApplicationEndDate(request.getApplicationEndDate());
		application.setShortDescription(request.getShortDescription());
		application.setViewPageDescription(request.getViewPageDescription());
		application.setApplyLink(request.getApplyLink());
		application.setStatus(true);
		application.setAdvFileName(request.getAdvFileName());

		if (request.getAdvNo() != null) {
			application.setAdvertisementNo(request.getAdvNo());
		}

		return applicationRepository.save(application);

	}

	@Override
	public Application updateApplication(ApplicationUpdateRequest request, MultipartFile file, MultipartFile advFile)
			throws IOException {

		Application currApplication = findById(request.getApplicationId());

		if (!file.isEmpty()) {
			String originalFilename = file.getOriginalFilename();
			String contentType = file.getContentType();

			boolean hasMdExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".md");
			boolean hasMdMimeType = "text/markdown".equalsIgnoreCase(contentType);

			long fileSize = file.getSize() / (1024 * 1024);

			if (!hasMdExtension && !hasMdMimeType)
				throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a markdown file."));

			if (fileSize > 5)
				throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

			byte[] fileBytes = file.getBytes();
			String markdownContent = new String(fileBytes, StandardCharsets.UTF_8);

			request.setViewPageDescription(markdownContent);

		}

		if (!advFile.isEmpty()) {

			String advFilename = advFile.getOriginalFilename();
			String advContentType = advFile.getContentType();

			boolean hasPdfExtension = advFilename != null && advFilename.toLowerCase().endsWith(".pdf");
			boolean hasPdfMimeType = "application/pdf".equalsIgnoreCase(advContentType);

			long advFileSize = advFile.getSize() / (1024 * 1024);

			if (!hasPdfExtension && !hasPdfMimeType)
				throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a pdf file."));

			if (advFileSize > 50)
				throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

			request.setAdvFileName("/uploads/" + fileStorageService.saveFile(advFile));

		}

		currApplication.setTags(request.getTags());
		currApplication.setShortDescription(request.getShortDescription());
		currApplication.setViewPageDescription(request.getViewPageDescription());
		currApplication.setApplyLink(request.getApplyLink());
		currApplication.setAdvFileName(request.getAdvFileName());

		return applicationRepository.save(currApplication);

	}

}
