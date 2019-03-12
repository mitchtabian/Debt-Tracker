package com.codingwithmitch.debttracker.util;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class DatabaseBoundResource<DatabaseResponse> {


    private MutableLiveData<Resource<DatabaseResponse>> results = new MutableLiveData<>();

    public void setValue(Resource<DatabaseResponse> newValue) {
        if (results.getValue() != newValue) {
            results.setValue(newValue);
        }
    }

    public final LiveData<Resource<DatabaseResponse>> getAsLiveData(){
        return results;
    }

    public abstract static class InsertData<DatabaseResponse> extends DatabaseBoundResource<DatabaseResponse>{

        public InsertData() {
            insertNewData();
        }

        public abstract void insertNewData();

    }

    public abstract static class RetrieveData<DatabaseResponse> extends DatabaseBoundResource<DatabaseResponse>{

        public RetrieveData() {
            retrieveAllData();
        }

        public abstract void retrieveAllData();

    }


    public abstract static class UpdateData<DatabaseResponse> extends DatabaseBoundResource<DatabaseResponse>{

        public UpdateData() {
            updateData();
        }

        public abstract void updateData();

    }

}


















