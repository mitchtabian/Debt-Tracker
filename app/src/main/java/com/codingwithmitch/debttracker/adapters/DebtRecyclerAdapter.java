package com.codingwithmitch.debttracker.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.codingwithmitch.debttracker.R;
import com.codingwithmitch.debttracker.models.DebtAndAllPayments;
import com.codingwithmitch.debttracker.util.BigDecimalUtil;
import com.codingwithmitch.debttracker.util.DateConverterUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DebtRecyclerAdapter extends RecyclerView.Adapter<DebtRecyclerAdapter.DebtViewHolder> implements Filterable {

    private static final String TAG = "DebtRecyclerAdapter";
    private List<DebtAndAllPayments> filteredDebtsAndPayments = new ArrayList<>();
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
        ((DebtViewHolder)holder).onBind(filteredDebtsAndPayments.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredDebtsAndPayments.size();
    }

    public void setDebtsAndPayments(List<DebtAndAllPayments> data){
        this.debtsAndPayments = data;
        notifyDataSetChanged();
    }

    public List<DebtAndAllPayments> getDataSet(){
        return filteredDebtsAndPayments;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d(TAG, "performFiltering: called.");
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredDebtsAndPayments = debtsAndPayments;
                } else {
                    List<DebtAndAllPayments> filteredList = new ArrayList<>();
                    for (DebtAndAllPayments data : debtsAndPayments) {
                        if (data.debt.getPerson().getName().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(data);
                        }
                        filteredDebtsAndPayments = filteredList;
                    }

                }
                FilterResults results = new FilterResults();
                results.values = filteredDebtsAndPayments;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.d(TAG, "publishResults: called.");
                filteredDebtsAndPayments = (List<DebtAndAllPayments>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class DebtViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView personPortrait;
        ImageButton quickSettle;
        TextView name, lendingDate, debtAmount, debtSettled;
        OnDebtSelected onDebtSelected;
        RequestManager requestManager;

        public DebtViewHolder(@NonNull View itemView, OnDebtSelected onDebtSelected, RequestManager requestManager) {
            super(itemView);
            personPortrait = itemView.findViewById(R.id.person_portrait);
            quickSettle = itemView.findViewById(R.id.quick_settle);
            name = itemView.findViewById(R.id.person_name);
            debtAmount = itemView.findViewById(R.id.debt_amount);
            lendingDate = itemView.findViewById(R.id.lending_date);
            debtSettled = itemView.findViewById(R.id.debt_settled);

            this.onDebtSelected = onDebtSelected;
            this.requestManager = requestManager;

            itemView.setOnClickListener(this);
            quickSettle.setOnClickListener(this);
        }

        public void onBind(DebtAndAllPayments data){

            requestManager
                    .load(data.debt.getPerson().getPhoto_uri())
                    .into(personPortrait);

            name.setText(data.debt.getPerson().getName());
            lendingDate.setText(DateConverterUtil.getStringFormattedDate(data.debt.getLending_date()));

            if((new BigDecimal(BigDecimalUtil.getRemainingDebt(data))).doubleValue() > 0){
                String debtRemaining = "$" + BigDecimalUtil.getRemainingDebt(data);
                debtAmount.setText(debtRemaining);
            }
            else{
                String settledString = "$" + BigDecimalUtil.getValue(data.debt.getDebt_amount());
                debtAmount.setText(settledString);
            }


            if(!data.debt.getIs_settled()){
                quickSettle.setVisibility(View.VISIBLE);
                debtSettled.setVisibility(View.GONE);
            }
            else{
                quickSettle.setVisibility(View.GONE);
                debtSettled.setVisibility(View.VISIBLE);
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
