package com.codingwithmitch.debttracker.repositories;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.codingwithmitch.debttracker.models.Person;
import com.codingwithmitch.debttracker.util.CursorBoundResource;
import com.codingwithmitch.debttracker.util.Resource;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;


public class ContactsRepository {

    private static final String TAG = "ContactsRepository";

    private static ContactsRepository instance;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public static ContactsRepository getInstance(){
        if(instance == null){
            instance = new ContactsRepository();
        }
        return instance;
    }

    private ContactsRepository() {

    }

    public CompositeDisposable getDisposables(){
        return disposables;
    }
    /**
     * Retrieves Contacts from the phones Contacts app
     * It will get the NAME and PHOTO_URI
     */
    public LiveData<Resource<List<Person>>> retrieveContacts(ContentResolver contentResolver){

        return new CursorBoundResource<Person>(contentResolver, ContactsContract.Contacts.CONTENT_URI){

            @Override
            public Function<Cursor, Person> createMapFunction() {
                return cursor -> {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String photo_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                    Log.d(TAG, "map: " + Thread.currentThread().getName() + ". name: " + name);
                    Log.d(TAG, "map: " + Thread.currentThread().getName() + ". photo_uri: " + photo_uri);
                    return new Person(name, photo_uri);
                };
            }

            @Override
            public Observer<Person> createObserver() {
                Log.d(TAG, "createObserver: called.");
                List<Person> contacts = new ArrayList<>();
                return new Observer<Person>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called. " + d);
                        disposables.add(d);

                        // tell the UI you're doing something with a loading status
                        // can use this to show a progress bar
                        setValue(Resource.loading(null));
                    }

                    @Override
                    public void onNext(Person person) {
                        Log.d(TAG, "onNext: " + Thread.currentThread().getName() + ". name: " + person.getName());
                        contacts.add(person);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        setValue(Resource.error(e.getMessage(), null));
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called.");
                        setValue(Resource.success(contacts));
                        done();
                    }

                };


            }
        }.getAsLiveData();
    }

    /**
     * Retrieve a single contact from the device contacts app using a cursor
     * @param contentResolver
     * @param uri
     * @return
     */
    public LiveData<Resource<List<Person>>> retrieveSingleContact(@NonNull ContentResolver contentResolver, @NonNull Uri uri){

        return new CursorBoundResource<Person>(contentResolver, uri){
            Person mPerson;

            @Override
            public Function<Cursor, Person> createMapFunction() {
                return cursor -> {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String photo_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                    Log.d(TAG, "map: " + Thread.currentThread().getName() + ". name: " + name);
                    Log.d(TAG, "map: " + Thread.currentThread().getName() + ". photo_uri: " + photo_uri);
                    return new Person(name, photo_uri);
                };
            }

            @Override
            public Observer<Person> createObserver() {
                return new Observer<Person>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called.");
                        disposables.add(d);

                        // tell the UI you're doing something with a loading status
                        // can use this to show a progress bar
                        setValue(Resource.loading(null));
                    }

                    @Override
                    public void onNext(Person person) {
                        mPerson = person;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        setValue(Resource.error(e.getMessage(), null));
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called.");
                        if(mPerson != null){
                            List<Person> personList = new ArrayList<>();
                            personList.add(mPerson);
                            setValue(Resource.success(personList));
                        }
                        else{
                            setValue(Resource.error("Could not retrieve contact", null));
                        }
                    }
                };
            }
        }.getAsLiveData();

    }

}





















