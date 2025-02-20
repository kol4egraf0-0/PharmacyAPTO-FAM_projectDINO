package com.example.aptofam.Model;

public class NotificationModel {
    private String notificationId;
    private String message;
    private long timestamp;

    public NotificationModel() {
    }

    public NotificationModel(String notificationId, String message, long timestamp) {
        this.notificationId = notificationId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
