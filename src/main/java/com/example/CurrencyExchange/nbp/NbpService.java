package com.example.CurrencyExchange.nbp;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service

public class NbpService {
    @Value("${nbp-api.url}")
    private String url;

    public Rates getRate(){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<RatesList> result = restTemplate.getForEntity(url, RatesList.class);
        return Objects.requireNonNull(result.getBody(), "Could not receive rates from NBP API")
                .getRates(0);
    }
}
