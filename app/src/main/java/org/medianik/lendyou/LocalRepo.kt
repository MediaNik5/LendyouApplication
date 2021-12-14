package org.medianik.lendyou

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import org.medianik.lendyou.model.Repo
import org.medianik.lendyou.model.SnackbarManager
import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtId
import org.medianik.lendyou.model.debt.DebtInfo
import org.medianik.lendyou.model.debt.SortingOrder
import org.medianik.lendyou.model.person.*
import org.medianik.lendyou.util.ServerDatabase
import org.medianik.lendyou.util.sql.LendyouDatabase
import java.math.BigDecimal
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList

class LocalRepo(
    private val database: LendyouDatabase,
    private val serverDatabase: ServerDatabase,
    private val isLender: Boolean,
    auth: FirebaseAuth,
) : Repo {

    override fun thisPerson() = thisId

    private val thisId = PersonId(auth.currentUser!!.uid)
    private val pendingDebts: CopyOnWriteArrayList<DebtInfo> = CopyOnWriteArrayList()
    private val debts: MutableMap<DebtId, Debt> = ConcurrentHashMap()
    private val persons: MutableMap<PersonId, Person> = ConcurrentHashMap()
    private val subscribers: Deque<() -> Unit> = ConcurrentLinkedDeque()

    init {
        database.addPerson(
            Person(
                thisId,
                auth.currentUser!!.displayName!!,
                auth.currentUser!!.email!!,
                Passport(
                    "400-100",
                    auth.currentUser!!.displayName!!,
                    "Nikitin",
                    "Nikitin"
                )
            )
        )
        for (person in database.allPersons()) {
            persons[person.id] = person
        }
        for (debt in database.allDebts()) {
            debts.addDebt(debt)
        }
        pendingDebts.addAll(database.pendingDebts())
    }

    override fun getDebt(debtId: DebtId, forceUpdate: Boolean): Debt? {
        return debts[debtId]
    }

    override fun getDebts(): List<Debt> {
        return ArrayList(debts.values)
    }

    override fun getDebts(lender: Lender): Collection<Debt> {
        return persons.values.first { it.id == lender.id }.lender.debts
    }

    override fun getDebts(debtor: Debtor): Collection<Debt> {
        return persons.values.first { it.id == debtor.id }.debtor.debts
    }

    override fun getDebtors(): List<Debtor> {
        return persons.values.map { it.debtor }
    }

    override fun getLenders(): List<Lender> {
        return persons.values.map { it.lender }
    }

    override fun createDebt(
        debtInfo: DebtInfo,
        from: Account,
        to: Account,
        period: Duration
    ): Debt {
        if (isLender) {
            return createDebtAsLender(debtInfo, from, to, period).also { subscribers.update() }
        }
        throw IllegalArgumentException("You have to be lender to create debts")
    }

    override fun declineDebt(debtInfo: DebtInfo) {
        if (isLender) {
            serverDatabase.declineDebt(debtInfo).addOnSuccessListener {
                database.removePendingDebt(debtInfo)
                pendingDebts.remove(debtInfo)
                subscribers.update()
                SnackbarManager.showMessage(R.string.pending_debt_declined)
            }.addOnFailureListener { exception ->
                SnackbarManager.showMessage(R.string.pending_debt_not_declined)
                Log.e(
                    "Lendyou",
                    "Exception happened while updating database(declining debt)",
                    exception
                )
            }
        } else {
            pendingDebts.remove(debtInfo)
            subscribers.update()
            SnackbarManager.showMessage(R.string.pending_debt_declined)
        }
        throw IllegalStateException("Cannot delete pending debt as debtor")
    }

    // Unchecked
    private fun createDebtAsLender(
        debtInfo: DebtInfo,
        from: Account,
        to: Account,
        period: Duration
    ): Debt {
        if (!pendingDebts.contains(debtInfo))
            throw IllegalStateException("DebtInfo $debtInfo is not in pending debts, cannot create debt")

        val newDebt = Debt(
            debtInfo,
            from,
            to,
            period
        )
        serverDatabase.addDebt(newDebt).addOnSuccessListener {
            debts[newDebt.id] = newDebt
            database.addDebt(newDebt)
            pendingDebts.remove(debtInfo)
            subscribers.update()
            SnackbarManager.showMessage(R.string.debt_created)
        }.addOnFailureListener { exception ->
            SnackbarManager.showMessage(R.string.debt_not_created)
            Log.e("Lendyou", "Exception happened while updating database(creating debt)", exception)
        }
        return newDebt
    }

    override fun payDebt(debt: Debt, sum: BigDecimal): Boolean {
        if(thisId == debt.debtInfo.debtorId){
            return payDebtAsDebtor(debt, sum).also { subscribers.update() }
        }
        if(thisId == debt.debtInfo.lenderId){
            return payDebtAsLender(debt, sum).also { subscribers.update() }
        }
        throw IllegalArgumentException("Debt must have this user either as debtor or lender to be paid")
    }

    private fun payDebtAsDebtor(debt: Debt, sum: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }

    private fun payDebtAsLender(debt: Debt, sum: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }


    override fun pay(lender: Lender, sum: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }

    override fun pay(debtor: Debtor, sum: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }

    override fun addPendingDebt(debtInfo: DebtInfo) {
        pendingDebts.add(debtInfo)
        database.addPendingDebt(debtInfo)
        subscribers.update()
        Log.d("Lendyou", "New debtInfo came here: $debtInfo")
    }

    override fun getPendingDebts(sortingOrder: SortingOrder?): List<DebtInfo> {
        when (sortingOrder) {
            SortingOrder.DateTime -> pendingDebts.sortBy { it.dateTime }
            SortingOrder.Sum -> pendingDebts.sortBy { it.sum }
            SortingOrder.Lender -> pendingDebts.sortBy { it.lenderId }
            SortingOrder.Debtor -> pendingDebts.sortBy { it.debtorId }
        }
        return if (isLender) {
            pendingDebts.filter { it.lenderId == thisId }
        } else
            pendingDebts.filter { it.debtorId == thisId }
    }

    override fun getLender(lenderId: PersonId): Lender {
        return persons[lenderId]?.lender
            ?: throw NoSuchElementException("Person with $lenderId id is not found")
    }

    override fun getDebtor(debtorId: PersonId): Debtor {
        return persons[debtorId]?.debtor
            ?: throw NoSuchElementException("Person with $debtorId id is not found")
    }

    override fun subscribeToChanges(function: () -> Unit) {
        subscribers.add(function)
    }

    override fun askForDebt(debtInfo: DebtInfo) {
        serverDatabase.askForDebt(debtInfo).addOnSuccessListener {
            SnackbarManager.showMessage(R.string.request_debt_success_to_server)
            subscribers.update()
        }.addOnFailureListener { exception ->
            SnackbarManager.showMessage(R.string.request_debt_failure_to_server)
            Log.e("Lendyou", "Exception happened while updating database", exception)
        }
    }


    private fun isDebtAdded(id: DebtId): Boolean =
        debts.contains(id)

    private fun Deque<() -> Unit>.update() {
        for (func in this)
            func()
    }

    private fun MutableMap<DebtId, Debt>.addDebt(debt: Debt): Debt {
        put(debt.id, debt)
        return debt
    }
}

