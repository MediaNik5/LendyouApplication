package org.medianik.lendyou.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.medianik.lendyou.model.Repos;
import org.medianik.lendyou.model.bank.Payment;
import org.medianik.lendyou.model.debt.Debt;
import org.medianik.lendyou.model.debt.DebtInfo;
import org.medianik.lendyou.model.person.Person;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

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
        messageInfo.onReceive();

        final var notification = remoteMessage.getNotification();
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
            final var type = map.get("type");
            final var contents = map.get("contents");
            return new MessageInfo(type == null ? null : Type.valueOf(type), contents);
        }

        public void onReceive() {
            if (type == null)
                return;
            type.onReceive(contents);
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
            Noop((__) -> {
            }),
            NewDebt(debt -> Repos.getInstance().addDebtFromServer(Debt.of(debt))),
            NewDebtRequest(debtInfo -> Repos.getInstance().addPendingDebtFromServer(DebtInfo.of(debtInfo))),
            DeclineDebt(debtInfo -> Repos.getInstance().declineDebtFromServer(DebtInfo.of(debtInfo))),
            NewPerson(person -> Repos.getInstance().addPerson(Person.of(person))),
            NewPayment(payment -> Repos.getInstance().addPaymentFromServer(Payment.of(payment)));

            private final Consumer<String> onReceive;

            Type(Consumer<String> onReceive) {
                this.onReceive = onReceive;
            }

            public void onReceive(String contents) {
                onReceive.accept(contents);
            }
        }
    }
}
