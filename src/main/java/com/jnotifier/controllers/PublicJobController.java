package com.jnotifier.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.jnotifier.app.JNotifierConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.CategoryPublicResponse;
import com.jnotifier.payload.response.JobApplicationResponse;
import com.jnotifier.payload.response.PaginatedResponse;
import com.jnotifier.services.ApplicationService;
import com.jnotifier.services.CategoryService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/public")
public class PublicJobController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CategoryService categoryService;

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
                        app.getShortDescription()
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
}
