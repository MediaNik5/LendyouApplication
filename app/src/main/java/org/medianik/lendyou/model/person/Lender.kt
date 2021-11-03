package org.medianik.lendyou.model.person

import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtInfo
import java.io.Serializable

class Lender(phone: String, passport: Passport, id: PersonId, name: String) : User(
    phone, passport, id, name
), Serializable {
    private val debts = HashMap<Long, Debt>()
    private val debtors = ArrayList<Debtor>()

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

    fun getDebts(): List<Debt> {
        return ArrayList(debts.values)
    }

    fun getDebtors(): List<Debtor> {
        return debtors.clone() as List<Debtor>
    }
}