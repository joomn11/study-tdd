package com.example.chap02;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayDate {

    private LocalDate billingDate;
    private int payAmount;
    private LocalDate firstBillingDate;
}
