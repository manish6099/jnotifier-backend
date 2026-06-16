package com.jnotifier.services.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.services.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {

  private final Path fileStorageLocation;

  public FileStorageServiceImpl() {
    this.fileStorageLocation = Paths.get("uploads")
        .toAbsolutePath().normalize();

    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  @Override
  public String storeFile(MultipartFile file) {
    // 1. Validation - check if empty
    if (file.isEmpty()) {
      throw new GenericException(ApiResponse.error("INVALID_FILE", "File is empty."));
    }

    // 2. Validation - check file size <= 10MB (10 * 1024 * 1024 bytes)
    if (file.getSize() > 10 * 1024 * 1024) {
      throw new GenericException(ApiResponse.error("INVALID_FILE", "File size exceeds the maximum limit of 10 MB."));
    }

    // 3. Validation - check file extension is .pdf
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
      throw new GenericException(ApiResponse.error("INVALID_FILE", "Original filename is invalid."));
    }
    
    String cleanFilename = StringUtils.cleanPath(originalFilename);
    if (!cleanFilename.toLowerCase().endsWith(".pdf")) {
      throw new GenericException(ApiResponse.error("INVALID_FILE", "Only PDF files are allowed."));
    }

    // 4. Validation - check MIME type is application/pdf
    String contentType = file.getContentType();
    if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
      throw new GenericException(ApiResponse.error("INVALID_FILE", "Only PDF content type is allowed."));
    }

    // 5. Validation - check Magic Bytes of PDF (%PDF -> 0x25 0x50 0x44 0x46)
    try {
      byte[] bytes = file.getBytes();
      if (bytes.length < 4 || bytes[0] != 0x25 || bytes[1] != 0x50 || bytes[2] != 0x44 || bytes[3] != 0x46) {
        throw new GenericException(ApiResponse.error("INVALID_FILE", "Malicious file upload blocked: Invalid PDF file signature."));
      }
    } catch (IOException e) {
      throw new GenericException(ApiResponse.error("FILE_READ_ERROR", "Could not read file signatures."));
    }

    try {
      // 6. Generate Unique File Name
      String fileExtension = "";
      int extensionIndex = cleanFilename.lastIndexOf(".");
      if (extensionIndex >= 0) {
        fileExtension = cleanFilename.substring(extensionIndex);
      }
      
      String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

      // 7. Copy file to target location
      Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return uniqueFilename;
    } catch (IOException ex) {
      throw new GenericException(ApiResponse.error("FILE_SAVE_ERROR", "Could not store file. Please try again."));
    }
  }

  @Override
  public Resource loadFileAsResource(String fileName) {
    try {
      Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        return resource;
      } else {
        throw new GenericException(ApiResponse.error("FILE_NOT_FOUND", "File not found: " + fileName));
      }
    } catch (MalformedURLException ex) {
      throw new GenericException(ApiResponse.error("FILE_NOT_FOUND", "File not found due to path issue: " + fileName));
    }
  }
}
