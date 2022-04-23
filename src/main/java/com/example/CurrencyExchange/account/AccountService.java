package com.example.CurrencyExchange.account;

import com.example.CurrencyExchange.exception.AccountNotFoundException;
import com.example.CurrencyExchange.exception.InsufficientBalanceException;
import com.example.CurrencyExchange.nbp.NbpService;
import com.example.CurrencyExchange.wallet.CurrencyUnit;
import com.example.CurrencyExchange.wallet.Wallet;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.MathContext;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final NbpService nbpService;

    public AccountService(AccountRepository accountRepository, NbpService nbpService) {
        this.accountRepository = accountRepository;
        this.nbpService = nbpService;
    }

    public Account getAccount(Integer id){
        Account account = accountRepository.getAccountById(id);
        if(account == null){
            throw new AccountNotFoundException("Account with id " + id + " does not exist");
        }
        return account;
    }

    public Account createAccount(AccountRequest accountRequest){
        Account account = new Account(accountRequest.getFirstName(), accountRequest.getLastName());
        Wallet plnWallet = new Wallet(accountRequest.getPlnBalance(), CurrencyUnit.PLN);
        account.addWallet(plnWallet);
        Wallet usdWallet = new Wallet(new BigDecimal(0), CurrencyUnit.USD);
        account.addWallet(usdWallet);
        return accountRepository.save(account);
    }

    public BigDecimal exchangeToUsd(Integer id, BigDecimal amountToExchange) {
        Account account = accountRepository.getAccountById(id);
        if(account == null){
            throw new AccountNotFoundException("Account with id " + id + " not found");
        }
        Wallet plnWallet = getWallet(account,CurrencyUnit.PLN);
        if(plnWallet
                .getBalance()
                .compareTo(amountToExchange) < 0){
            throw new InsufficientBalanceException("Not enough balance");
        }

        plnWallet.setBalance(plnWallet.getBalance().subtract(amountToExchange));
        Wallet usdWallet = getWallet(account,CurrencyUnit.USD);
        final BigDecimal exchangedAmount = amountToExchange.divide(nbpService.getRate().getAsk(),
                                                                   new MathContext(4));
        usdWallet.setBalance(usdWallet
                                    .getBalance()
                                    .add(exchangedAmount));
        accountRepository.save(account);

        return exchangedAmount;
    }

    public BigDecimal exchangeToPln(Integer id, BigDecimal amountToExchange) {
        Account account = accountRepository.getAccountById(id);
        if(account == null){
            throw new AccountNotFoundException("Account with id " + id + " not found");
        }
        Wallet usdWallet = getWallet(account,CurrencyUnit.USD);
        if(usdWallet
                    .getBalance()
                    .compareTo(amountToExchange) < 0){
            throw new InsufficientBalanceException("Not enough balance");
        }

        usdWallet.setBalance(usdWallet.getBalance().subtract(amountToExchange));
        Wallet plnWallet = getWallet(account,CurrencyUnit.PLN);
        final BigDecimal exchangedAmount = amountToExchange.multiply(nbpService.getRate().getBid(),
                                                                     new MathContext(4));
        plnWallet.setBalance(plnWallet
                                    .getBalance()
                                    .add(exchangedAmount));
        accountRepository.save(account);

        return exchangedAmount;
    }

    private Wallet getWallet(Account account, CurrencyUnit currencyUnit){
        return account
                .getWallets()
                .stream()
                .filter(wallet -> wallet.getCurrencyUnit().equals(currencyUnit))
                .findFirst()
                .orElse(null);
    }
}
