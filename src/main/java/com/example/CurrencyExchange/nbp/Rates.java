package com.example.CurrencyExchange.nbp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rates {
    private BigDecimal bid;
    private BigDecimal ask;
}
