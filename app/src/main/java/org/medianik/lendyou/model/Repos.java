package org.medianik.lendyou.model;

import com.google.firebase.auth.FirebaseAuth;

import org.medianik.lendyou.LocalRepo;
import org.medianik.lendyou.util.ServerDatabase;
import org.medianik.lendyou.util.sql.LendyouDatabase;

public class Repos {
    @Deprecated
    private Repos() {

    }

    private static Repo repo;

    public static Repo getInstance() {
        return repo;
    }

    public static void initRepo(LendyouDatabase database, ServerDatabase storage, FirebaseAuth auth, boolean isLender) {
        repo = new LocalRepo(database, storage, isLender, auth);
    }
}
