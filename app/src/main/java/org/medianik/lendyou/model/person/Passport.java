package org.medianik.lendyou.model.person;

import java.util.Objects;

public final class Passport {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final String middleName;

    public Passport(
            long id,
            String firstName,
            String lastName,
            String middleName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
    }

    public long id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String middleName() {
        return middleName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Passport) obj;
        return this.id == that.id &&
                Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.lastName, that.lastName) &&
                Objects.equals(this.middleName, that.middleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, middleName);
    }

    @Override
    public String toString() {
        return "Passport[" +
                "id=" + id + ", " +
                "firstName=" + firstName + ", " +
                "lastName=" + lastName + ", " +
                "middleName=" + middleName + ']';
    }
}
