package com.jnotifier.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.entity.User;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.SignupRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.services.impl.UserServices;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServices services;

    @GetMapping("/get-me")
    public ResponseEntity<ApiResponse<Object>> getMe(Authentication authentication) {
        Map<String, String> reply = new HashMap<>();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);

        reply.put("message", "Authenticated");
        reply.put("role", role);
        return ResponseEntity.ok(ApiResponse.success(reply));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> getProfile(Authentication authentication) {
        Map<String, String> reply = new HashMap<>();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new GenericException(ApiResponse.error(
                "INVALID_CREDS", "Invalid credentials"
        )));

        reply.put("fullName", user.getFullname());
        reply.put("email", user.getEmail());
        reply.put("username", user.getUsername());
        reply.put("category", user.getCategory());
        reply.put("isPwd", Optional.ofNullable(user.getIsPwd()).orElse(false).toString());
        reply.put("dob", user.getDob().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        reply.put("gender", user.getGender());

        return ResponseEntity.ok(ApiResponse.success(reply));
    }


    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<Object>> registerAdminUsers(@Valid @RequestBody SignupRequest signupRequest) throws JsonProcessingException, GenericException {
        ServiceReply serviceReply = services.registerAdminUsers(signupRequest);
        return ResponseEntity.status(serviceReply.getHttpStatusCode()).body(ApiResponse.success(serviceReply.getReply()));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAllUserDetailsExceptSA(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size)
            throws GenericException {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        ServiceReply reply = services.getAllUsersDetailsExceptSA(pageable);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }
}
