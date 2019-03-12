package com.codingwithmitch.debttracker.viewmodels.debts;


import android.app.Application;

import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.util.Resource;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class DebtNewViewModel extends BaseViewModel {

    private static final String TAG = "DebtNewViewModel";

    public DebtNewViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Resource<Debt>> insertNewDebt(Debt debt){
        return debtsRepository.insertNewData(debt);
    }
}



















