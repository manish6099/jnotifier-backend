package com.jnotifier.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotifier.entity.ERole;
import com.jnotifier.entity.Role;
import com.jnotifier.entity.User;
import com.jnotifier.exception.GenericException;
import com.jnotifier.helpers.CaptchaHelper;
import com.jnotifier.helpers.EmailHelper;
import com.jnotifier.payload.request.SignupRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.RoleRepository;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServices implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private EmailHelper emailHelper;
    @Autowired
    private CaptchaHelper captchaHelper;

    @Value("${jnotifier.app.default-otp-enabled}")
    private boolean defaultOtpEnabled;
    @Value("${notification.support.email}")
    private String notificationSupportEmail;

    private static final Map<String, String> otpStore = new ConcurrentHashMap<>();

    private Map<String, String> getOtpPayload(User user, String otp) {
        Map<String, String> payload = new HashMap<>();

        payload.put("userName", user.getUsername());
        payload.put("name", user.getFullname());
        payload.put("subject", "Verification of newly created account reg.");
        payload.put("email", user.getEmail());
        payload.put("supportEmail", notificationSupportEmail);
        payload.put("gender", user.getGender());
        payload.put("category", user.getCategory());
        payload.put("dob", user.getDob().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        payload.put("otp", otp);

        return payload;
    }

    @Override
    public ServiceReply registerAdminUsers(SignupRequest signUpRequest) throws JsonProcessingException, GenericException {
        String captchaId = signUpRequest.getCaptchaId();
        String captcha = signUpRequest.getCaptcha();

        if (!captchaHelper.validateCaptcha(captchaId, captcha))
            throw new GenericException(ApiResponse.error("INVALID_CAPTCHA", "Captcha is incorrect or expired."));
        captchaHelper.clearCaptcha(captchaId);

        if (userRepository.existsByEmail(signUpRequest.getEmail()))
            throw new GenericException(ApiResponse.error("BAD_REQUEST", "Error: Email is already in use!"));

        String requestedRole = signUpRequest.getRole();
        if (requestedRole == null || requestedRole.trim().isEmpty()) {
            requestedRole = "user";
        }

        if (requestedRole.equalsIgnoreCase("user"))
            throw new GenericException(ApiResponse.error("INVALID_ROLE", "Invalid Role."));

        Role userRole;

        //Deciding and assigning the appropriate role to the signup dto.
        if (requestedRole.equalsIgnoreCase("admin")) {
            Authentication callerAuth = SecurityContextHolder.getContext().getAuthentication();

            if (!callerAuth.isAuthenticated() ||
                    callerAuth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"))) {
                throw new GenericException(ApiResponse.error("FORBIDDEN", "Error: Only SUPERADMIN accounts can register new ADMIN users."));
            }

            userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: ADMIN role not initialized in database."));
        } else {
            userRole = roleRepository.findByName(ERole.ROLE_SUPERADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: SUPERADMIN role not initialized in database."));
        }

        // Generate unique system-level username containing timestamp & name alphabets
        String cleanName = signUpRequest.getFullName().toLowerCase().replaceAll("[^a-zA-Z]", "");
        if (cleanName.isEmpty()) {
            cleanName = "user";
        }

        String generatedUsername = cleanName + "_" + System.currentTimeMillis();

        // Create new user's account
        User user = new User(
                signUpRequest.getFullName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getDob(),
                signUpRequest.getGender(),
                signUpRequest.getMobile(), signUpRequest.getCategory(), signUpRequest.getIsPwd(), false);

        user.setUsername(generatedUsername);
        user.setRole(userRole);
        userRepository.save(user);

        String otpCode = "123456";

        if (!defaultOtpEnabled) {
            otpCode = String.format("%06d", new Random().nextInt(100000, 999999));
        }
        otpStore.put(generatedUsername, otpCode);

        if (!defaultOtpEnabled) {
            Map<String, String> content = getOtpPayload(user, otpCode);
            emailHelper.sendWelcomeNotification(content);
        }

        Map<String, Object> replyContent = new HashMap<>();

        replyContent.put("message", "Admin created successfully!");
        replyContent.put("username", user.getUsername());
        return new ServiceReply().build(HttpStatusCode.valueOf(201), replyContent);
    }
}
