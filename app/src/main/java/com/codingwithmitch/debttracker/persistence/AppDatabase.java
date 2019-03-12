package com.codingwithmitch.debttracker.persistence;

import android.content.Context;

import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.Payment;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Debt.class, Payment.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "app_db";

    private static AppDatabase instance;

    public static AppDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract PaymentDao getPaymentDao();

    public abstract DebtDao getDebtDao();
}










