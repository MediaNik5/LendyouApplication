package org.medianik.lendyou.model.person

import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtInfo
import java.io.Serializable
import java.util.*

class Lender(id: PersonId, name: String, phone: String, passport: Passport) :
    Person(id, name, phone, passport), Serializable {

    private val _debts = HashMap<Long, Debt>()
    val debts: Collection<Debt> by lazy { Collections.unmodifiableCollection(_debts.values) }

    private val _debtors = ArrayList<Debtor>()
    val debtors: Collection<Debtor> by lazy { Collections.unmodifiableCollection(_debtors) }

    /**
     * Supposed to be called by debtor to ask if
     * @param debtInfo
     * @param person
     * @return
     */
    fun requestNewDebt(debtInfo: DebtInfo?, person: Person?): Boolean {
        throw RuntimeException("Not yet implemented")
    }

    fun getNewDebt(debtInfo: DebtInfo?, debtor: Debtor?, account: Account?): Debt {
        throw RuntimeException("Not yet implemented")
    }

    fun close(debt: Debt?): Boolean {
        throw RuntimeException("Not yet implemented")
    }
}