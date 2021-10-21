package org.medianik.lendyou.model.person;

import org.medianik.lendyou.model.bank.Account;
import org.medianik.lendyou.model.debt.Debt;
import org.medianik.lendyou.model.debt.DebtInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lender extends User {

    private final HashMap<Long, Debt> debts = new HashMap<>();
    private final ArrayList<Debtor> debtors = new ArrayList<>();

    public Lender(String phone, Passport passport, long id, String name) {
        super(phone, passport, id, name);
    }

    /**
     * Supposed to be called by debtor to ask if
     * @param debtInfo
     * @param person
     * @return
     */
    public boolean requestNewDebt(DebtInfo debtInfo, Person person){
        throw new RuntimeException("Not yet implemented");
    }

    public Debt getNewDebt(DebtInfo debtInfo, Debtor debtor, Account account){
        throw new RuntimeException("Not yet implemented");
    }

    public boolean close(Debt debt){
        throw new RuntimeException("Not yet implemented");
    }

    public List<Debt> getDebts(){
        return new ArrayList<>(debts.values());
    }

    public List<Debtor> getDebtors(){
        //noinspection unchecked
        return (List<Debtor>) debtors.clone();
    }
}
