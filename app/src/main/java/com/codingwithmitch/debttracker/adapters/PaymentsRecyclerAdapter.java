package com.codingwithmitch.debttracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingwithmitch.debttracker.R;
import com.codingwithmitch.debttracker.models.Payment;
import com.codingwithmitch.debttracker.util.BigDecimalUtil;
import com.codingwithmitch.debttracker.util.DateConverterUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentsRecyclerAdapter extends RecyclerView.Adapter<PaymentsRecyclerAdapter.PaymentViewHolder> {

    private OnPaymentListener onPaymentListener;
    private List<Payment> payments = new ArrayList<>();

    public PaymentsRecyclerAdapter(OnPaymentListener onPaymentListener) {
        this.onPaymentListener = onPaymentListener;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_payment_list_item, parent, false);
        return new PaymentViewHolder(view, onPaymentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        holder.onBind(payments.get(position));
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public void setPayments(List<Payment> payments){
        this.payments = payments;
        notifyDataSetChanged();
    }

    public List<Payment> getPayments(){
        return payments;
    }

    public void removePayment(Payment payment){
        payments.remove(payment);
        notifyDataSetChanged();
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnPaymentListener onPaymentListener;
        TextView paymentAmount, paymentDate;

        public PaymentViewHolder(@NonNull View itemView, OnPaymentListener onPaymentListener) {
            super(itemView);
            this.onPaymentListener = onPaymentListener;
            itemView.setOnClickListener(this);

            paymentAmount = itemView.findViewById(R.id.payment_amount);
            paymentDate = itemView.findViewById(R.id.payment_date);
        }

        public void onBind(Payment payment){
            String payment_amount = "$" + BigDecimalUtil.getValue(payment.getAmount());
            paymentAmount.setText(payment_amount);

            paymentDate.setText(DateConverterUtil.getStringFormattedDate(payment.getTimestamp()));
        }

        @Override
        public void onClick(View v) {
            onPaymentListener.onPaymentClick(getAdapterPosition());
        }
    }

    public interface OnPaymentListener{
        void onPaymentClick(int position);
    }
}



























