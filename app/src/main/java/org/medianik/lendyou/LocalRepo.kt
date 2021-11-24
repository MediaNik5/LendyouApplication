package org.medianik.lendyou

import org.medianik.lendyou.model.Repo
import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtId
import org.medianik.lendyou.model.debt.DebtInfo
import org.medianik.lendyou.model.person.*
import org.medianik.lendyou.util.sql.LendyouDatabase
import java.math.BigDecimal
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

class LocalRepo(private val database: LendyouDatabase) : Repo {

    override fun thisPerson() = thisId

    private val thisId = PersonId(1)
    private val debts: MutableMap<DebtId, Debt> = ConcurrentHashMap()
    private val persons: MutableMap<PersonId, Person> = ConcurrentHashMap()
    private val subscribers: Deque<() -> Unit> = ConcurrentLinkedDeque()

    init {
        database.addPerson(
            Person(
                PersonId(0),
                "Rina",
                "+79876543210",
                Passport(
                    "100-200",
                    "Irina",
                    "Nikitina",
                    "Nikitina"
                )
            )
        )
        database.addPerson(
            Person(
                PersonId(1),
                "Nikita",
                "+79876543212",
                Passport(
                    "400-100",
                    "Nikita",
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

    override fun createDebt(debtInfo: DebtInfo, from: Account, to: Account, period: Duration): Debt {
        if(thisId == debtInfo.lenderId){
            return createDebtAsLender(debtInfo, from, to, period).also { subscribers.update() }
        }
        if(thisId == debtInfo.debtorId){
            return createDebtAsDebtor(debtInfo, from, to, period).also { subscribers.update() }
        }
        throw IllegalArgumentException("Newly created debt must have this user either as debtor or lender")
    }

    // Unchecked
    private fun createDebtAsLender(debtInfo: DebtInfo, from: Account, to: Account, period: Duration): Debt {
        val newDebt = Debt(
            debtInfo,
            from,
            to,
            period
        )
        debts[newDebt.id] = newDebt
        database.addDebt(newDebt)
        return newDebt
    }

    private fun createDebtAsDebtor(debtInfo: DebtInfo, from: Account, to: Account, period: Duration): Debt {
        // Now it repeats the lender implementations because we have no real end users
        // there is only this user
        return createDebtAsLender(debtInfo, from, to, period)
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

//    override fun getPendingOperations(): Collection<Operation<*>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getCompletedOperations(): Collection<Operation<*>> {
//        TODO("Not yet implemented")
//    }

    override fun nextUniqueId(): Long {
        return debts.size.toLong()
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

    private fun isDebtAdded(id: DebtId): Boolean =
        debts.contains(id)

    private fun Deque<() -> Unit>.update() {
        for (func in this)
            func()
    }

//    inner class NewDebtOperation(
//        debt: Debt,
//    ) : Operation<NewDebtOperationInfo>(
//        id = nextUniqueId(),
//        info = NewDebtOperationInfo(debt),
//        messagePending = R.string.new_debt_pending,
//        messageOnSuccess = R.string.new_debt_complete,
//        messageOnFailure = R.string.new_debt_failure,
//        date = LocalDateTime.now(),
//        expirationDate = LocalDateTime.now().plusMinutes(1)
//    ){
//        override val result: Boolean?
//            get() {
//                if (_result != null) return _result!!
//                if(LocalDateTime.now() > expirationDate){
//                    _result = false
//                    return false
//                }
//                if(isDebtAdded(info.debt.id)) {
//                    _result = true
//                    return true
//                }
//                return null
//            }
//    }
//
//    inner class PaidDebtOperation(
//        debt: Debt
//    ): Operation<PaidDebtOperationInfo>(
//        id = nextUniqueId(),
//        info = PaidDebtOperationInfo(debt),
//        messagePending = R.string.paid_debt_pending,
//        messageOnSuccess = R.string.paid_debt_complete,
//        messageOnFailure = R.string.paid_debt_failure,
//    ) {
//        override val result: Boolean? = null
//    }
}

private fun MutableMap<DebtId, Debt>.addDebt(debt: Debt): Debt {
    put(debt.id, debt)
    return debt
}
