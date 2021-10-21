package org.medianik.lendyou.model;

import org.medianik.lendyou.model.person.Debtor;
import org.medianik.lendyou.model.person.Lender;

public class Repos {
    public static Repos instance;

    @Deprecated
    public Repos(){

    }

    private Repo repo;
    private Debtor thisDebtor;
    private Lender thisLender;
    public Repo getCurrentRepo(){
        return repo;
    }
    public static Repos getInstance(){
        return instance;
    }
}
