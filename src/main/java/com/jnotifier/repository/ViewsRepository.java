package com.jnotifier.repository;

import com.jnotifier.entity.Views;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViewsRepository extends JpaRepository<Views, Integer> {
    Optional<Views> findByIpAddress(String ipAddress);
}
