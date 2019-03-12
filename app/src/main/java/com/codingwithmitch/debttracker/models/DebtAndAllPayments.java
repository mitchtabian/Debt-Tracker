package com.codingwithmitch.debttracker.models;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class DebtAndAllPayments{
    @Embedded
    public Debt debt;
    @Relation(parentColumn = "id", entityColumn = "debt_id", entity = Payment.class)
    public List<PaymentAmount> payments;
}