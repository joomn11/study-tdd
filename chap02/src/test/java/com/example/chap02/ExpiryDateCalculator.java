package com.example.chap02;

import java.time.LocalDate;
import java.time.YearMonth;

public class ExpiryDateCalculator {

    public LocalDate calculateExpiryDate(PayDate payDate) {

        int addedMonths = payDate.getPayAmount() / 10_000;

        if (payDate.getFirstBillingDate() != null) {
            return expiryDateUsingFirstBillingDate(payDate, addedMonths);
        } else {
            return payDate.getBillingDate().plusMonths(addedMonths);
        }
    }

    private LocalDate expiryDateUsingFirstBillingDate(PayDate payDate, int addedMonths) {
        LocalDate candidateExp = payDate.getBillingDate().plusMonths(addedMonths);

        if (!isSameDayOfMonth(payDate.getFirstBillingDate(), candidateExp)) {

            final int dayLenOfCandiMon = lastDayOfMonth(candidateExp);
            int dayOfFirstBilling = payDate.getFirstBillingDate().getDayOfMonth();

            if (dayLenOfCandiMon < dayOfFirstBilling) {
                return candidateExp.withDayOfMonth(dayLenOfCandiMon);
            }

            return candidateExp.withDayOfMonth(dayOfFirstBilling);
        } else {
            return candidateExp;
        }
    }

    private int lastDayOfMonth(LocalDate candidateExp) {
        return YearMonth.from(candidateExp).lengthOfMonth();
    }

    private boolean isSameDayOfMonth(LocalDate firstBillingDate, LocalDate candidateExp) {
        return firstBillingDate.getDayOfMonth() == candidateExp.getDayOfMonth();
    }
}
