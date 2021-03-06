package org.medianik.lendyou.model

import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.bank.Payment
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtId
import org.medianik.lendyou.model.debt.DebtInfo
import org.medianik.lendyou.model.debt.SortingOrder
import org.medianik.lendyou.model.person.Debtor
import org.medianik.lendyou.model.person.Lender
import org.medianik.lendyou.model.person.Person
import org.medianik.lendyou.model.person.PersonId
import java.math.BigDecimal


/**
 * Repo containing info about this particular Lender&Debtor
 */
interface Repo {
    fun thisPerson(): PersonId

    fun getDebt(debtId: DebtId, forceUpdate: Boolean = false): Debt?
    fun getDebts(): List<Debt>

    /**
     * Returns all debts that [lender] holds on this user as [Debtor]
     */
    fun getDebts(lender: Lender): Collection<Debt>

    /**
     * Returns all debts that [debtor] is being hold by this user as [Lender]
     */
    fun getDebts(debtor: Debtor): Collection<Debt>

    fun addPerson(person: Person): Boolean
    fun addPerson(email: String): Boolean
    fun getDebtors(): List<Debtor>
    fun getLenders(): List<Lender>

    fun createDebt(
        debtInfo: DebtInfo,
        from: Account,
        to: Account,
    ): Debt

    fun addDebtFromServer(debt: Debt): Boolean

    fun declineDebtAsLender(debtInfo: DebtInfo)
    fun declineDebtFromServer(debtInfo: DebtInfo)

    fun payDebt(debt: Debt, sum: BigDecimal = debt.debtInfo.sum): Boolean

    /**
     * If succeeds marks [sum] of money paid on [lender] that is holding debt on this user as [Debtor]
     */
    fun pay(lender: Lender, sum: BigDecimal): Boolean

    /**
     * If succeeds marks [sum] of money paid on [debtor] that is being hold debt on this user as [Lender]
     */
    fun pay(debtor: Debtor, sum: BigDecimal): Boolean

//    fun getPendingOperations(): Collection<Operation<*>>
//    fun getCompletedOperations(): Collection<Operation<*>>

    fun addPendingDebtFromServer(debtInfo: DebtInfo)
    fun getPendingDebts(sortingOrder: SortingOrder? = null): List<DebtInfo>

    fun getLender(lenderId: PersonId): Lender
    fun getDebtor(debtorId: PersonId): Debtor
    fun subscribeToChanges(function: () -> Unit)
    fun askForDebt(debtInfo: DebtInfo)
    fun addPayment(payment: Payment)
    fun addPaymentFromServer(payment: Payment): Boolean
}

