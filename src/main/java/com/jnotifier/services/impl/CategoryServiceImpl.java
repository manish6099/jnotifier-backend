package com.jnotifier.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jnotifier.entity.Application;
import com.jnotifier.entity.Category;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.CategoryRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.repository.ApplicationRepository;
import com.jnotifier.repository.CategoryRepository;
import com.jnotifier.services.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ApplicationRepository applicationRepository;

  @Override
  public Category save(CategoryRequest request) {
    Application application = applicationRepository.findById(request.getApplicationId())
        .orElseThrow(() -> new GenericException(ApiResponse.error("RESOURCE_NOT_FOUND", "Application not found with id: " + request.getApplicationId())));

    Category category = new Category();
    category.setCategoryName(request.getCategoryName());
    category.setCategoryDesc(request.getCategoryDesc());
    category.setApplication(application);
    category.setOrderId(request.getOrderId());
    if (request.getStatus() != null) {
      category.setStatus(request.getStatus());
    } else {
      category.setStatus(true);
    }
    return categoryRepository.save(category);
  }

  @Override
  public Category update(Long id, CategoryRequest request) {
    Category category = findById(id);
    
    Application application = applicationRepository.findById(request.getApplicationId())
        .orElseThrow(() -> new GenericException(ApiResponse.error("RESOURCE_NOT_FOUND", "Application not found with id: " + request.getApplicationId())));

    category.setCategoryName(request.getCategoryName());
    category.setCategoryDesc(request.getCategoryDesc());
    category.setApplication(application);
    category.setOrderId(request.getOrderId());
    if (request.getStatus() != null) {
      category.setStatus(request.getStatus());
    }
    return categoryRepository.save(category);
  }

  @Override
  public Category updateStatus(Long id, Boolean status) {
    Category category = findById(id);
    category.setStatus(status);
    return categoryRepository.save(category);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Category findById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new GenericException(ApiResponse.error("RESOURCE_NOT_FOUND", "Category not found with id: " + id)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Category> findByApplicationId(Long applicationId) {
    if (!applicationRepository.existsById(applicationId)) {
      throw new GenericException(ApiResponse.error("RESOURCE_NOT_FOUND", "Application not found with id: " + applicationId));
    }
    return categoryRepository.findByApplicationId(applicationId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Category> findActiveByApplicationId(Long applicationId) {
    if (!applicationRepository.existsById(applicationId)) {
      throw new GenericException(ApiResponse.error("RESOURCE_NOT_FOUND", "Application not found with id: " + applicationId));
    }
    return categoryRepository.findByApplicationIdAndStatusTrue(applicationId);
  }
}
