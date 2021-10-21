package org.medianik.lendyou.model.person;

import androidx.annotation.NonNull;

import java.util.Objects;

public class User extends Person {
    private final String phone;
    private final Passport passport;

    public User(String phone, Passport passport, long id, String name) {
        super(id, name);
        this.phone = phone;
        this.passport = passport;
    }

    public String phone() {
        return phone;
    }

    public Passport passport() {
        return passport;
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone, passport);
    }

    @NonNull
    @Override
    public String toString() {
        return "User[" +
                "phone=" + phone + ", " +
                "passport=" + passport + ']';
    }
}
