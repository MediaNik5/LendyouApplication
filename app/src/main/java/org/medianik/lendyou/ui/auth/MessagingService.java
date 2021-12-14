package org.medianik.lendyou.ui.auth;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.medianik.lendyou.model.Repos;
import org.medianik.lendyou.model.debt.DebtInfo;

import java.util.Map;
import java.util.Objects;

public class MessagingService extends FirebaseMessagingService {
    private static final Gson gson = new Gson();
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("Lendyou", "FIREBASE TOKEN: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("Lendyou", "From: " + remoteMessage.getFrom());

        final var messageInfo = MessageInfo.of(remoteMessage.getData());
        if (messageInfo.type == MessageInfo.Type.NewDebt) {
            final String debtInfo = messageInfo.contents;
            Repos.getInstance().addPendingDebt(DebtInfo.of(debtInfo));
        } else if (messageInfo.type == MessageInfo.Type.DeclineDebt) {
            final String debtInfo = messageInfo.contents;
            Repos.getInstance().declineDebt(DebtInfo.of(debtInfo));
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            Log.d("Lendyou", "Message notification body: " + notification.getBody());
        }
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

        @NonNull
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
            Noop,
            NewDebt,
            DeclineDebt,
        }
    }
}
