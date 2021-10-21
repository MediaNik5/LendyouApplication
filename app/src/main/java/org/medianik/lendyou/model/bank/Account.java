package org.medianik.lendyou.model.bank;

import java.util.Objects;

/**
 * Banking account
 */
public final class Account {
    private final String cardNumber;

    public Account(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String cardNumber() {
        return cardNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Account) obj;
        return Objects.equals(this.cardNumber, that.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }

    @Override
    public String toString() {
        return "Account[" +
                "cardNumber=" + cardNumber + ']';
    }
}
