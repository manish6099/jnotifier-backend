package com.jnotifier.services;

import java.util.List;
import com.jnotifier.entity.Category;
import com.jnotifier.payload.request.CategoryRequest;

public interface CategoryService {
  Category save(CategoryRequest request);
  Category update(Long id, CategoryRequest request);
  Category updateStatus(Long id, Boolean status);
  List<Category> findAll();
  Category findById(Long id);
  List<Category> findByApplicationId(Long applicationId);
  List<Category> findActiveByApplicationId(Long applicationId);
}
