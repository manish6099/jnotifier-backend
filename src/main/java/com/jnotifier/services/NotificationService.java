package com.jnotifier.services;

import java.util.List;
import com.jnotifier.entity.Notification;

public interface NotificationService {
  Notification save(Notification notification);
  Notification sendNotification(Notification notification);
  List<Notification> findAll();
  List<Notification> findByRecipient(String recipient);
  boolean existsById(Long id);
  void deleteById(Long id);
}
