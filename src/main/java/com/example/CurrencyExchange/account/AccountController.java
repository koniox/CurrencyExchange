package com.example.CurrencyExchange.account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/account")
@Validated
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable("id") Integer id){
        return new ResponseEntity<>(accountService.getAccount(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody @Valid AccountRequest accountRequest){
        Account newAccount = accountService.createAccount(accountRequest);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/to-usd")
    public ResponseEntity<?> exchangePlnToUsd(@PathVariable("id") Integer id,
                                              @RequestParam BigDecimal amountToExchange){
        return new ResponseEntity<>("Successfully exchanged " + amountToExchange + "PLN to "
                + accountService.exchangeToUsd(id, amountToExchange).toString() + "USD", HttpStatus.OK);
    }

    @PutMapping("/{id}/to-pln")
    public ResponseEntity<?> exchangeUsdToPln(@PathVariable("id") Integer id,
                                              @RequestParam BigDecimal amountToExchange) {
        return new ResponseEntity<>("Successfully exchanged " + amountToExchange + "USD to "
                + accountService.exchangeToPln(id, amountToExchange).toString() + "PLN", HttpStatus.OK);
    }
}
