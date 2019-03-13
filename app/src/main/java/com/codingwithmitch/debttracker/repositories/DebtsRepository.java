package com.codingwithmitch.debttracker.repositories;

import android.content.Context;
import android.util.Log;

import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.DebtAndAllPayments;
import com.codingwithmitch.debttracker.models.Payment;
import com.codingwithmitch.debttracker.persistence.AppDatabase;
import com.codingwithmitch.debttracker.persistence.DebtDao;
import com.codingwithmitch.debttracker.util.DatabaseBoundResource;
import com.codingwithmitch.debttracker.util.Resource;

import java.util.List;

import androidx.lifecycle.LiveData;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DebtsRepository {

    private static final String TAG = "DebtsRepository";

    private static DebtsRepository instance;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private DebtDao debtDao;

    public static DebtsRepository getInstance(Context context){
        if(instance == null){
            instance = new DebtsRepository(context);
        }
        return instance;
    }

    private DebtsRepository(Context context) {
        debtDao = AppDatabase.getInstance(context).getDebtDao();
    }


    public CompositeDisposable getDisposables(){
        return disposables;
    }

    public LiveData<Resource<Debt>> insertNewData(Debt debt){
        Log.d(TAG, "insertNewDebt: called.");
        return new DatabaseBoundResource.InsertData<Debt>(){
            @Override
            public void insertNewData() {
                debtDao.insertNewDebt(debt)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "onSubscribe: called.");
                                disposables.add(d);
                                setValue(Resource.loading(null));
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: called.");
                                setValue(Resource.success(null));
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: called.");
                                setValue(Resource.error(e.getMessage(), null));
                            }
                        });
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<DebtAndAllPayments>>> retrieveAllDebtsAndPayments(boolean showOnlySettled, boolean orderByNewest){

        if(orderByNewest){
            return new DatabaseBoundResource.RetrieveData<List<DebtAndAllPayments>>(){
                @Override
                public void retrieveAllData() {
                    debtDao.retrieveAllDebtsAndPaymentsDES(showOnlySettled)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<List<DebtAndAllPayments>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    disposables.add(d);
                                }

                                @Override
                                public void onNext(List<DebtAndAllPayments> debtsAndPayments) {
                                    Log.d(TAG, "onNext: called.");
                                    setValue(Resource.success(debtsAndPayments));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "onError: called. " + e.getMessage());
                                    setValue(Resource.error(e.getMessage(), null));
                                }

                                @Override
                                public void onComplete() {
                                    Log.d(TAG, "onComplete: called.");
                                }
                            });
                }
            }.getAsLiveData();
        }
        else{
            return new DatabaseBoundResource.RetrieveData<List<DebtAndAllPayments>>(){
                @Override
                public void retrieveAllData() {
                    debtDao.retrieveAllDebtsAndPaymentsASC(showOnlySettled)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<List<DebtAndAllPayments>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    disposables.add(d);
                                }

                                @Override
                                public void onNext(List<DebtAndAllPayments> debtsAndPayments) {
                                    Log.d(TAG, "onNext: called.");
                                    setValue(Resource.success(debtsAndPayments));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "onError: called. " + e.getMessage());
                                    setValue(Resource.error(e.getMessage(), null));
                                }

                                @Override
                                public void onComplete() {
                                    Log.d(TAG, "onComplete: called.");
                                }
                            });
                }
            }.getAsLiveData();
        }
    }


    public void updateDebt(Debt debt){
        debtDao.updateDebt(debt)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called.");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called. Updated a debt: " + debt.getPerson() + ", " + debt.getDebt_amount());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }
                });
    }

}














