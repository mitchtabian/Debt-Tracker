package com.codingwithmitch.debttracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.codingwithmitch.debttracker.R;
import com.codingwithmitch.debttracker.models.DebtAndAllPayments;
import com.codingwithmitch.debttracker.models.Payment;
import com.codingwithmitch.debttracker.models.PaymentAmount;
import com.codingwithmitch.debttracker.util.BigDecimalUtil;
import com.codingwithmitch.debttracker.util.DateConverterUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DebtRecyclerAdapter extends RecyclerView.Adapter<DebtRecyclerAdapter.DebtViewHolder> {

//    private List<Debt> debts = new ArrayList<>();
    private List<DebtAndAllPayments> debtsAndPayments = new ArrayList<>();
    private OnDebtSelected onDebtSelected;
    private RequestManager requestManager;

    public DebtRecyclerAdapter(OnDebtSelected onDebtSelected, RequestManager requestManager) {
        this.onDebtSelected = onDebtSelected;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public DebtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_debt_list_item, parent, false);
        return new DebtViewHolder(view, onDebtSelected, requestManager);
    }

    @Override
    public void onBindViewHolder(@NonNull DebtViewHolder holder, int position) {
//        ((DebtViewHolder)holder).onBind(debts.get(position));
        ((DebtViewHolder)holder).onBind(debtsAndPayments.get(position));
    }

    @Override
    public int getItemCount() {
//        return debts.size();
        return debtsAndPayments.size();
    }

//    public void setDebts(List<Debt> debts){
//        this.debts = debts;
//        notifyDataSetChanged();
//    }

    public void setDebtsAndPayments(List<DebtAndAllPayments> data){
        this.debtsAndPayments = data;
        notifyDataSetChanged();
    }

    public class DebtViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView personPortrait;
        ImageButton quickSettle;
        TextView name, lendingDate, debtAmount;
        OnDebtSelected onDebtSelected;
        RequestManager requestManager;

        public DebtViewHolder(@NonNull View itemView, OnDebtSelected onDebtSelected, RequestManager requestManager) {
            super(itemView);
            personPortrait = itemView.findViewById(R.id.person_portrait);
            quickSettle = itemView.findViewById(R.id.quick_settle);
            name = itemView.findViewById(R.id.person_name);
            debtAmount = itemView.findViewById(R.id.debt_amount);
            lendingDate = itemView.findViewById(R.id.lending_date);

            this.onDebtSelected = onDebtSelected;
            this.requestManager = requestManager;

            itemView.setOnClickListener(this);
            quickSettle.setOnClickListener(this);
        }

//        public void onBind(Debt debt){
//
//            requestManager
//                    .load(debt.getPerson().getPhoto_uri())
//                    .into(personPortrait);
//
//            name.setText(debt.getPerson().getName());
//            lendingDate.setText(DateConverterUtil.getStringFormattedDate(debt.getLending_date()));
//
//            String amount = "$" + debt.getDebt_amount().toString();
//            debtAmount.setText(amount);
//
//            if(!debt.getIs_settled()){
//                quickSettle.setVisibility(View.VISIBLE);
//            }
//            else{
//                quickSettle.setVisibility(View.GONE);
//            }
//        }

        public void onBind(DebtAndAllPayments data){

            requestManager
                    .load(data.debt.getPerson().getPhoto_uri())
                    .into(personPortrait);

            name.setText(data.debt.getPerson().getName());
            lendingDate.setText(DateConverterUtil.getStringFormattedDate(data.debt.getLending_date()));

            double totalPayments = 0;
            for(PaymentAmount amount: data.payments){
                totalPayments = totalPayments + amount.amount.doubleValue();
            }
            String debtRemaining = "$" + BigDecimalUtil.getValue(new BigDecimal(data.debt.getDebt_amount().doubleValue() - totalPayments));
            debtAmount.setText(debtRemaining);

            if(!data.debt.getIs_settled()){
                quickSettle.setVisibility(View.VISIBLE);
            }
            else{
                quickSettle.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.quick_settle:{
                    onDebtSelected.onQuickSettle(getAdapterPosition());
                    break;
                }

                default:{
                    onDebtSelected.onDebtClicked(getAdapterPosition());
                    break;
                }
            }

        }
    }

    public interface OnDebtSelected{
        void onDebtClicked(int position);
        void onQuickSettle(int position);
    }
}
