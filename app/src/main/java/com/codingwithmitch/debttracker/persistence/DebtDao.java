package com.codingwithmitch.debttracker.persistence;

import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.DebtAndAllPayments;
import com.codingwithmitch.debttracker.models.Payment;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface DebtDao {

    @Insert
    Completable insertNewDebt(Debt debt);

    @Update
    Completable updateDebt(Debt debt);

    @Query("SELECT * FROM debts")
    Observable<List<Debt>> retrieveAllDebts();

//    // inner join on debts and payments tables
//    @Query("SELECT * FROM debts")
//    Observable<List<DebtAndAllPayments>> retrieveAllDebtsAndPayments();

    // inner join on debts and payments tables (DATE ASCENDING)
    @Query("SELECT * FROM debts WHERE is_settled = :isSettled ORDER BY lending_date ASC")
    Observable<List<DebtAndAllPayments>> retrieveAllDebtsAndPaymentsASC(boolean isSettled);

    // inner join on debts and payments tables (DATE DESCENDING)
    @Query("SELECT * FROM debts WHERE is_settled = :isSettled ORDER BY lending_date DESC")
    Observable<List<DebtAndAllPayments>> retrieveAllDebtsAndPaymentsDES(boolean isSettled);


}


























