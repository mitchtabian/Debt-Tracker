package com.codingwithmitch.debttracker.models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

@Entity(
        tableName = "payments",
        foreignKeys = @ForeignKey(
                entity = Debt.class,
                parentColumns = "id",
                childColumns = "debt_id"
        ),
        indices = {@Index("debt_id")}
)
public class Payment {


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "payment_id")
    private int payment_id;

    @ColumnInfo(name = "debt_id")
    private int debt_id;

    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    @ColumnInfo(name = "amount")
    private BigDecimal amount;

    public Payment(int payment_id, int debt_id, Date timestamp, BigDecimal amount) {
        this.payment_id = payment_id;
        this.debt_id = debt_id;
        this.timestamp = timestamp;
        this.amount = amount;
    }


    @Ignore
    public Payment() {
    }

    public int getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }

    public int getDebt_id() {
        return debt_id;
    }

    public void setDebt_id(int debt_id) {
        this.debt_id = debt_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


}
