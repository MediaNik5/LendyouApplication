package org.medianik.lendyou.model.bank;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.medianik.lendyou.model.Jsonable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public final class Payment implements Serializable, Jsonable {
    private final LocalDateTime dateTime;
    private final BigDecimal sum;
    private final double sumDouble;
    private final long debtId;

    public Payment(
            LocalDateTime dateTime,
            BigDecimal sum,
            long debtId
    ) {
        this.dateTime = dateTime;
        this.sum = sum;
        this.sumDouble = sum.doubleValue();
        this.debtId = debtId;
    }

    public double getSumDouble() {
        return sumDouble;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public BigDecimal getSum() {
        return sum;
    }

    @NotNull
    public static Payment of(String payment) {
        JsonObject json = JsonParser.parseString(payment).getAsJsonObject();
        return new Payment(
                LocalDateTime.ofEpochSecond(json.getAsJsonPrimitive("dateTime").getAsLong(), 0, ZoneOffset.UTC),
                new BigDecimal(json.getAsJsonPrimitive("sum").getAsString()),
                json.getAsJsonPrimitive("debtId").getAsLong()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Payment) obj;
        return Objects.equals(this.dateTime, that.dateTime) &&
                Objects.equals(this.sum, that.sum) &&
                this.debtId == that.debtId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, sum, debtId);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("dateTime", this.dateTime.toEpochSecond(ZoneOffset.UTC));
        jsonObject.addProperty("sum", this.sum.toString());
        jsonObject.addProperty("debtId", this.debtId);
        return jsonObject;
    }

    public long getDebtId() {
        return debtId;
    }
}
