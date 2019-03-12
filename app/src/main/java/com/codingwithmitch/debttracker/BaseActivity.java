package com.codingwithmitch.debttracker;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.debttracker.models.Person;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import static com.codingwithmitch.debttracker.util.Constants.INSERT_NEW_CONTACT_CODE;
import static com.codingwithmitch.debttracker.util.Constants.PERMISSION_REQUEST_CONTACTS;

public abstract class BaseActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        ChooseContactDialog.OnRetrievedPerson
{

    private static final String TAG = "BaseActivity";

    // ui
    public ContentLoadingProgressBar mProgressBar;
    private ChooseContactDialog mChooseContactDialog;

    // vars


    @Override
    public void setContentView(int layoutResID) {

        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        CircularRevealFrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
        mProgressBar = constraintLayout.findViewById(R.id.progress_bar);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        super.setContentView(constraintLayout);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(hasContactsPermission()){
            // do something?

        }
        else{
            askContactsPermission();
        }
    }

    public void showContactsDialog(ArrayList<Person> people){
        mChooseContactDialog = ChooseContactDialog.getInstance(people);
        mChooseContactDialog.show(getSupportFragmentManager(), getString(R.string.dialog_choose_contact));
    }

    public void showProgressBar(boolean visibility){
        mProgressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }

    public boolean hasContactsPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            else{
                return true;
            }
        }
        else{
            return true;
        }
    }

    public void askContactsPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSION_REQUEST_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CONTACTS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // do something?
            }
        }
    }


    public void addNewContact(){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra("finishActivityOnSaveCompleted", true); // Fix for 4.0.3 +
        startActivityForResult(intent, INSERT_NEW_CONTACT_CODE);
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public abstract void onDateSet(DatePicker view, int year, int month, int dayOfMonth);

    public RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar);
        options.circleCrop();
        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }
}









