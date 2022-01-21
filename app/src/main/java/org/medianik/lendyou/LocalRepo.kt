package org.medianik.lendyou

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import org.medianik.lendyou.model.Repo
import org.medianik.lendyou.model.SnackbarManager.showMessage
import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.bank.Payment
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtId
import org.medianik.lendyou.model.debt.DebtInfo
import org.medianik.lendyou.model.debt.SortingOrder
import org.medianik.lendyou.model.person.*
import org.medianik.lendyou.util.ServerDatabase
import org.medianik.lendyou.util.sql.LendyouDatabase
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Local android implementation, that uses maps to store temp data and calls for sqliteDatabase for storing permanent data of Repo
 */
class LocalRepo(
    private val database: LendyouDatabase,
    private val serverDatabase: ServerDatabase,
    private val isLender: Boolean,
    auth: FirebaseAuth,
) : Repo {

    override fun thisPerson() = thisId

    private val thisId = PersonId(auth.currentUser!!.uid)
    private val pendingDebts: CopyOnWriteArrayList<DebtInfo> by lazy {
        CopyOnWriteArrayList<DebtInfo>().also {
            it.addAll(database.allPendingDebts())
        }
    }
    private val debts: MutableMap<DebtId, Debt> by lazy {
        ConcurrentHashMap<DebtId, Debt>().also {
            database.allDebts().forEach { debt ->
                it.addDebt(debt)
            }
        }
    }
    private val persons: MutableMap<PersonId, Person> by lazy {
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
        ConcurrentHashMap<PersonId, Person>().also { map ->
            database.allPersons().forEach { person ->
                map[person.id] = person
            }
        }
    }
    private val subscribers: Deque<() -> Unit> = ConcurrentLinkedDeque()

    override fun getDebt(debtId: DebtId, forceUpdate: Boolean): Debt? {
        return debts[debtId]
    }

    override fun getDebts(): List<Debt> {
        return debts.values.filter { thisId == if (isLender) it.debtInfo.lenderId else it.debtInfo.debtorId }
    }

    override fun getDebts(lender: Lender): Collection<Debt> {
        return persons.values.first { it.id == lender.id }.lender.debts
    }

    override fun getDebts(debtor: Debtor): Collection<Debt> {
        return persons.values.first { it.id == debtor.id }.debtor.debts
    }

    override fun addPerson(person: Person): Boolean {
        val added = null == persons.putIfAbsent(person.id, person)
        if (added) {
            showMessage(R.string.add_new_person)
            database.addPerson(person)
            subscribers.update()
        }
        return added
    }

    override fun addPerson(email: String): Boolean {
        if (persons.all { it.value.email != email }) {
            serverDatabase.addPerson(email)
            return true
        }
        return false
    }

    override fun getDebtors(): List<Debtor> {
        return persons.values.filterNot { it.id == thisId }.map { it.debtor }
    }

    override fun getLenders(): List<Lender> {
        return persons.values.filterNot { it.id == thisId }.map { it.lender }
    }

    override fun createDebt(
        debtInfo: DebtInfo,
        from: Account,
        to: Account
    ): Debt {
        if (isLender) {
            return createDebtAsLender(debtInfo, from, to)
        }
        throw IllegalArgumentException("You have to be lender to create debts")
    }

    override fun addDebtFromServer(debt: Debt): Boolean {
        if (debts.addDebt(debt)) {
            database.addDebt(debt)
            pendingDebts.remove(debt.debtInfo)
            subscribers.update()
            return true
        }
        return false
    }

    override fun declineDebtAsLender(debtInfo: DebtInfo) {
        if (isLender) {
            serverDatabase.declineDebt(debtInfo).addOnSuccessListener { it: Void? ->
                showMessage(R.string.pending_debt_declined)
            }.addOnFailureListener { exception ->
                showMessage(R.string.pending_debt_not_declined)
                Log.e(
                    "Lendyou",
                    "Exception happened while updating database(declining debt)",
                    exception
                )
            }
            return
        }
        throw IllegalStateException("Cannot delete pending debt as debtor")
    }

    override fun declineDebtFromServer(debtInfo: DebtInfo) {
        if (pendingDebts.remove(debtInfo)) {
            database.removePendingDebt(debtInfo)
            subscribers.update()
            showMessage(R.string.pending_debt_declined)
        }
    }


    // Unchecked
    private fun createDebtAsLender(
        debtInfo: DebtInfo,
        from: Account,
        to: Account
    ): Debt {
        if (!pendingDebts.contains(debtInfo))
            throw IllegalStateException("DebtInfo $debtInfo is not in pending debts, cannot create debt")

        val newDebt = Debt(
            debtInfo,
            from,
            to,
        )
        serverDatabase.addDebt(newDebt).addOnSuccessListener { it: Void? ->
            showMessage(R.string.debt_sent_agreement)
        }.addOnFailureListener { exception ->
            showMessage(R.string.debt_not_created)
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

    override fun addPendingDebtFromServer(debtInfo: DebtInfo) {
        if (!pendingDebts.contains(debtInfo)) {
            pendingDebts.add(debtInfo)
            database.addPendingDebt(debtInfo)
            subscribers.update()
            Log.d("Lendyou", "New debtInfo came here: $debtInfo")
        }
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
        serverDatabase.askForDebt(debtInfo).addOnSuccessListener { it: Void? ->
            showMessage(R.string.request_debt_success_to_server)
        }.addOnFailureListener { exception ->
            showMessage(R.string.request_debt_failure_to_server)
            Log.e("Lendyou", "Exception happened while updating database", exception)
        }
    }

    override fun addPayment(payment: Payment) {
        serverDatabase.addPayment(payment)
            .addOnSuccessListener {
                showMessage(R.string.payment_added)
            }.addOnFailureListener { exception ->
                showMessage(R.string.payment_error)
                Log.e(
                    "Lendyou",
                    "Exception happened while updating database(adding payment)",
                    exception
                )
            }
    }

    override fun addPaymentFromServer(payment: Payment): Boolean {
        val added = debts[DebtId(payment.debtId)]!!.addPayment(payment)
        if (added) {
            database.addPayment(payment)
            subscribers.update()
            showMessage(R.string.payment_added)
        }
        return added
    }


    private fun isDebtAdded(id: DebtId): Boolean =
        debts.contains(id)

    private fun Deque<() -> Unit>.update() {
        for (func in this)
            func()
    }

    private fun MutableMap<DebtId, Debt>.addDebt(debt: Debt): Boolean {
        return put(debt.id, debt) == null
    }
}

