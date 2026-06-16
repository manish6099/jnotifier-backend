package com.jnotifier.controllers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
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
import com.jnotifier.payload.request.LoginRequest;
import com.jnotifier.payload.request.SignupRequest;
import com.jnotifier.payload.request.TokenRefreshRequest;
import com.jnotifier.payload.request.OtpRequest;
import com.jnotifier.payload.response.JwtResponse;
import com.jnotifier.payload.response.MessageResponse;
import com.jnotifier.payload.response.TokenRefreshResponse;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.repository.RoleRepository;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.security.jwt.JwtUtils;
import com.jnotifier.services.RefreshTokenService;
import com.jnotifier.exception.GenericException;
import com.jnotifier.exception.TokenRefreshException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  // Stores captchaId -> captchaCode
  private static final Map<String, String> captchaStore = new ConcurrentHashMap<>();

  // Stores username -> otpCode
  private static final Map<String, String> otpStore = new ConcurrentHashMap<>();

  @Value("${jnotifier.app.default-otp-enabled:true}")
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

  @GetMapping("/captcha")
  public ResponseEntity<ApiResponse<Map<String, String>>> getCaptcha() {
    String captchaId = UUID.randomUUID().toString();
    String captchaCode = generateRandomText();
    captchaStore.put(captchaId, captchaCode);

    logger.info("Captcha Code :: {}", captchaCode);

    String captchaImageBase64 = generateCaptchaImage(captchaCode);

    Map<String, String> response = new HashMap<>();
    response.put("captchaId", captchaId);
    response.put("captchaImage", captchaImageBase64);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/signin")
  public ResponseEntity<ApiResponse<Map<String, String>>> authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest) {
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
    String otpCode;
    if (defaultOtpEnabled) {
      otpCode = "123456";
    } else {
      otpCode = String.format("%06d", new Random().nextInt(999999));
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

    return ResponseEntity.ok(ApiResponse.success(data));
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<ApiResponse<JwtResponse>> verifyOtp(@Valid @RequestBody OtpRequest otpRequest) {
    String correctOtp = otpStore.get(otpRequest.getUsername());
    if (correctOtp == null || !correctOtp.equals(otpRequest.getOtpCode())) {
      return ResponseEntity
          .badRequest()
          .body(ApiResponse.error("INVALID_OTP", "OTP is incorrect or expired."));
    }
    otpStore.remove(otpRequest.getUsername());

    User user = userRepository.findByUsername(otpRequest.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found: " + otpRequest.getUsername()));

    String jwt = jwtUtils.generateTokenFromUsername(user.getUsername());
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

    List<String> roles = List.of(user.getRole().getName().name());

    JwtResponse jwtResponse = new JwtResponse(jwt,
        refreshToken.getToken(),
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        roles);

    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(refreshTokenDurationMs / 1000)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(ApiResponse.success(jwtResponse));
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<MessageResponse>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

    String correctCaptcha = captchaStore.get(signUpRequest.getCaptchaId());
    if (correctCaptcha == null || !correctCaptcha.equalsIgnoreCase(signUpRequest.getCaptchaValue())) {
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
    String cleanName = signUpRequest.getFullname().toLowerCase().replaceAll("[^a-zA-Z]", "");
    if (cleanName.isEmpty()) {
      cleanName = "user";
    }
    String generatedUsername = cleanName + "_" + System.currentTimeMillis();

    // Create new user's account
    User user = new User(
        signUpRequest.getFullname(),
        signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()),
        signUpRequest.getDob(),
        signUpRequest.getGender(),
        signUpRequest.getMobile());
    user.setUsername(generatedUsername);
    user.setRole(userRole);
    userRepository.save(user);

    return ResponseEntity.ok(ApiResponse
        .success(new MessageResponse("User registered successfully with generated username: " + generatedUsername)));
  }

  @PostMapping("/refreshtoken")
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

    ResponseCookie cookie = ResponseCookie.from("refreshToken", finalToken)
        .httpOnly(true)
        .secure(true)
        .path("/api/v1")
        .maxAge(refreshTokenDurationMs / 1000)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
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
