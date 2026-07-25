package com.jnotifier.components;

import com.jnotifier.helpers.query.migrations.CreateSchemaQuery;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("jNotifierPostConstructComponent")
public class JNotifierPostConstructComponent {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(JNotifierPostConstructComponent.class);

    public JNotifierPostConstructComponent(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        jdbcTemplate.execute(CreateSchemaQuery.CREATE_MASTER_SCHEMA_QUERY);
        logger.info("ALL SCHEMA QUERIES HAVE BEEN EXECUTED SUCCESSFULLY.");
    }
}
