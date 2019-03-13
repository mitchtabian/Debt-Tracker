package com.codingwithmitch.debttracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codingwithmitch.debttracker.adapters.PaymentsRecyclerAdapter;
import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.Payment;
import com.codingwithmitch.debttracker.models.Person;
import com.codingwithmitch.debttracker.util.BigDecimalUtil;
import com.codingwithmitch.debttracker.util.DateConverterUtil;
import com.codingwithmitch.debttracker.util.DecimalDigitsInputFilter;
import com.codingwithmitch.debttracker.util.LayoutBuilder;
import com.codingwithmitch.debttracker.viewmodels.debts.DebtUpdateViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.codingwithmitch.debttracker.util.Constants.INSERT_NEW_CONTACT_CODE;


public class DebtUpdateActivity extends BaseActivity implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        PaymentsRecyclerAdapter.OnPaymentListener
{

    private static final String TAG = "DebtUpdateActivity";

    // ui
    private TextView mSelectedLendingDate, mInitialAmount, mSelectedContact,
            mRemainingAmount, mStatus;
    private EditText mDescription;
    private ImageButton mStatusIcon;
    private RecyclerView mRecyclerView;
    private PaymentsRecyclerAdapter mAdapter;


    // vars
    private DebtUpdateViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_debt);
        mSelectedContact = findViewById(R.id.selected_contact);
        mSelectedLendingDate = findViewById(R.id.selected_lending_date);
        mInitialAmount = findViewById(R.id.initial_cost);
        mRemainingAmount = findViewById(R.id.remaining_cost);
        mRecyclerView = findViewById(R.id.payments_recyclerview);
        mStatus = findViewById(R.id.debt_status);
        mStatusIcon = findViewById(R.id.debt_status_icon);
        mDescription = findViewById(R.id.debt_description);

        findViewById(R.id.lending_date_selector).setOnClickListener(this);
        findViewById(R.id.add_payment).setOnClickListener(this);
        findViewById(R.id.add_contact).setOnClickListener(this);
        mSelectedContact.setOnClickListener(this);
        mSelectedLendingDate.setOnClickListener(this);

        viewModel = ViewModelProviders.of(this).get(DebtUpdateViewModel.class);

        getIncomingIntent();
        initToolbar();
        initRecyclerView();
        subscribeObservers();
        retrieveContacts();
    }

    private void initRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PaymentsRecyclerAdapter(this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        // set scroll to top when done loading the payments
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                findViewById(R.id.scrollview).scrollTo(0,0);
            }
        });
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("debt")){
            Debt debt = getIntent().getParcelableExtra("debt");
            Log.d(TAG, "getIncomingIntent: " + debt.toString());
            viewModel.setDebt(debt);
        }
    }

    private void retrieveContacts(){
        viewModel.retrieveContacts(getContentResolver());
    }

    private void retrieveNewContact(Uri uri){
        viewModel.retrieveNewlyAddedContact(getContentResolver(), uri);
    }

    private void subscribeObservers(){
        viewModel.getContactsList().observe(this, listResource -> {
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
                        mSelectedContact.setText(listResource.data.get(0).getName());
                    }
                    break;
                }

                case ERROR:{
                    String errorMsg = listResource.message;
                    Log.e(TAG, "subscribeObservers: ERROR while loading contacts from device..." +  errorMsg );
                    Toast.makeText(DebtUpdateActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    showProgressBar(false);
                    break;
                }
            }
        });

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

        viewModel.getDebt().observe(this, debt -> {
            setDebtProperties(debt);
            updateDebt();
            setRemainingAmount();
        });

        viewModel.getPayments(viewModel.getDebt().getValue()).observe(this, listResource -> {
            if(listResource != null){
                switch(listResource.status){

                    case LOADING:{
                        Log.d(TAG, "subscribeObservers: LOADING... attempting to get payments...");
                        break;
                    }

                    case SUCCESS:{
                        Log.d(TAG, "subscribeObservers: setting payments.");
                        viewModel.setRepayedAmount(listResource.data);
                        mAdapter.setPayments(listResource.data);
                        setRemainingAmount();
                        break;
                    }

                    case ERROR:{
                        String errorMsg = listResource.message;
                        Log.e(TAG, "subscribeObservers: ERROR getting payments: " + errorMsg );
                        break;
                    }
                }
            }
        });
    }

    private void setRemainingAmount(){
        if(viewModel.isDebtSettled()){
            settleDebt();
        }
        else{
            reverseSettleDebt();
            updateUI(false);
        }
        String remainingAmount = "$" + viewModel.getRemainingAmount();
        mRemainingAmount.setText(remainingAmount);
    }

    private void settleDebt(){
        viewModel.settleDebt().observe(this, debtResource -> {
            if(debtResource != null) {
                switch (debtResource.status) {
                    case LOADING: {
                        Log.d(TAG, "settleDebt: LOADING... settling debt...");
                        break;
                    }

                    case SUCCESS: {
                        Log.d(TAG, "settleDebt: SUCCESS. Debt has been settled.");
                        updateUI(true);
                        break;
                    }

                    case ERROR: {
                        String errorMsg = debtResource.message;
                        Log.e(TAG, "settleDebt: ERROR setting debt. " + errorMsg);
                        break;
                    }
                }
            }
        });
    }

    private void reverseSettleDebt(){
        viewModel.reverseSettleDebt().observe(this, debtResource -> {
            if(debtResource != null) {
                switch (debtResource.status) {
                    case LOADING: {
                        Log.d(TAG, "reverseSettleDebt: LOADING... Reversing debt settle...");
                        break;
                    }

                    case SUCCESS: {
                        Log.d(TAG, "reverseSettleDebt: SUCCESS. Debt is no longer settled.");
                        updateUI(false);
                        break;
                    }

                    case ERROR: {
                        String errorMsg = debtResource.message;
                        Log.e(TAG, "reverseSettleDebt: ERROR. " + errorMsg);
                        break;
                    }
                }
            }
        });
    }

    private void updateUI(boolean isSettled){
        if(isSettled){
            findViewById(R.id.add_payment).setOnClickListener(null);
            findViewById(R.id.add_payment).setBackground(ContextCompat.getDrawable(this, R.drawable.grey_rounded_button));
            mStatusIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_check_green_24dp));
            mStatus.setText("Settled");
        }
        else{
            findViewById(R.id.add_payment).setOnClickListener(this);
            findViewById(R.id.add_payment).setBackground(ContextCompat.getDrawable(this, R.drawable.orange_rounded_button));
            mStatusIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_close_red_24dp));
            mStatus.setText("Due");
        }
    }


    private void makePayment(BigDecimal paymentAmount) {
        Payment payment = new Payment();
        payment.setAmount(paymentAmount);
        payment.setDebt_id(viewModel.getDebt().getValue().getId());
        payment.setTimestamp(Calendar.getInstance().getTime());
        viewModel.insertNewPayment(payment).observe(this, paymentResource -> {
            if(paymentResource != null){
                switch(paymentResource.status){

                    case LOADING:{
                        Log.d(TAG, "makePayment: inserting new payment...");
                        break;
                    }

                    case SUCCESS:{
                        Log.d(TAG, "makePayment: DONE inserting new payment...");
                        break;
                    }

                    case ERROR:{
                        String errorMsg = paymentResource.message;
                        Log.e(TAG, "makePayment: ERROR while inserting new payment..." +  errorMsg );
                        Toast.makeText(DebtUpdateActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
    }

    private void removePayment(Payment payment){
        viewModel.removePayment(payment).observe(DebtUpdateActivity.this, paymentResource -> {
            if(paymentResource != null){
                switch(paymentResource.status){

                    case LOADING:{
                        Log.d(TAG, "removePayment: removing a payment...");
                        break;
                    }

                    case SUCCESS:{
                        Log.d(TAG, "removePayment: DONE removing a payment...");
                        if(!viewModel.isDebtSettled()){
                            reverseSettleDebt();
                        }
                        break;
                    }

                    case ERROR:{
                        String errorMsg = paymentResource.message;
                        Log.e(TAG, "removePayment: ERROR while removing a payment..." +  errorMsg );
                        Toast.makeText(DebtUpdateActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
    }

    private void setDebtProperties(Debt debt){
        viewModel.setSelectedDate(debt.getLending_date());
        viewModel.setSelectedPerson(debt.getPerson());

        String initialDebtAmount = "$" + BigDecimalUtil.getValue(debt.getDebt_amount());
        mInitialAmount.setText(initialDebtAmount);
        mDescription.setText((debt.getDescription().equals("") ? " " : debt.getDescription()));
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

    private void showMakePaymentDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a Payment");

        LinearLayout linearLayout = LayoutBuilder.buildDialogLayout(this);

        final TextView enterPaymentText = new TextView(this);
        enterPaymentText.setPadding(0, 0, 0, 20);
        String text = "Enter a payment amount:";
        enterPaymentText.setText(text);
        enterPaymentText.setInputType(InputType.TYPE_CLASS_TEXT);
        linearLayout.addView(enterPaymentText);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
        linearLayout.addView(input);

        builder.setView(linearLayout);

        builder.setPositiveButton("SUBMIT", (dialog, which) -> {

            BigDecimal bd = new BigDecimal(input.getText().toString());
            makePayment(bd);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showEditInitialAmountDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Initial Amount");

        LinearLayout linearLayout = LayoutBuilder.buildDialogLayout(this);

        final TextView currentAmountView = new TextView(this);
        currentAmountView.setPadding(0, 0, 0, 20);
        String currentAmount = "Current initial debt: " + mInitialAmount.getText().toString();
        currentAmountView.setText(currentAmount);
        currentAmountView.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayout.addView(currentAmountView);

        final TextView newDebtHeading = new TextView(this);
        newDebtHeading.setPadding(0, 0, 0, 20);
        newDebtHeading.setText("New initial debt:");
        newDebtHeading.setInputType(InputType.TYPE_CLASS_TEXT);
        linearLayout.addView(newDebtHeading);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
        linearLayout.addView(input);

        builder.setView(linearLayout);

        builder.setPositiveButton("UPDATE", (dialog, which) -> {

            BigDecimal bd = new BigDecimal(Double.parseDouble((input.getText().toString().equals("") ? "0.00" : input.getText().toString())));
            String newAmount = "$" + BigDecimalUtil.getValue(bd);
            Log.d(TAG, "onClick: new Amount: " + newAmount);
            mInitialAmount.setText(newAmount);

            Debt debt =  viewModel.getDebt().getValue();
            debt.setDebt_amount(bd);
            viewModel.setDebt(debt);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateDebt(){
        viewModel.updateDebt();
    }

    private void initToolbar(){
        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Edit Debt");
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

            case R.id.action_edit: {
                // Edit the initial amount
                showEditInitialAmountDialog();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_debt_menu, menu);
        return super.onCreateOptionsMenu(menu);
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

            case R.id.add_payment:{
                showMakePaymentDialog();
                break;
            }

            case R.id.add_contact:{
                addNewContact();
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
        Debt debt = viewModel.getDebt().getValue();
        debt.setLending_date(selectedDate);
        viewModel.setDebt(debt);
    }

    @Override
    public void onPersonClicked(Person person) {
        viewModel.setSelectedPerson(person);
        Debt debt = viewModel.getDebt().getValue();
        debt.setPerson(person);
        viewModel.setDebt(debt);
    }

    @Override
    public void onPaymentClick(int position) {
        Log.d(TAG, "onPaymentClick: clicked a payment.");
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            removePayment(mAdapter.getPayments().get(viewHolder.getAdapterPosition()));
            mAdapter.removePayment(mAdapter.getPayments().get(viewHolder.getAdapterPosition()));
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        // update the description
        if(!viewModel.getDebt().getValue().getDescription().equals(mDescription.getText().toString())){
            Debt debt = viewModel.getDebt().getValue();
            debt.setDescription(mDescription.getText().toString());
            viewModel.setDebt(debt);
        }
    }
}


















