package com.jnotifier.repository;

import com.jnotifier.entity.Views;
import com.jnotifier.helpers.query.ViewsQueries;
import com.jnotifier.payload.pojo.PageViewsPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViewsRepository extends JpaRepository<Views, Integer> {
    Optional<Views> findByIpAddress(String ipAddress);

    @Query(value = ViewsQueries.GET_PAGE_VIEWS, nativeQuery = true)
    Optional<PageViewsPojo> findPageViews(@Param("visitedPage") String visitedPage);
}
