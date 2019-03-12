package com.codingwithmitch.debttracker;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.codingwithmitch.debttracker.adapters.DebtRecyclerAdapter;
import com.codingwithmitch.debttracker.models.Debt;
import com.codingwithmitch.debttracker.models.Person;
import com.codingwithmitch.debttracker.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.debttracker.viewmodels.debts.DebtListViewModel;



public class DebtListActivity extends BaseActivity implements
        View.OnClickListener,
        DebtRecyclerAdapter.OnDebtSelected
{

    private static final String TAG = "DebtListActivity";

    // ui
    private RecyclerView mRecyclerView;
    private TextView mTotalDebt;


    // vars
    private DebtListViewModel viewModel;
    private DebtRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_list);
        mRecyclerView = findViewById(R.id.recycler_view);
        mTotalDebt = findViewById(R.id.total_debt);

        viewModel = ViewModelProviders.of(this).get(DebtListViewModel.class);

        findViewById(R.id.add_new_debt).setOnClickListener(this);

        initRecyclerView();
        initToolbar();
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
//        viewModel.retrieveDebts();
        viewModel.retrieveAllDebtsAndPayments();
    }

    private void subscribeObservers(){
//        viewModel.getDebts().observe(this, listResource -> {
//            if(listResource != null){
//                if(listResource.data != null){
//                    switch(listResource.status){
//                        case LOADING:{
//                            // loading the contacts...
//                            Log.d(TAG, "subscribeObservers: loading debts from device...");
//                            showProgressBar(true);
//                            break;
//                        }
//
//                        case SUCCESS:{
//                            Log.d(TAG, "subscribeObservers: DONE loading debts from device...");
//
//                            showProgressBar(false);
//                            for(Debt debt: listResource.data){
//                                Log.d(TAG, "onChanged: " + debt.getPerson().getName() + ", " + debt.getDebt_amount() + ", " + debt.getLending_date());
//                            }
//                            mAdapter.setDebts(listResource.data);
//                            break;
//                        }
//
//                        case ERROR:{
//                            String errorMsg = listResource.message;
//                            Log.e(TAG, "subscribeObservers: ERROR while loading debt data..." +  errorMsg );
//                            Toast.makeText(DebtListActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
//                            showProgressBar(false);
//                            break;
//                        }
//                    }
//                }
//            }
//        });

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

        viewModel.getTotalDebt().observe(this, s -> {
            s = "$" + s;
            mTotalDebt.setText(s);
        });
    }

    private void initToolbar(){
        setSupportActionBar(findViewById(R.id.toolbar));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_new_debt:{
                Intent intent = new Intent(this, DebtNewActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onDebtClicked(int position) {
        Log.d(TAG, "onDebtClicked: clicked a debt");
        Intent intent = new Intent(this, DebtUpdateActivity.class);
//        intent.putExtra("debt", viewModel.getDebts().getValue().data.get(position));
        intent.putExtra("debt", viewModel.getDataSet().getValue().data.get(position).debt);
        startActivity(intent);
    }

    @Override
    public void onQuickSettle(int position) {
        Log.d(TAG, "onQuickSettle: settling debt.");
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

























