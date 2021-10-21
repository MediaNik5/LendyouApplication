package org.medianik.lendyou.model.exception

import org.medianik.lendyou.model.debt.DebtId
import org.medianik.lendyou.model.debt.DebtInfo
import org.medianik.lendyou.model.person.PersonId

class DifferentLenderDebtException(debtInfo: DebtInfo, thisLenderId: PersonId):
    Exception("Trying to change debt $debtInfo that does not correspond to this as lender with id $thisLenderId. " +
            "It might mean that debt corresponds to this user as debtor.")

class NoSuchDebtException(debtId: DebtId): Exception("Debt with id $debtId does not exist")
class AlreadyPaidDebtException(debtId: DebtId): Exception("Debt with id $debtId does not exist")