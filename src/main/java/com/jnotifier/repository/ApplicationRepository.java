package com.jnotifier.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jnotifier.entity.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
  List<Application> findByStatusTrue();

  Page<Application> findByStatusTrue(Pageable pageable);

  Page<Application> findByCreatedBy(String createdBy, Pageable pageable);
}
