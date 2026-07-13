package com.jnotifier.repository;

import com.jnotifier.entity.Media;
import com.jnotifier.helpers.query.MediaQueries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    @Query(MediaQueries.GET_ALL_ACTIVE_MEDIA)
    Page<Media> findAllActiveMedia(Pageable pageable);
}
