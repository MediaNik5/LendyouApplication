package org.medianik.lendyou.ui.auth;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.medianik.lendyou.ui.MainActivity;

import java.util.Map;
import java.util.Objects;

public class MessagingService extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        final var intent = new Intent(MainActivity.firebaseKey);
        intent.putExtra(MainActivity.firebaseKey, token);
        broadcaster.sendBroadcast(intent);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("Lendyou", "From: " + remoteMessage.getFrom());

        if (!remoteMessage.getData().isEmpty()) {
            Log.d("Lendyou", "Message data payload: " + remoteMessage.getData());
        }

        final var messageInfo = MessageInfo.of(remoteMessage.getData());
        if (messageInfo.type == MessageInfo.Type.Auth && messageInfo.contents.equals("success")) {
            broadcaster.sendBroadcast(new Intent(MainActivity.successfulAuth));
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null)
            Log.d("Lendyou", "Message notification body: " + notification.getBody());
    }

    public static class MessageInfo {
        private final Type type;
        private final String contents;

        private MessageInfo(Type type, String contents) {
            this.type = type;
            this.contents = contents;
        }

        public static MessageInfo of(Map<String, String> map) {
            final var type = Objects.requireNonNull(map.get("type"), "type cannot be empty");
            final var contents = Objects.requireNonNull(map.get("contents"), "contents cannot be empty");
            return new MessageInfo(Type.valueOf(type), contents);
        }

        public Type getType() {
            return type;
        }

        public String getContents() {
            return contents;
        }

        @Override
        public String toString() {
            return "MessageInfo{" +
                    "type=" + type +
                    ", contents='" + contents + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MessageInfo)) return false;
            MessageInfo that = (MessageInfo) o;
            return type == that.type && Objects.equals(contents, that.contents);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, contents);
        }

        public enum Type {
            Auth
        }
    }
}
