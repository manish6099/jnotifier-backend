package com.jnotifier.controllers;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import com.jnotifier.entity.Application;
import com.jnotifier.entity.Category;
import com.jnotifier.payload.request.ApplicationRequest;
import com.jnotifier.payload.request.CategoryRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.PaginatedResponse;
import com.jnotifier.services.ApplicationService;
import com.jnotifier.services.CategoryService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminJobController {

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private CategoryService categoryService;

  // --- Job Application Endpoints ---

  @PostMapping(value = "/applications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Application>> createApplication(
      @RequestPart("application") @Valid ApplicationRequest request,
      @RequestPart("file") MultipartFile file) {
    Application saved = applicationService.save(request, file);
    return ResponseEntity.ok(ApiResponse.success(saved));
  }

  @PutMapping(value = "/applications/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Application>> updateApplication(
      @PathVariable Long id,
      @RequestPart("application") @Valid ApplicationRequest request,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    Application updated = applicationService.update(id, request, file);
    return ResponseEntity.ok(ApiResponse.success(updated));
  }

  @PatchMapping("/applications/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Application>> updateApplicationStatus(@PathVariable Long id,
      @RequestParam("active") Boolean active) {
    Application updated = applicationService.updateStatus(id, active);
    return ResponseEntity.ok(ApiResponse.success(updated));
  }

  @GetMapping("/applications")
  public ResponseEntity<ApiResponse<PaginatedResponse<Application>>> getAllApplications(
      Authentication authentication,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    
    boolean isSuperAdmin = authentication.getAuthorities().stream()
        .anyMatch(r -> r.getAuthority().equals("ROLE_SUPERADMIN"));
    String username = authentication.getName();
    
    Page<Application> applicationsPage = applicationService.findApplicationsForUser(username, isSuperAdmin, page, size);
    
    return ResponseEntity.ok(ApiResponse.success(new PaginatedResponse<>(applicationsPage)));
  }

  @GetMapping("/applications/{id}")
  public ResponseEntity<ApiResponse<Application>> getApplicationById(@PathVariable Long id) {
    Application application = applicationService.findById(id);
    return ResponseEntity.ok(ApiResponse.success(application));
  }

  // --- Category Endpoints ---

  @PostMapping("/categories")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryRequest request) {
    Category saved = categoryService.save(request);
    return ResponseEntity.ok(ApiResponse.success(saved));
  }

  @PutMapping("/categories/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id,
      @Valid @RequestBody CategoryRequest request) {
    Category updated = categoryService.update(id, request);
    return ResponseEntity.ok(ApiResponse.success(updated));
  }

  @PatchMapping("/categories/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Category>> updateCategoryStatus(@PathVariable Long id,
      @RequestParam("active") Boolean active) {
    Category updated = categoryService.updateStatus(id, active);
    return ResponseEntity.ok(ApiResponse.success(updated));
  }

  @GetMapping("/categories")
  public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
    List<Category> categories = categoryService.findAll();
    return ResponseEntity.ok(ApiResponse.success(categories));
  }

  @GetMapping("/categories/{id}")
  public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
    Category category = categoryService.findById(id);
    return ResponseEntity.ok(ApiResponse.success(category));
  }
}
