package com.example.CurrencyExchange.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountRequest {
    @NotBlank(message = "First name can't be empty")
    private String firstName;

    @NotBlank(message = "Last name can't be empty")
    private String lastName;

    @NotNull(message = "Balance must be present")
    @Min(message = "Minimal balance value must be 1", value = 1)
    private BigDecimal plnBalance;
}
