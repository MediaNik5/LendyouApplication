package org.medianik.lendyou

import org.medianik.lendyou.model.*
import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtId
import org.medianik.lendyou.model.debt.DebtInfo
import org.medianik.lendyou.model.person.Debtor
import org.medianik.lendyou.model.person.Lender
import org.medianik.lendyou.model.person.Passport
import org.medianik.lendyou.model.person.PersonId
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class LocalRepo(
    private val thisLender: Lender,
    private val thisDebtor: Debtor
) : Repo {

    private val thisId = thisLender.id
    private val debts: MutableMap<DebtId, Debt> = ConcurrentHashMap()
    private val debtors: MutableMap<PersonId, Debtor> = ConcurrentHashMap()
    private val lenders: MutableMap<PersonId, Lender> = ConcurrentHashMap()
    init {
        val rinaLender = Lender(
            "+79876543210",
            Passport(0, "Irina", "Nikitina", "Nikitina"),
            0,
            "Rina"
        )
        val rinaDebtor = Debtor(
            "+79876543210",
            Passport(0, "Irina", "Nikitina", "Nikitina"),
            0,
            "Rina"
        )
        val nikitaLender = Lender(
            "+79876543211",
            Passport(1, "Nikita", "Rostovtsev", "Nikitin"),
            1,
            "Nikita"
        )
        val nikitaDebtor = Debtor(
            "+79876543211",
            Passport(1, "Nikita", "Rostovtsev", "Nikitin"),
            1,
            "Nikita"
        )

        debtors[PersonId(0)] = rinaDebtor
        debtors[PersonId(1)] = nikitaDebtor

        lenders[PersonId(0)] = rinaLender
        lenders[PersonId(1)] = nikitaLender

        val accountRina = Account("Rina")
        val accountNikita = Account("Nikita")

        debts[DebtId(0)] = Debt(
            DebtId(0),
            DebtInfo(BigDecimal(1000), nikitaLender.id, rinaDebtor.id, LocalDateTime.now()),
            accountRina,
            accountNikita,
            Duration.ofDays(30)
        )
        debts[DebtId(1)] = Debt(
            DebtId(1),
            DebtInfo(BigDecimal(3000), nikitaLender.id, rinaDebtor.id, LocalDateTime.now().minusDays(20)),
            accountRina,
            accountNikita,
            Duration.ofDays(30)
        )
    }

    override fun getDebt(debtId: DebtId, forceUpdate: Boolean): Debt? {
        return debts[debtId]
    }

    override fun getDebts(): List<Debt> {
        return ArrayList(debts.values)
    }

    override fun getDebts(lender: Lender): List<Debt> {
        return lenders.values.first {it.id == lender.id}.debts
    }

    override fun getDebts(debtor: Debtor): List<Debt> {
        return debtors.values.first {it.id == debtor.id}.debts
    }

    override fun getDebtors(): List<Debtor> {
        return ArrayList(debtors.values)
    }

    override fun getLenders(): List<Lender> {
        return ArrayList(lenders.values)
    }

    override fun createDebt(debtInfo: DebtInfo, from: Account, to: Account, period: Duration): Debt {
        if(thisId == debtInfo.lenderId){
            return createDebtAsLender(debtInfo, from, to, period)
        }
        if(thisId == debtInfo.debtorId){
            return createDebtAsDebtor(debtInfo, from, to, period)
        }
        throw IllegalArgumentException("Newly created debt must have this user either as debtor or lender")
    }

    // Unchecked
    private fun createDebtAsLender(debtInfo: DebtInfo, from: Account, to: Account, period: Duration): Debt {
        val newDebt = Debt(
            DebtId(nextUniqueId()),
            debtInfo,
            from,
            to,
            period
        )
        debts[newDebt.id()] = newDebt
        return newDebt
    }

    private fun createDebtAsDebtor(debtInfo: DebtInfo, from: Account, to: Account, period: Duration): Debt {
        // Now it repeats the lender implementations because we have no real end users
        // there is only this user
        return createDebtAsLender(debtInfo, from, to, period)
    }

    override fun payDebt(debt: Debt, sum: BigDecimal): Boolean {
        if(thisId == debt.debtInfo().debtorId){
            return payDebtAsDebtor(debt, sum)
        }
        if(thisId == debt.debtInfo().lenderId){
            return payDebtAsLender(debt, sum)
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
        TODO("Not yet implemented")
    }

    fun isDebtAdded(id: DebtId): Boolean =
        debts.contains(id)

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
