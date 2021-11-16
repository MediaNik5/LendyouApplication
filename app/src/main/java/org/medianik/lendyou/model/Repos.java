package org.medianik.lendyou.model;

import org.medianik.lendyou.LocalRepo;
import org.medianik.lendyou.model.sql.LendyouDatabase;

public class Repos {
    @Deprecated
    private Repos() {

    }

    private static Repo repo;

    public static Repo getInstance() {
        return repo;
    }

    public static void initRepo(LendyouDatabase database) {
        repo = new LocalRepo(database);
    }
}
