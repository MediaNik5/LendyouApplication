package org.medianik.lendyou.model.person

import org.medianik.lendyou.model.person.Passport
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.person.Lender
import java.lang.RuntimeException
import java.util.ArrayList
import java.util.HashMap

class Debtor(phone: String, passport: Passport, id: PersonId, name: String)
    : User(phone, passport, id, name) {
    private val debts = HashMap<Long, Debt>()
    private val lenders = ArrayList<Lender>()

    /**
     * Pay `sum` of money for this `debt`
     * @param debt Debt to pay
     * @param sum Sum of money to pay
     * @return true if succeeds, false otherwise
     */
    fun putMoney(debt: Debt?, sum: Long): Boolean {
        throw RuntimeException("Not implemented exception")
    }

    fun getDebts(): List<Debt> {
        return ArrayList(debts.values)
    }

    fun getLenders(): List<Lender> {
        return lenders.clone() as List<Lender>
    }
}