package com.codingwithmitch.debttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codingwithmitch.debttracker.adapters.DebtRecyclerAdapter;
import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.DebtAndAllPayments;
import com.codingwithmitch.debttracker.models.Payment;
import com.codingwithmitch.debttracker.models.Person;
import com.codingwithmitch.debttracker.util.BigDecimalUtil;
import com.codingwithmitch.debttracker.util.LayoutBuilder;
import com.codingwithmitch.debttracker.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.debttracker.viewmodels.debts.DebtListViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.util.Calendar;


public class DebtListActivity extends BaseActivity implements
        View.OnClickListener,
        DebtRecyclerAdapter.OnDebtSelected
{

    private static final String TAG = "DebtListActivity";

    // ui
    private RecyclerView mRecyclerView;
    private TextView mToolbarTotal, mToolbarDebtHeader;
    private FloatingActionButton mFab;


    // vars
    private DebtListViewModel viewModel;
    private DebtRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_list);
        mRecyclerView = findViewById(R.id.recycler_view);
        mToolbarTotal = findViewById(R.id.toolbar_total);
        mFab = findViewById(R.id.add_new_debt);
        mToolbarDebtHeader = findViewById(R.id.toolbar_debt_header);

        viewModel = ViewModelProviders.of(this).get(DebtListViewModel.class);

        mFab.setOnClickListener(this);
        findViewById(R.id.filter_list_icon).setOnClickListener(this);

        initRecyclerView();
        initToolbar();
        initSearchView();
        subscribeObservers();
    }

    private void initRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DebtRecyclerAdapter(this, initGlide());
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(50);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called.");
        viewModel.retrieveAllDebtsAndPayments();
    }

    private void subscribeObservers(){
        viewModel.getDataSet().observe(this, listResource -> {
            if(listResource != null){
                if(listResource.data != null){
                    switch(listResource.status){
                        case LOADING:{
                            // loading the contacts...
                            Log.d(TAG, "subscribeObservers: loading debts from device...");
                            showProgressBar(true);
                            break;
                        }

                        case SUCCESS:{
                            Log.d(TAG, "subscribeObservers: DONE loading debts from device...");
                            showProgressBar(false);
                            mAdapter.setDebtsAndPayments(listResource.data);
                            if(viewModel.getSearchViewFilter().getValue() == null){
                                mAdapter.getFilter().filter("");
                            }
                            else{
                                mAdapter.getFilter().filter(viewModel.getSearchViewFilter().getValue());
                            }
                            break;
                        }

                        case ERROR:{
                            String errorMsg = listResource.message;
                            Log.e(TAG, "subscribeObservers: ERROR while loading debt data..." +  errorMsg );
                            Toast.makeText(DebtListActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            showProgressBar(false);
                            break;
                        }
                    }
                }
            }
        });

        viewModel.getToolbarTotal().observe(this, s -> {
            s = "$" + s;
            mToolbarTotal.setText(s);
            enableSettledToolbar(viewModel.getMyPreferenceManager().getShowOnlySettled());
        });

        viewModel.getSearchViewFilter().observe(this, s -> mAdapter.getFilter().filter(s));

        viewModel.getAppBarVerticalOffset().observe(this, appbarVerticalOffset -> {
            if(appbarVerticalOffset >= 0){
                showFab();
            }
            else{
                hideFab();
            }
        });
    }

    private void enableSettledToolbar(boolean isEnabled){
        if(isEnabled){
            mToolbarDebtHeader.setText("total settled:");
        }
        else{
            mToolbarDebtHeader.setText("total debt:");
        }
    }

    private void showFab(){
        mFab.show();
    }

    private void hideFab(){
        mFab.hide();
    }

    private void makePayment(DebtAndAllPayments debtAndAllPayments) {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal(BigDecimalUtil.getRemainingDebt(debtAndAllPayments)));
        payment.setDebt_id(debtAndAllPayments.debt.getId());
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
                        settleDebt(debtAndAllPayments.debt);
                        break;
                    }

                    case ERROR:{
                        String errorMsg = paymentResource.message;
                        Log.e(TAG, "makePayment: ERROR while inserting new payment..." +  errorMsg );
                        Toast.makeText(DebtListActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
    }

    private void settleDebt(Debt debt){
        viewModel.settleDebt(debt).observe(this, debtResource -> {
            if(debtResource != null){
                switch(debtResource.status){

                    case LOADING:{
                        Log.d(TAG, "settleDebt: settling debt... ");
                        break;
                    }

                    case SUCCESS:{
                        Log.d(TAG, "settleDebt: DONE settling debt...");
                        viewModel.retrieveAllDebtsAndPayments();
                        break;
                    }

                    case ERROR:{
                        String errorMsg = debtResource.message;
                        Log.e(TAG, "settleDebt: ERROR while settling debt..." +  errorMsg );
                        Toast.makeText(DebtListActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
    }


    private void showQuickSettleDialog(DebtAndAllPayments debtAndAllPayments){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Remaining debt: $" + BigDecimalUtil.getRemainingDebt(debtAndAllPayments));

        LinearLayout linearLayout = LayoutBuilder.buildDialogLayout(this);

        final TextView enterPaymentText = new TextView(this);
        enterPaymentText.setPadding(0, 0, 0, 20);
        String text = "Are you sure you want to settle this debt?";
        enterPaymentText.setText(text);
        enterPaymentText.setInputType(InputType.TYPE_CLASS_TEXT);
        enterPaymentText.setTextSize(17);
        linearLayout.addView(enterPaymentText);

        builder.setView(linearLayout);

        builder.setPositiveButton("YES", (dialog, which) -> {
            makePayment(debtAndAllPayments);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showFilterOptionsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Filters");

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);

        // settled debts switch preference
        final TextView settleSwitchTitle = new TextView(this);
        settleSwitchTitle.setText("Show settled debts");
        settleSwitchTitle.setTextSize(17);
        settleSwitchTitle.setTextColor(Color.BLACK);
        Switch settledSwitch = new Switch(this);
        settledSwitch.setLayoutParams(params);
        settledSwitch.setChecked(viewModel.getMyPreferenceManager().getShowOnlySettled());
        RelativeLayout relativeSettledSwitch = new RelativeLayout(this);
        relativeSettledSwitch.setPadding(0, 0, 0, 40);
        relativeSettledSwitch.addView(settleSwitchTitle);
        relativeSettledSwitch.addView(settledSwitch);

        // order of debts switch preference
        final TextView orderSwitchTitle = new TextView(this);
        orderSwitchTitle.setText("Order by newest to oldest");
        orderSwitchTitle.setTextSize(17);
        orderSwitchTitle.setTextColor(Color.BLACK);
        Switch orderSwitch = new Switch(this);
        orderSwitch.setLayoutParams(params);
        orderSwitch.setChecked(viewModel.getMyPreferenceManager().getOrderByNewest());
        RelativeLayout relativeOrderedSwitch = new RelativeLayout(this);
        relativeOrderedSwitch.setPadding(0, 0, 0, 40);
        relativeOrderedSwitch.addView(orderSwitchTitle);
        relativeOrderedSwitch.addView(orderSwitch);

        // orient the two relative layouts one above another
        LinearLayout linearLayout = LayoutBuilder.buildDialogLayout(this);
        linearLayout.addView(relativeSettledSwitch);
        linearLayout.addView(relativeOrderedSwitch);
        builder.setView(linearLayout);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            viewModel.getMyPreferenceManager().setOnlySettled(settledSwitch.isChecked());
            viewModel.getMyPreferenceManager().setOrderByNewest(orderSwitch.isChecked());
            viewModel.retrieveAllDebtsAndPayments();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void initToolbar(){
        setSupportActionBar(findViewById(R.id.toolbar));

        ((AppBarLayout)findViewById(R.id.appbar)).addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
                viewModel.setNewAppBarVerticalOffset(verticalOffset));
    }

    private void initSearchView(){
        ((SearchView)findViewById(R.id.searchview)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchViewFilter(newText);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_new_debt:{
                Intent intent = new Intent(this, DebtNewActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.filter_list_icon:{
                showFilterOptionsDialog();
                break;
            }
        }
    }

    @Override
    public void onDebtClicked(int position) {
        Log.d(TAG, "onDebtClicked: clicked a debt");
        Intent intent = new Intent(this, DebtUpdateActivity.class);
        intent.putExtra("debt", mAdapter.getDataSet().get(position).debt);
        startActivity(intent);
    }

    @Override
    public void onQuickSettle(int position) {
        Log.d(TAG, "onQuickSettle: settling debt.");
        showQuickSettleDialog(mAdapter.getDataSet().get(position));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // do nothing
    }

    @Override
    public void onPersonClicked(Person person) {
        // do nothing
    }

}

























