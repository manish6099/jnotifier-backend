package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.entity.User;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/get-me")
    public ResponseEntity<ApiResponse<Object>> getMe() {
        Map<String, String> reply = new HashMap<>();

        reply.put("message", "Authenticated");
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
        reply.put("isPwd", user.getIsPwd().toString());
        reply.put("dob", user.getDob().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        reply.put("gender",user.getGender());

        return ResponseEntity.ok(ApiResponse.success(reply));
    }
}
