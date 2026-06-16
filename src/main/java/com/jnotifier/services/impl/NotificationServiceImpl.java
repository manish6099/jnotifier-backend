package com.jnotifier.services.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.jnotifier.entity.Notification;
import com.jnotifier.repository.NotificationRepository;
import com.jnotifier.services.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

  private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

  @Autowired
  private NotificationRepository notificationRepository;

  @Override
  public Notification save(Notification notification) {
    return notificationRepository.save(notification);
  }

  @Override
  @Retryable(
      retryFor = Exception.class,
      maxAttemptsExpression = "${jnotifier.notification.max-attempts:3}",
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  public Notification sendNotification(Notification notification) {
    logger.info("Attempting to deliver notification: {}", notification.getTitle());
    
    // Simulate flaky notification delivery channel
    if (Math.random() < 0.6) {
      logger.warn("Delivery channel transient failure. Raising exception to trigger retry.");
      throw new RuntimeException("Transient delivery network issue.");
    }
    
    notification.setDeliveryStatus("DELIVERED");
    logger.info("Notification delivered successfully!");
    return notificationRepository.save(notification);
  }

  @Recover
  public Notification recover(Exception e, Notification notification) {
    logger.error("All delivery retry attempts exhausted. Marking notification status as FAILED. Error: {}", e.getMessage());
    notification.setDeliveryStatus("FAILED");
    // Saving the failed state so the user isn't shown an error but the notification is tracked as FAILED
    return notificationRepository.save(notification);
  }

  @Override
  public List<Notification> findAll() {
    return notificationRepository.findAll();
  }

  @Override
  public List<Notification> findByRecipient(String recipient) {
    return notificationRepository.findByRecipient(recipient);
  }

  @Override
  public boolean existsById(Long id) {
    return notificationRepository.existsById(id);
  }

  @Override
  public void deleteById(Long id) {
    notificationRepository.deleteById(id);
  }
}
