package com.codingwithmitch.debttracker.util;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public abstract class CursorBoundResource<CursorObject> {

    private static final String TAG = "CursorBoundResource";

    private MutableLiveData<Resource<List<CursorObject>>> results = new MutableLiveData<>();
    private Cursor cursor;

    public CursorBoundResource(ContentResolver contentResolver, Uri uri) {
        Log.d(TAG, "CursorBoundResource: called.");
        this.cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        init();
    }

    private void init(){

        // tell the UI something is loading
        Log.d(TAG, "init: loading...");
        results.setValue(Resource.loading(null));

        // Create the observable
        Observable<CursorObject> observable = Observable.fromIterable(() -> new Iterator<Cursor>() {
            @Override
            public boolean hasNext() {
                Log.d(TAG, "hasNext: called.");
                return !cursor.isClosed() && cursor.moveToNext();
            }

            @Override
            public Cursor next() {
                Log.d(TAG, "next: called. " + cursor.isClosed());
                return cursor;
            }
        })
                .doAfterNext(c -> {
                    Log.d(TAG, "init: called.");
                    if(c.getPosition() == c.getCount() - 1){
                        Log.d(TAG, "doAfterNext: " + Thread.currentThread().getName() + ". Done. Closing Cursor.");
                        c.close();
                    }
                })
                .doOnError(throwable -> {
                    Log.e(TAG, "accept: ", throwable );
                    cursor.close();
                    setValue(Resource.error(throwable.getMessage(), null));
                })
                .map(createMapFunction())
                .subscribeOn(Schedulers.io());

        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObserver());
    }

    public void setValue(Resource<List<CursorObject>> newValue) {
        if (results.getValue() != newValue) {
            results.setValue(newValue);
        }
    }

    public abstract Function<Cursor, CursorObject> createMapFunction();

    public abstract Observer<CursorObject> createObserver();

    public final LiveData<Resource<List<CursorObject>>> getAsLiveData(){
        return results;
    }

    public void done(){
        // Nullify the cursor or will get memory leaks
        cursor = null;
    };
}























