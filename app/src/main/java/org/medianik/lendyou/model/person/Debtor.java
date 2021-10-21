package org.medianik.lendyou.model.person;

import org.medianik.lendyou.model.debt.Debt;

import java.util.*;

public class Debtor extends User {

    private final HashMap<Long, Debt> debts = new HashMap<>();
    private final ArrayList<Lender> lenders = new ArrayList<>();

    public Debtor(String phone, Passport passport, long id, String name) {
        super(phone, passport, id, name);
    }

    /**
     * Pay {@code sum} of money for this {@code debt}
     * @param debt Debt to pay
     * @param sum Sum of money to pay
     * @return true if succeeds, false otherwise
     */
    public boolean putMoney(Debt debt, long sum){
        throw new RuntimeException("Not implemented exception");
    }

    public List<Debt> getDebts(){
        return new ArrayList<>(debts.values());
    }

    public List<Lender> getLenders(){
        //noinspection unchecked
        return (List<Lender>) lenders.clone();
    }
}
