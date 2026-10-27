package com.college.eventmanagement.service;

import com.college.eventmanagement.model.Notification;
import com.college.eventmanagement.model.User;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(User user, String message, String type);
    List<Notification> getUserNotifications(User user);
    void markAsRead(Long notificationId);
}
