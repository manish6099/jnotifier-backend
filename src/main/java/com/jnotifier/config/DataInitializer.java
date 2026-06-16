package com.jnotifier.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.jnotifier.entity.ERole;
import com.jnotifier.entity.Role;
import com.jnotifier.repository.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

  @Autowired
  private RoleRepository roleRepository;

  @Override
  public void run(String... args) throws Exception {
    for (ERole erole : ERole.values()) {
      if (!roleRepository.findByName(erole).isPresent()) {
        roleRepository.save(new Role(erole));
      }
    }
  }
}
