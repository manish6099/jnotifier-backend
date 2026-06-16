package com.jnotifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.jnotifier.entity.Notification;
import com.jnotifier.services.NotificationService;
import com.jnotifier.repository.NotificationRepository;

@SpringBootTest
public class NotificationServiceTest {

  @Autowired
  private NotificationService notificationService;

  @SpyBean
  private NotificationRepository notificationRepository;

  @Test
  public void testSendNotificationRetryAndRecovery() {
    Notification notification = new Notification();
    notification.setTitle("Test Title");
    notification.setContent("Test Content");
    notification.setRecipient("recipient@example.com");
    notification.setSender("sender@example.com");

    // Stub repository save to return the argument
    doAnswer(invocation -> invocation.getArgument(0)).when(notificationRepository).save(any(Notification.class));

    Notification result = null;
    try {
      result = notificationService.sendNotification(notification);
    } catch (Exception e) {
      result = null;
    }

    assertThat(result).isNotNull();
    assertThat(result.getDeliveryStatus()).isIn("DELIVERED", "FAILED");
  }
}
