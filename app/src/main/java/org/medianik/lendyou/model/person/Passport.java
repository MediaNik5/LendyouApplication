package org.medianik.lendyou.model.person;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public final class Passport implements Serializable {
    private final String passportId;
    private final String firstName;
    private final String middleName;
    private final String lastName;

    public Passport(
            @NonNull String passportId,
            @NonNull String firstName,
            @NonNull String middleName,
            @NonNull String lastName
    ) {
        this.passportId = passportId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
    }

    @NonNull
    public String getId() {
        return passportId;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    @NonNull
    public String getLastName() {
        return lastName;
    }

    @NonNull
    public String getMiddleName() {
        return middleName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Passport) obj;
        return this.passportId.equals(that.passportId) &&
                Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.lastName, that.lastName) &&
                Objects.equals(this.middleName, that.middleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passportId, firstName, lastName, middleName);
    }

    @NonNull
    @Override
    public String toString() {
        return "Passport[" +
                "id=" + passportId + ", " +
                "firstName=" + firstName + ", " +
                "lastName=" + lastName + ", " +
                "middleName=" + middleName + ']';
    }
}
