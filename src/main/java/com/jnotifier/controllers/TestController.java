package com.jnotifier.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jnotifier.payload.response.ApiResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/test")
public class TestController {
  @GetMapping("/all")
  public ResponseEntity<ApiResponse<String>> allAccess() {
    return ResponseEntity.ok(ApiResponse.success("Public Content."));
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
  public ResponseEntity<ApiResponse<String>> userAccess() {
    return ResponseEntity.ok(ApiResponse.success("User Content."));
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<String>> adminAccess() {
    return ResponseEntity.ok(ApiResponse.success("Admin Board."));
  }

  @GetMapping("/superadmin")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<ApiResponse<String>> superAdminAccess() {
    return ResponseEntity.ok(ApiResponse.success("Superadmin Board."));
  }
}
