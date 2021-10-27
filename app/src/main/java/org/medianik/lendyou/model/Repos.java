package org.medianik.lendyou.model;

import org.medianik.lendyou.LocalRepo;
import org.medianik.lendyou.model.person.Debtor;
import org.medianik.lendyou.model.person.Lender;

public class Repos {
    private static final Repos instance = new Repos();

    @Deprecated
    private Repos(){

    }

    private Repo repo = new LocalRepo();
//    private Debtor thisDebtor;
//    private Lender thisLender;
    public Repo getCurrentRepo(){
        return repo;
    }

    public static Repos getInstance(){
        return instance;
    }
}
