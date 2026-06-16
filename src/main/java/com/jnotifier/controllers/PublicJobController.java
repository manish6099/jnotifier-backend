package com.jnotifier.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import com.jnotifier.entity.Application;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.CategoryPublicResponse;
import com.jnotifier.payload.response.JobApplicationResponse;
import com.jnotifier.payload.response.PaginatedResponse;
import com.jnotifier.services.ApplicationService;
import com.jnotifier.services.CategoryService;
import com.jnotifier.services.FileStorageService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/public")
public class PublicJobController {

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private FileStorageService fileStorageService;

  @GetMapping("/jobs")
  public ResponseEntity<ApiResponse<PaginatedResponse<JobApplicationResponse>>> getActiveJobList(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<JobApplicationResponse> jobsPage = applicationService.findActiveApplications(page, size)
        .map(app -> new JobApplicationResponse(
            app.getTitle(),
            app.getTags(),
            app.getApplicationStartDate(),
            app.getApplicationEndDate(),
            app.getShortDescription(),
            app.getApplyLink(),
            app.getNotificationPdfFilename()
        ));
    
    return ResponseEntity.ok(ApiResponse.success(new PaginatedResponse<>(jobsPage)));
  }

  @GetMapping("/applications/{applicationId}/categories")
  public ResponseEntity<ApiResponse<List<CategoryPublicResponse>>> getCategoriesByApplicationId(@PathVariable Long applicationId) {
    List<CategoryPublicResponse> categories = categoryService.findActiveByApplicationId(applicationId).stream()
        .map(cat -> new CategoryPublicResponse(
            cat.getCategoryName(),
            cat.getCategoryDesc(),
            cat.getOrderId()
        ))
        .collect(Collectors.toList());

    return ResponseEntity.ok(ApiResponse.success(categories));
  }

  @GetMapping("/applications/{id}/download")
  public ResponseEntity<Resource> downloadNotificationPdf(@PathVariable Long id) {
    Application app = applicationService.findById(id);
    String filename = app.getNotificationPdfFilename();
    if (filename == null || filename.trim().isEmpty()) {
      throw new GenericException(ApiResponse.error("FILE_NOT_FOUND", "No PDF uploaded for this application."));
    }
    
    Resource resource = fileStorageService.loadFileAsResource(filename);
    
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        .body(resource);
  }
}
