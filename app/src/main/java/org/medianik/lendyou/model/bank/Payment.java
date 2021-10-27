package org.medianik.lendyou.model.bank;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Payment implements Serializable {
    private final LocalDateTime dateTime;
    private final BigDecimal sum;
    private final double sumDouble;
    private final Account from;
    private final Account to;

    public Payment(
            LocalDateTime dateTime,
            BigDecimal sum,
            Account from,
            Account to
    ) {
        this.dateTime = dateTime;
        this.sum = sum;
        sumDouble = sum.doubleValue();
        this.from = from;
        this.to = to;
    }

    public double sumDouble() {
        return sumDouble;
    }

    public LocalDateTime dateTime() {
        return dateTime;
    }

    public BigDecimal sum() {
        return sum;
    }

    public Account from() {
        return from;
    }

    public Account to() {
        return to;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Payment) obj;
        return Objects.equals(this.dateTime, that.dateTime) &&
                Objects.equals(this.sum, that.sum) &&
                Objects.equals(this.from, that.from) &&
                Objects.equals(this.to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, sum, from, to);
    }

    @Override
    public String toString() {
        return "Payment[" +
                "dateTime=" + dateTime + ", " +
                "sum=" + sum + ", " +
                "from=" + from + ", " +
                "to=" + to + ']';
    }

}
