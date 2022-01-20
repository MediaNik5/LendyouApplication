package org.medianik.lendyou.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;
import org.medianik.lendyou.model.bank.Payment;
import org.medianik.lendyou.model.debt.Debt;
import org.medianik.lendyou.model.debt.DebtInfo;

import java.util.concurrent.ThreadLocalRandom;

public class MyFirebaseDatabase implements ServerDatabase {
    private final FirebaseDatabase underlyingDatabase;
    private final String uid;
    private String firebaseToken;

    public MyFirebaseDatabase(
            FirebaseDatabase underlyingDatabase,
            @NonNull String uid
    ) {
        this.underlyingDatabase = underlyingDatabase;
        this.uid = uid;
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(this::setFirebaseToken)
                .addOnFailureListener(exception -> Log.e("Lendyou", "Exception happened while obtaining firebase token", exception));
    }

    @Override
    public @NonNull
    Task<Void> addDebt(@NonNull Debt newDebt) {
        long id = newDebt.id;
        var reference = underlyingDatabase.getReference("debt/" + uid + '/' + id);
        return reference.setValue(newDebt.toString());
    }

    @Override
    public @NonNull
    Task<Void> askForDebt(@NonNull DebtInfo debtInfo) {
        long id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var reference = underlyingDatabase.getReference("newDebt/" + uid + '/' + id);
        return reference.setValue(debtInfo.toString());
    }

    @SuppressWarnings("UnusedReturnValue")
    private @NonNull
    Task<Void> setFirebaseToken(String token) {
        this.firebaseToken = token;
        var reference = underlyingDatabase.getReference("user/" + uid);
        return reference.setValue(token)
                .addOnFailureListener((exception) -> Log.e("Lendyou", "Exception happened while sending firebase token to server", exception))
                .addOnSuccessListener((__) -> Log.d("Lendyou", "Set firebase token: " + firebaseToken));
    }

    @Override
    public @NonNull
    Task<Void> addPerson(String email) {
        long id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var reference = underlyingDatabase.getReference("newPerson/" + uid + '/' + id);
        return reference.setValue(email);
    }

    @Override
    public @NonNull
    Task<Void> declineDebt(@NotNull DebtInfo debtInfo) {
        long id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var reference = underlyingDatabase.getReference("declineDebt/" + uid + '/' + id);
        return reference.setValue(debtInfo.toString());
    }

    @NonNull
    @Override
    public Task<Void> addPayment(@NotNull Payment payment) {
        long id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var reference = underlyingDatabase.getReference("payment/" + uid + '/' + id);
        return reference.setValue(payment.toString());
    }
}
