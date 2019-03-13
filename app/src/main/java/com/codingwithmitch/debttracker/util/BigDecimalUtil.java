package com.codingwithmitch.debttracker.util;

import com.codingwithmitch.debttracker.models.DebtAndAllPayments;
import com.codingwithmitch.debttracker.models.PaymentAmount;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalUtil {

    public static String getValue(BigDecimal value){
        DecimalFormat df = new DecimalFormat("#########.00");
        return String.valueOf(df.format(value));
    }
    /*
        For rating bar (actual rating)
     */
    public static float getFloat(BigDecimal value){
        return value.floatValue();
    }

    public static String getRemainingDebt(DebtAndAllPayments data){
        double totalPayments = 0;
        for(PaymentAmount amount: data.payments){
            totalPayments = totalPayments + amount.amount.doubleValue();
        }
        return BigDecimalUtil.getValue(new BigDecimal(data.debt.getDebt_amount().doubleValue() - totalPayments));
    }
}