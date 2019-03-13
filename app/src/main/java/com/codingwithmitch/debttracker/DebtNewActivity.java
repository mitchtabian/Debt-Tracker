package com.codingwithmitch.debttracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.Person;
import com.codingwithmitch.debttracker.util.BigDecimalUtil;
import com.codingwithmitch.debttracker.util.DateConverterUtil;
import com.codingwithmitch.debttracker.util.DecimalDigitsInputFilter;
import com.codingwithmitch.debttracker.util.Resource;
import com.codingwithmitch.debttracker.viewmodels.debts.DebtNewViewModel;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import static com.codingwithmitch.debttracker.util.Constants.INSERT_NEW_CONTACT_CODE;


public class DebtNewActivity extends BaseActivity implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener
{

    private static final String TAG = "DebtNewActivity";

    // ui
    private TextView mSelectedLendingDate, mSelectedContact;
    private EditText mDebtAmount;

    // vars
    private DebtNewViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_debt);
        mSelectedContact = findViewById(R.id.selected_contact);
        mSelectedLendingDate = findViewById(R.id.selected_lending_date);
        mDebtAmount = findViewById(R.id.debt_amount);

        findViewById(R.id.lending_date_selector).setOnClickListener(this);
        findViewById(R.id.add_contact).setOnClickListener(this);
        findViewById(R.id.create_debt).setOnClickListener(this);
        mSelectedContact.setOnClickListener(this);
        mSelectedLendingDate.setOnClickListener(this);

        viewModel = ViewModelProviders.of(this).get(DebtNewViewModel.class);

        initToolbar();
        initDebtAmountEditText();

        subscribeObservers();
        retrieveContacts();
        init();
    }

    private void createNewDebt(){
        Log.d(TAG, "createNewDebt: called.");
        Log.d(TAG, "createNewDebt: " + viewModel.getSelectedDate().getValue());

        viewModel.insertNewDebt(
                new Debt(
                        viewModel.getSelectedPerson().getValue(),
                        Calendar.getInstance().getTime(),
                        viewModel.getSelectedDate().getValue(),
                        new BigDecimal(Double.parseDouble((mDebtAmount.getText().toString().equals("") ? "0.00" : mDebtAmount.getText().toString()))),
                        false
                )
        ).observe(this, debtResource -> {
            if(debtResource != null){
                switch(debtResource.status){

                    case LOADING:{
                        Log.d(TAG, "createNewDebt: inserting new debt...");
                        showProgressBar(true);
                        break;
                    }

                    case SUCCESS:{
                        Log.d(TAG, "createNewDebt: DONE inserting new debt...");
                        showProgressBar(false);
                        finish();
                        break;
                    }

                    case ERROR:{
                        String errorMsg = debtResource.message;
                        Log.e(TAG, "createNewDebt: ERROR while inserting new debt..." +  errorMsg );
                        Toast.makeText(DebtNewActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
    }

    private void init(){
        viewModel.setSelectedDate(Calendar.getInstance().getTime());
    }

    private void retrieveContacts(){
        viewModel.retrieveContacts(getContentResolver());
    }

    private void retrieveNewContact(Uri uri){
        viewModel.retrieveNewlyAddedContact(getContentResolver(), uri);
    }

    private void initDebtAmountEditText(){
        mDebtAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mDebtAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
    }

    private void subscribeObservers(){

        viewModel.getSelectedPerson().observe(this, person -> {
            if(person != null){
                mSelectedContact.setText(person.getName());
            }
        });

        viewModel.getSelectedDate().observe(this, date -> {
            if(date != null){
                mSelectedLendingDate.setText(DateConverterUtil.getStringFormattedDate(date));
            }
            else{
                mSelectedLendingDate.setText(DateConverterUtil.getStringFormattedDate(Calendar.getInstance().getTime()));
            }
        });


        viewModel.getContactsList().observe(this, listResource -> {
            if(listResource != null){
                if(listResource.data != null){
                    switch(listResource.status){
                        case LOADING:{
                            // loading the contacts...
                            Log.d(TAG, "subscribeObservers: loading contacts from device...");
                            showProgressBar(true);
                            break;
                        }

                        case SUCCESS:{
                            Log.d(TAG, "subscribeObservers: DONE loading contacts from device...");

                            showProgressBar(false);
                            if(mSelectedContact.getText().toString().equals("")){
                                viewModel.setSelectedPerson(listResource.data.get(0));
                            }
                            break;
                        }

                        case ERROR:{
                            String errorMsg = listResource.message;
                            Log.e(TAG, "subscribeObservers: ERROR while loading contacts from device..." +  errorMsg );
                            Toast.makeText(DebtNewActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            showProgressBar(false);
                            break;
                        }
                    }
                }
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == INSERT_NEW_CONTACT_CODE) {
            if(data != null){
                if(data.getData() != null){
                    Log.d(TAG, "onActivityResult: added a new contact.");
                    retrieveNewContact(data.getData());
                }
            }

        }
    }

    private void initToolbar(){
        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("New Debt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Respond to the action bar's Up/Home button
            case android.R.id.home: {
                finish();
                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.lending_date_selector:{
                showDatePickerDialog();
                break;
            }

            case R.id.selected_lending_date:{
                showDatePickerDialog();
                break;
            }

            case R.id.add_contact:{
                addNewContact();
                break;
            }

            case R.id.create_debt:{
                // insert debt into sqlite db
                createNewDebt();
                break;
            }

            case R.id.selected_contact:{
                // show dialog for selecting a user
                showContactsDialog(new ArrayList<>(viewModel.getContactsList().getValue().data));
                break;
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date selectedDate = DateConverterUtil.getDateFromDatePicker(view);
        Log.d(TAG, "onDateSet: " + DateConverterUtil.getStringFormattedDate(selectedDate));
        viewModel.setSelectedDate(selectedDate);
    }

    @Override
    public void onPersonClicked(Person person) {
        viewModel.setSelectedPerson(person);
    }
}


















