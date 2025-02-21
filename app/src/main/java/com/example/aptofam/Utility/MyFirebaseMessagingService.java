package com.example.aptofam.Utility;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.util.Log;
//Неиспользованное тк не BlazePlan
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d("FCM", "Title: " + title);
            Log.d("FCM", "Body: " + body);

            showNotification(title, body);
        }
    }

    private void showNotification(String title, String message) {

    }
}
