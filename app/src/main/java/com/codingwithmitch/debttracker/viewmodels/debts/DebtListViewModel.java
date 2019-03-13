package com.codingwithmitch.debttracker.viewmodels.debts;

import android.app.Application;
import android.util.Log;

import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.DebtAndAllPayments;
import com.codingwithmitch.debttracker.models.Payment;
import com.codingwithmitch.debttracker.models.PaymentAmount;
import com.codingwithmitch.debttracker.repositories.PaymentsRepository;
import com.codingwithmitch.debttracker.util.BigDecimalUtil;
import com.codingwithmitch.debttracker.util.MyPreferenceManager;
import com.codingwithmitch.debttracker.util.Resource;

import java.math.BigDecimal;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;


public class DebtListViewModel extends BaseViewModel {

    private static final String TAG = "DebtListViewModel";
    private MutableLiveData<String> totalDebt = new MutableLiveData<>();
    private MediatorLiveData<Resource<List<DebtAndAllPayments>>> dataSet = new MediatorLiveData<>();
    private MutableLiveData<String> searchViewFilter = new MutableLiveData<>();
    private MutableLiveData<Integer> appBarVerticalOffset = new MutableLiveData<>();
    private PaymentsRepository paymentsRepository;
    private MyPreferenceManager myPreferenceManager;


    public DebtListViewModel(@NonNull Application application) {
        super(application);
        paymentsRepository = PaymentsRepository.getInstance(application);
        myPreferenceManager = MyPreferenceManager.getInstance(application);
    }

    public LiveData<String> getTotalDebt(){
        return totalDebt;
    }


    public LiveData<Resource<List<DebtAndAllPayments>>> getDataSet(){
        return dataSet;
    }

    public LiveData<Resource<Payment>> insertNewPayment(Payment payment){
        return paymentsRepository.insertNewPayment(payment);
    }

    public LiveData<Resource<Debt>> settleDebt(Debt debt){
        return paymentsRepository.settleDebt(debt, true);
    }

    public LiveData<String> getSearchViewFilter(){
        return searchViewFilter;
    }

    public void setSearchViewFilter(String filterText){
        searchViewFilter.setValue(filterText);
    }

    public MutableLiveData<Integer> getAppBarVerticalOffset() {
        return appBarVerticalOffset;
    }

    public void setNewAppBarVerticalOffset(Integer newValue) {
        appBarVerticalOffset.setValue(newValue);
    }

    public MyPreferenceManager getMyPreferenceManager(){
        return myPreferenceManager;
    }

    public void retrieveAllDebtsAndPayments(){
        final LiveData<Resource<List<DebtAndAllPayments>>> source = debtsRepository
                .retrieveAllDebtsAndPayments(
                        myPreferenceManager.getShowOnlySettled(),
                        myPreferenceManager.getOrderByNewest()
                );
        dataSet.addSource(source, listResource -> {
            if(listResource != null){
                dataSet.setValue(listResource);
                if(listResource.data != null){
                    double debtSum = 0;
                    for(DebtAndAllPayments data: listResource.data){
                        if(!data.debt.getIs_settled()){
                            double totalPayed = 0;
                            for(PaymentAmount paymentAmount: data.payments){
                                totalPayed = totalPayed + paymentAmount.amount.doubleValue();
                            }
                            debtSum = debtSum + (data.debt.getDebt_amount().doubleValue() - totalPayed);
                        }
                    }
                    totalDebt.setValue(BigDecimalUtil.getValue(new BigDecimal(debtSum)));
                }
                if(listResource.status.equals(Resource.Status.SUCCESS)
                        || listResource.status.equals(Resource.Status.ERROR)){
                    dataSet.removeSource(source);
                }
            }
        });
    }
}
















