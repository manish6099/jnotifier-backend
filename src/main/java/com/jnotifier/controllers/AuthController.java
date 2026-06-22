package com.jnotifier.controllers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.app.JNotifierEnums;
import com.jnotifier.payload.request.*;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.RedisService;
import com.jnotifier.services.impl.VerifyService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.jnotifier.entity.ERole;
import com.jnotifier.entity.Role;
import com.jnotifier.entity.User;
import com.jnotifier.entity.RefreshToken;
import com.jnotifier.payload.response.JwtResponse;
import com.jnotifier.payload.response.TokenRefreshResponse;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.repository.RoleRepository;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.security.jwt.JwtUtils;
import com.jnotifier.services.RefreshTokenService;
import com.jnotifier.exception.GenericException;
import com.jnotifier.exception.TokenRefreshException;


@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // Stores captchaId -> captchaCode
    private static final Map<String, String> captchaStore = new ConcurrentHashMap<>();

    // Stores username -> otpCode
    private static final Map<String, String> otpStore = new ConcurrentHashMap<>();

    @Value("${jnotifier.app.default-otp-enabled}")
    private boolean defaultOtpEnabled;

    @Value("${jnotifier.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    private RedisService redisService;

    @Value("${notification.support.email}")
    private String notificationSupportEmail;

    @Autowired
    private VerifyService verifyService;

    @GetMapping("/captcha")
    public ResponseEntity<ApiResponse<Map<String, String>>> getCaptcha() {
        String captchaId = UUID.randomUUID().toString();
        String captchaCode = generateRandomText();
        captchaStore.put(captchaId, captchaCode);

        String captchaImageBase64 = generateCaptchaImage(captchaCode);

        Map<String, String> response = new HashMap<>();
        response.put("captchaId", captchaId);
        response.put("captchaImage", captchaImageBase64);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<Map<String, String>>> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        Map<String, Object> welcomeNotificationMsg = new HashMap<>();
        Map<String, String> welcomeNotificationContent = new HashMap<>();
        // 1. Validate Captcha
        String correctCaptcha = captchaStore.get(loginRequest.getCaptchaId());
        if (correctCaptcha == null || !correctCaptcha.equalsIgnoreCase(loginRequest.getCaptchaValue())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("INVALID_CAPTCHA", "Captcha is incorrect or expired."));
        }
        captchaStore.remove(loginRequest.getCaptchaId());

        // 2. Authenticate username and password credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 3. Generate OTP
        String otpCode = "123456";

        if (!defaultOtpEnabled) {
            otpCode = String.format("%06d", new Random().nextInt(100000, 999999));
        }

        otpStore.put(loginRequest.getUsername(), otpCode);

        // Simulate sending OTP to user's registered email
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new GenericException(ApiResponse.error("USER_NOT_FOUND", "Username or Password is wrong.")));

        logger.info("[OTP Verification] Generated OTP {} for user {}", otpCode, loginRequest.getUsername());
        logger.info("[OTP Verification] Sending OTP email to {}", user.getEmail());

        Map<String, String> data = new HashMap<>();
        data.put("username", loginRequest.getUsername());
        data.put("status", "OTP_REQUIRED");
        data.put("message", "OTP verification code has been generated. Please verify to complete sign-in.");

        //Creating content object for welcome notification
        welcomeNotificationContent.put("userName", user.getUsername());
        welcomeNotificationContent.put("name", user.getFullname());
        welcomeNotificationContent.put("subject", "Request for new OTP reg.");
        welcomeNotificationContent.put("email", user.getEmail());
        welcomeNotificationContent.put("supportEmail", notificationSupportEmail);
        welcomeNotificationContent.put("otp", otpCode);

        //Creating actual welcome notification payload.
        welcomeNotificationMsg.put("timestamp", System.currentTimeMillis());
        welcomeNotificationMsg.put("content", welcomeNotificationContent);

        redisService.publishOTPNotification(welcomeNotificationMsg);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Object>> resendOtp(@Valid @RequestBody ResendOTPRequest resendOTPRequest) throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        Map<String, Object> welcomeNotificationMsg = new HashMap<>();
        Map<String, String> welcomeNotificationContent = new HashMap<>();
        User user = userRepository.findByUsername(resendOTPRequest.getUsername()).orElseThrow(() -> new GenericException(ApiResponse.error("USER_NOT_FOUND", "Username or Password is wrong.")));
        String otpCode = "123456";

        if (!defaultOtpEnabled) {
            otpCode = String.format("%06d", new Random().nextInt(100000, 999999));
        }

        otpStore.put(resendOTPRequest.getUsername(), otpCode);
        data.put("message", "OTP sent successfully");

        //Creating content object for welcome notification
        welcomeNotificationContent.put("userName", user.getUsername());
        welcomeNotificationContent.put("name", user.getFullname());
        welcomeNotificationContent.put("subject", "Request for new OTP reg.");
        welcomeNotificationContent.put("email", user.getEmail());
        welcomeNotificationContent.put("supportEmail", notificationSupportEmail);
        welcomeNotificationContent.put("otp", otpCode);

        //Creating actual welcome notification payload.
        welcomeNotificationMsg.put("timestamp", System.currentTimeMillis());
        welcomeNotificationMsg.put("content", welcomeNotificationContent);

        logger.info("[OTP Verification] Generated OTP {} for user {}", otpCode, resendOTPRequest.getUsername());
        logger.info("[OTP Verification] Sending OTP email to {}", user.getEmail());

        redisService.publishOTPNotification(welcomeNotificationMsg);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyOtp(@Valid @RequestBody OtpRequest otpRequest) {
        String correctOtp = otpStore.get(otpRequest.getUsername());
        String verificationType = otpRequest.getVerificationType();
        ServiceReply serviceReply;

        System.out.println(correctOtp + " " + otpRequest.getOtpCode());

        if (correctOtp == null || !correctOtp.equals(otpRequest.getOtpCode())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("INVALID_OTP", "OTP is incorrect or expired."));
        }
        otpStore.remove(otpRequest.getUsername());

        JNotifierEnums verificationTypeEnum = JNotifierEnums.fromString(verificationType);

        switch (verificationTypeEnum) {
            case LOGIN:
                serviceReply = verifyService.login(otpRequest.getUsername());
                ResponseCookie refCookie = (ResponseCookie) serviceReply.getReply().get("refCookie");
                ResponseCookie accessCookie = (ResponseCookie) serviceReply.getReply().get("accessCookie");
                JwtResponse body = (JwtResponse) serviceReply.getReply().get("jwtResponse");

                return ResponseEntity.status(serviceReply.getHttpStatusCode())
                        .header(HttpHeaders.SET_COOKIE, refCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                        .body(ApiResponse.success(body));

            case EMAIL_VERIFY:
                serviceReply = verifyService.verifyEmail(otpRequest.getUsername());
                Object response = serviceReply.getReply();

                return ResponseEntity.status(serviceReply.getHttpStatusCode()).body(ApiResponse.success(response));

            default:
                return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_VERIFICATION_TYPE", "Invalid verification type."));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws JsonProcessingException {
        String correctCaptcha = captchaStore.get(signUpRequest.getCaptchaId());
        if (correctCaptcha == null || !correctCaptcha.equalsIgnoreCase(signUpRequest.getCaptcha())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("INVALID_CAPTCHA", "Captcha is incorrect or expired."));
        }
        captchaStore.remove(signUpRequest.getCaptchaId());

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("BAD_REQUEST", "Error: Email is already in use!"));
        }

        String requestedRole = signUpRequest.getRole();
        if (requestedRole == null || requestedRole.trim().isEmpty()) {
            requestedRole = "user";
        }

        Role userRole;
        if (requestedRole.equalsIgnoreCase("admin")) {
            // Admin role registration is protected and can only be done by SUPERADMIN
            Authentication callerAuth = SecurityContextHolder.getContext().getAuthentication();
            if (callerAuth == null ||
                    !callerAuth.isAuthenticated() ||
                    callerAuth instanceof AnonymousAuthenticationToken ||
                    callerAuth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("FORBIDDEN", "Error: Only SUPERADMIN accounts can register new ADMIN users."));
            }
            userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: ADMIN role not initialized in database."));
        } else if (requestedRole.equalsIgnoreCase("superadmin")) {
            userRole = roleRepository.findByName(ERole.ROLE_SUPERADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: SUPERADMIN role not initialized in database."));
        } else {
            userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: USER role not initialized in database."));
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

        Map<String, String> reply = new HashMap<>();
        Map<String, Object> welcomeNotificationMsg = new HashMap<>();
        Map<String, String> welcomeNotificationContent = new HashMap<>();
        String otpCode = "123456";

        if (!defaultOtpEnabled) {
            otpCode = String.format("%06d", new Random().nextInt(100000, 999999));
        }
        otpStore.put(generatedUsername, otpCode);

        //Creating content object for welcome notification
        welcomeNotificationContent.put("userName", user.getUsername());
        welcomeNotificationContent.put("name", user.getFullname());
        welcomeNotificationContent.put("subject", "Verification of newly created account reg.");
        welcomeNotificationContent.put("email", user.getEmail());
        welcomeNotificationContent.put("supportEmail", notificationSupportEmail);
        welcomeNotificationContent.put("gender", user.getGender());
        welcomeNotificationContent.put("category", user.getCategory());
        welcomeNotificationContent.put("dob", user.getDob().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        welcomeNotificationContent.put("otp", otpCode);

        //Creating actual welcome notification payload.
        welcomeNotificationMsg.put("timestamp", System.currentTimeMillis());
        welcomeNotificationMsg.put("content", welcomeNotificationContent);

        reply.put("message", "User successfully registered!");
        reply.put("username", generatedUsername);

        redisService.publishWelcomeNotification(welcomeNotificationMsg);
        return ResponseEntity.ok(ApiResponse
                .success(reply));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshtoken(
            @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
            @Valid @RequestBody(required = false) TokenRefreshRequest request) {

        String requestRefreshToken = cookieRefreshToken;
        if (requestRefreshToken == null || requestRefreshToken.trim().isEmpty()) {
            if (request != null) {
                requestRefreshToken = request.getRefreshToken();
            }
        }

        if (requestRefreshToken == null || requestRefreshToken.trim().isEmpty()) {
            throw new TokenRefreshException("", "Refresh token is missing from cookies and request body!");
        }

        String finalToken = requestRefreshToken;
        TokenRefreshResponse tokenRefreshResponse = refreshTokenService.findByToken(finalToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return new TokenRefreshResponse(token, finalToken);
                })
                .orElseThrow(() -> new TokenRefreshException(finalToken,
                        "Refresh token is not in database!"));

        ResponseCookie refCookie = ResponseCookie.from("refreshToken", finalToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenDurationMs / 1000)
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokenRefreshResponse.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenDurationMs / 1000)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(ApiResponse.success(tokenRefreshResponse));
    }

    // --- Helper Methods ---

    private String generateRandomText() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();

        while (sb.length() < 6) {
            int index = (int) (rnd.nextFloat() * chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    private String generateCaptchaImage(String captchaCode) {
        try {
            int width = 130;
            int height = 40;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Draw background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // Draw noisy lines
            Random rnd = new Random();
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 5; i++) {
                g2d.drawLine(rnd.nextInt(width), rnd.nextInt(height), rnd.nextInt(width), rnd.nextInt(height));
            }

            // Draw text
            g2d.setColor(new Color(33, 150, 243));
            g2d.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
            g2d.drawString(captchaCode, 15, 28);

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            return "data:image/png;base64," + base64Image;
        } catch (IOException e) {
            throw new RuntimeException("Error generating captcha image", e);
        }
    }
}
