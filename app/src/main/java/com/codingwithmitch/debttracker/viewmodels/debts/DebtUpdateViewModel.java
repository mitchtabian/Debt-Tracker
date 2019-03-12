package com.codingwithmitch.debttracker.viewmodels.debts;


import android.app.Application;
import android.util.Log;

import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.Payment;
import com.codingwithmitch.debttracker.repositories.PaymentsRepository;
import com.codingwithmitch.debttracker.util.BigDecimalUtil;
import com.codingwithmitch.debttracker.util.Resource;

import java.math.BigDecimal;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DebtUpdateViewModel extends BaseViewModel {

    private static final String TAG = "DebtUpdateViewModel";

    private MutableLiveData<Debt> debt = new MutableLiveData<>();
    private BigDecimal repayedAmount = new BigDecimal(0);
    private PaymentsRepository paymentsRepository;

    public DebtUpdateViewModel(@NonNull Application application) {
        super(application);
        paymentsRepository = PaymentsRepository.getInstance(application);
    }

    public LiveData<Debt> getDebt(){
        return debt;
    }

    public LiveData<Resource<List<Payment>>> getPayments(Debt debt){
        return paymentsRepository.getPayments(debt);
    }

    public LiveData<Resource<Payment>> insertNewPayment(Payment payment){
        return paymentsRepository.insertNewPayment(payment);
    }


    public void setDebt(Debt d){
        debt.setValue(d);
    }

    public void updateDebt(){
        Log.d(TAG, "updateDebt: called.");
        debtsRepository.updateDebt(debt.getValue());
    }

    public void setRepayedAmount(List<Payment> payments){
        double totalRepayed = 0;
        for(Payment payment: payments){
            totalRepayed = totalRepayed + payment.getAmount().doubleValue();
        }
        repayedAmount = new BigDecimal(totalRepayed);
    }

    public boolean isDebtSettled(){
        if(repayedAmount.doubleValue() >= debt.getValue().getDebt_amount().doubleValue()){
            return true;
        }
        return false;
    }

    public LiveData<Resource<Debt>> settleDebt(){
        return paymentsRepository.settleDebt(debt.getValue(), true);
    }

    public LiveData<Resource<Debt>> reverseSettleDebt(){
        return paymentsRepository.settleDebt(debt.getValue(), false);
    }

    public String getRemainingAmount(){
        return BigDecimalUtil.getValue(new BigDecimal(debt.getValue().getDebt_amount().doubleValue()
                - repayedAmount.doubleValue()));
    }

    public LiveData<Resource<Payment>> removePayment(Payment payment){
        return paymentsRepository.deletePayment(payment);
    }

}

















