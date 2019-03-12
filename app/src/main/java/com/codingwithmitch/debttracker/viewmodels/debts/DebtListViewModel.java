package com.codingwithmitch.debttracker.viewmodels.debts;

import android.app.Application;

import com.codingwithmitch.debttracker.models.DebtAndAllPayments;
import com.codingwithmitch.debttracker.models.PaymentAmount;
import com.codingwithmitch.debttracker.util.BigDecimalUtil;
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
//    private MediatorLiveData<Resource<List<Debt>>> debts = new MediatorLiveData<>();
//    private MediatorLiveData<Resource<List<DebtsAndPayments>>> dataSet = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<DebtAndAllPayments>>> dataSet = new MediatorLiveData<>();

    public DebtListViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getTotalDebt(){
        return totalDebt;
    }

//    public LiveData<Resource<List<Debt>>> getDebts(){
//        return debts;
//    }

//    public LiveData<Resource<List<DebtsAndPayments>>> getDataSet(){
//        return dataSet;
//    }

    public LiveData<Resource<List<DebtAndAllPayments>>> getDataSet(){
        return dataSet;
    }


//    public void retrieveDebts(){
//        final LiveData<Resource<List<Debt>>> source = debtsRepository.retrieveAllDebts();
//        debts.addSource(source, listResource -> {
//            if(listResource != null){
//                debts.setValue(listResource);
//                if(listResource.data != null){
//                    double debtSum = 0;
//                    for(Debt debt: listResource.data){
//                        if(!debt.getIs_settled()){
//                            debtSum = debtSum + debt.getDebt_amount().doubleValue();
//                        }
//                    }
//                    totalDebt.setValue(BigDecimalUtil.getValue(new BigDecimal(debtSum)));
//                }
//                if(listResource.status.equals(Resource.Status.SUCCESS)
//                        || listResource.status.equals(Resource.Status.ERROR)){
//                    debts.removeSource(source);
//                }
//            }
//        });
//    }

    public void retrieveAllDebtsAndPayments(){
        final LiveData<Resource<List<DebtAndAllPayments>>> source = debtsRepository.retrieveAllDebtsAndPayments();
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
















