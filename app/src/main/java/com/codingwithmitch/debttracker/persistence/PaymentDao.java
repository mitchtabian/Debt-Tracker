package com.codingwithmitch.debttracker.persistence;

import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.Payment;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface PaymentDao {

    @Insert
    Completable insertNewPayment(Payment payment);

    @Update
    Completable updatePayment(Payment payment);

    @Delete
    Completable deletePayment(Payment payment);

    @Query("SELECT * FROM payments WHERE debt_id = :debt_id")
    Observable<List<Payment>> retrieveAllPayments(int debt_id);
}












