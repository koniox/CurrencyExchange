package com.example.CurrencyExchange.nbp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RatesList {
    List<Rates> rates;

    public RatesList(){
        rates = new ArrayList<>();
    }

    public Rates getRates(int index){
        return rates.get(index);
    }
}
