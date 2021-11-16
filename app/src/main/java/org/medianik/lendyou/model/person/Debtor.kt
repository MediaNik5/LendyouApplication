package org.medianik.lendyou.model.person

import org.medianik.lendyou.model.debt.Debt
import java.io.Serializable
import java.util.*

class Debtor(id: PersonId, name: String, phone: String, passport: Passport) :
    Person(id, name, phone, passport), Serializable {

    //    @Relation(
//        parentColumn = "id",
//        entityColumn = "debtor_id"
//    )
    private val _debts = HashMap<Long, Debt>()
    val debts: Collection<Debt> by lazy { Collections.unmodifiableCollection(_debts.values) }
    private val _debtors = ArrayList<Lender>()
    val debtors: Collection<Lender> by lazy { Collections.unmodifiableCollection(_debtors) }

    /**
     * Pay `sum` of money for this `debt`
     * @param debt Debt to pay
     * @param sum Sum of money to pay
     * @return true if succeeds, false otherwise
     */
    fun putMoney(debt: Debt?, sum: Long): Boolean {
        throw RuntimeException("Not implemented exception")
    }
}