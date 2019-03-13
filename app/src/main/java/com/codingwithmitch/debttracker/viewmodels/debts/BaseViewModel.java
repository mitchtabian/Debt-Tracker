package com.codingwithmitch.debttracker.viewmodels.debts;


import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;

import com.codingwithmitch.debttracker.models.Person;
import com.codingwithmitch.debttracker.repositories.ContactsRepository;
import com.codingwithmitch.debttracker.repositories.DebtsRepository;
import com.codingwithmitch.debttracker.util.Resource;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public abstract class BaseViewModel extends AndroidViewModel {

    private static final String TAG = "BaseViewModel";

    public ContactsRepository contactsRepository;
    public DebtsRepository debtsRepository;
    private MediatorLiveData<Resource<List<Person>>> contactsList = new MediatorLiveData<>();
    private MutableLiveData<Person> selectedPerson = new MutableLiveData<>();
    private MutableLiveData<Date> selectedDate = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
        contactsRepository = ContactsRepository.getInstance();
        debtsRepository = DebtsRepository.getInstance(application);
    }

    public LiveData<Resource<List<Person>>> getContactsList(){
        return contactsList;
    }

    public LiveData<Person> getSelectedPerson(){
        return selectedPerson;
    }

    public LiveData<Date> getSelectedDate(){
        return selectedDate;
    }

    public void setSelectedDate(Date date){
        selectedDate.setValue(date);
    }

    public void setSelectedPerson(Person person){
        selectedPerson.setValue(person);
    }

    public void retrieveNewlyAddedContact(ContentResolver contentResolver, Uri uri){
        final LiveData<Resource<List<Person>>> newContact = contactsRepository.retrieveSingleContact(contentResolver, uri);
        contactsList.addSource(newContact, listResource -> {
            switch(listResource.status){
                case LOADING:{
                    break;
                }
                case SUCCESS:{
                    selectedPerson.setValue(listResource.data.get(0));
                    contactsList.removeSource(newContact);
                    retrieveContacts(contentResolver); // retrieve the new list now that a contact has been added
                    break;
                }
                case ERROR:{
                    contactsList.removeSource(newContact);
                    break;
                }
            }
        });
    }

    public void retrieveContacts(ContentResolver contentResolver){
        final LiveData<Resource<List<Person>>> contacts = contactsRepository.retrieveContacts(contentResolver);
        contactsList.addSource(contacts, listResource -> {

            contactsList.setValue(listResource);
            switch(listResource.status){
                case LOADING:{
                    break;
                }
                case SUCCESS:{
                    contactsList.removeSource(contacts);
                    break;
                }
                case ERROR:{
                    contactsList.removeSource(contacts);
                    break;
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        contactsRepository.getDisposables().clear();
        debtsRepository.getDisposables().clear();
    }
}















