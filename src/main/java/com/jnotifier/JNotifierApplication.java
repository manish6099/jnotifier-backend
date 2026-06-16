package com.jnotifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JNotifierApplication {

  public static void main(String[] args) {
    SpringApplication.run(JNotifierApplication.class, args);
  }

}
