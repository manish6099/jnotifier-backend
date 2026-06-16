package com.jnotifier.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jnotifier.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findByApplicationId(Long applicationId);
  List<Category> findByApplicationIdAndStatusTrue(Long applicationId);
}
