package com.codingwithmitch.debttracker.repositories;

import android.content.Context;
import android.util.Log;

import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.Payment;
import com.codingwithmitch.debttracker.persistence.AppDatabase;
import com.codingwithmitch.debttracker.persistence.DebtDao;
import com.codingwithmitch.debttracker.persistence.PaymentDao;
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

public class PaymentsRepository {

    private static final String TAG = "PaymentsRepository";

    private static PaymentsRepository instance;
    private PaymentDao paymentsDao;
    private DebtDao debtDao;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public static PaymentsRepository getInstance(Context context){
        if(instance == null){
            instance = new PaymentsRepository(context);
        }
        return instance;
    }

    private PaymentsRepository(Context context){
        paymentsDao = AppDatabase.getInstance(context).getPaymentDao();
        debtDao = AppDatabase.getInstance(context).getDebtDao();
    }

    public LiveData<Resource<List<Payment>>> getPayments(Debt debt){
        return new DatabaseBoundResource.RetrieveData<List<Payment>>(){
            @Override
            public void retrieveAllData() {
                paymentsDao.retrieveAllPayments(debt.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<Payment>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "onSubscribe: called.");
                                disposables.add(d);
                                setValue(Resource.loading(null));
                            }

                            @Override
                            public void onNext(List<Payment> payments) {
                                Log.d(TAG, "onNext: called");
                                setValue(Resource.success(payments));
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: ", e);
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

    public LiveData<Resource<Payment>> insertNewPayment(Payment payment){
        return new DatabaseBoundResource.InsertData<Payment>(){
            @Override
            public void insertNewData() {
                paymentsDao.insertNewPayment(payment)
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
                                Log.e(TAG, "onError: ", e);
                                setValue(Resource.error(e.getMessage(), null));
                            }
                        });
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Debt>> settleDebt(Debt debt, boolean isSettled){
        debt.setIs_settled(isSettled);
        return new DatabaseBoundResource.InsertData<Debt>(){
            @Override
            public void insertNewData() {
                debtDao.updateDebt(debt)
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
                                Log.e(TAG, "onError: ", e);
                                setValue(Resource.error(e.getMessage(), null));
                            }
                        });
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Payment>> deletePayment(Payment payment){
        return new DatabaseBoundResource.UpdateData<Payment>(){

            @Override
            public void updateData() {
                paymentsDao.deletePayment(payment)
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
                                Log.e(TAG, "onError: ", e);
                                setValue(Resource.error(e.getMessage(), null));
                            }
                        });
            }
        }.getAsLiveData();

    }

}










