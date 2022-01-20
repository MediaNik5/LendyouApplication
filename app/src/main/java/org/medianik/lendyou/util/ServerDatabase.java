package org.medianik.lendyou.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.medianik.lendyou.model.bank.Payment;
import org.medianik.lendyou.model.debt.Debt;
import org.medianik.lendyou.model.debt.DebtInfo;

/**
 * Interface representing Server database somewhere in internet, that saves passed data.
 */
public interface ServerDatabase {

    /**
     * Sends request to server acceptance of debt with id newDebt.id
     * Has to be called after {@code askForDebt(DebtInfo)} returned successful task on newDebt.debtInfo
     * <p>
     * See {@link MessagingService} for incoming requests
     * </p>
     *
     * @return Task, representing process of sending debt to server
     * (successful task does not mean successful processing, it means that request was delivered)
     */
    @NonNull
    Task<Void> addDebt(@NonNull Debt newDebt);

    /**
     * Sends request to server, asking for debt(must be called with debtInfo.debtor
     * equal to {@code Repos.getInstance().thisPerson()})
     * <p>
     * See {@link MessagingService} for incoming requests
     * </p>
     *
     * @return Task, representing process of sending debtInfo to server
     * (successful task does not mean successful processing, it means that request was delivered)
     */
    @NonNull
    Task<Void> askForDebt(@NonNull DebtInfo debtInfo);

    /**
     * Sends request to server, asking for person having this email to be added to personlist
     * <p>
     * See {@link MessagingService} for incoming requests
     * </p>
     *
     * @param email correct email like example@google.com
     * @return Task, representing process of sending debtInfo to server
     * (successful task does not mean successful processing, it means that request was delivered)
     */
    @NonNull
    Task<Void> addPerson(String email);

    /**
     * Sends request to server, asking for declining unique debtInfo instance
     * <p>
     * See {@link MessagingService} for incoming requests
     * </p>
     *
     * @return Task, representing process of sending debtInfo to server
     * (successful task does not mean successful processing, it means that request was delivered)
     */
    @NonNull
    Task<Void> declineDebt(@NotNull DebtInfo debtInfo);

    /**
     * Sends request to server, asking for new payment to be added for specific debt
     * <p>
     * See {@link MessagingService} for incoming requests
     * </p>
     *
     * @return Task, representing process of sending debtInfo to server
     * (successful task does not mean successful processing, it means that request was delivered)
     */
    @NonNull
    Task<Void> addPayment(@NotNull Payment payment);
}
