package com.codingwithmitch.debttracker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
        tableName = "debts",
        indices = {@Index("id")}
)
public class Debt implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "person")
    private Person person;

    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    @ColumnInfo(name = "lending_date")
    private Date lending_date;

    @ColumnInfo(name = "debt_amount")
    private BigDecimal debt_amount;

    @ColumnInfo(name = "is_settled")
    private Boolean is_settled;

    public Debt(Person person, Date timestamp, Date lending_date, BigDecimal debt_amount, Boolean is_settled) {
        this.person = person;
        this.timestamp = timestamp;
        this.lending_date = lending_date;
        this.debt_amount = debt_amount;
        this.is_settled = is_settled;
    }

    @Ignore
    public Debt() {
    }

    protected Debt(Parcel in) {
        id = in.readInt();
        person = in.readParcelable(Person.class.getClassLoader());
        byte tmpIs_settled = in.readByte();
        is_settled = tmpIs_settled == 0 ? null : tmpIs_settled == 1;
        timestamp = (Date) in.readSerializable();
        lending_date = (Date) in.readSerializable();
        debt_amount = (BigDecimal) in.readSerializable();

    }

    public static final Creator<Debt> CREATOR = new Creator<Debt>() {
        @Override
        public Debt createFromParcel(Parcel in) {
            return new Debt(in);
        }

        @Override
        public Debt[] newArray(int size) {
            return new Debt[size];
        }
    };

    public Boolean getIs_settled() {
        return is_settled;
    }

    public void setIs_settled(Boolean is_settled) {
        this.is_settled = is_settled;
    }

    public Date getLending_date() {
        return lending_date;
    }

    public void setLending_date(Date lending_date) {
        this.lending_date = lending_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getDebt_amount() {
        return debt_amount;
    }

    public void setDebt_amount(BigDecimal debt_amount) {
        this.debt_amount = debt_amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(person, flags);
        dest.writeByte((byte) (is_settled == null ? 0 : is_settled ? 1 : 2));
        dest.writeSerializable(timestamp);
        dest.writeSerializable(lending_date);
        dest.writeSerializable(debt_amount);
    }

    @Override
    public String toString() {
        return "Debt{" +
                "id=" + id +
                ", person=" + person +
                ", timestamp=" + timestamp +
                ", lending_date=" + lending_date +
                ", debt_amount=" + debt_amount +
                ", is_settled=" + is_settled +
                '}';
    }
}















