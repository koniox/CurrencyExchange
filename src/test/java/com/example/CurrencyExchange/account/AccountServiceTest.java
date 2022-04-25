package com.example.CurrencyExchange.account;

import com.example.CurrencyExchange.exception.AccountNotFoundException;
import com.example.CurrencyExchange.exception.InsufficientBalanceException;
import com.example.CurrencyExchange.nbp.NbpService;
import com.example.CurrencyExchange.nbp.Rates;
import com.example.CurrencyExchange.wallet.CurrencyUnit;
import com.example.CurrencyExchange.wallet.Wallet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private NbpService nbpService;
    private AccountService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AccountService(accountRepository, nbpService);
    }

    @Test
    void shouldReturnExistingAccount() {
        //given
        Account account = new Account("John", "Doe");
        //when
        when(accountRepository.getAccountById(account.getId())).thenReturn(account);
        Account expected = underTest.getAccount(account.getId());
        //then
        assertThat(expected).isSameAs(account);
    }

    @Test
    void shouldThrowExceptionForNonExistingAccount(){
        //given
        Account account = new Account("John", "Doe");
        given(accountRepository.getAccountById(account.getId()))
                .willReturn(null);
        //when
        //then
        assertThatThrownBy(()-> underTest.getAccount(account.getId()))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account with id " + account.getId() + " does not exist");

    }

    @Test
    void createAccount() {
        //given
        AccountRequest accountRequest = new AccountRequest("John", "Doe", new BigDecimal(5000));
        //when
        underTest.createAccount(accountRequest);
        //then
        verify(accountRepository, only()).save(any());
    }

    @Test
    void exchangeToUsdShouldThrowExceptionForNonExistingAccount() {
        //given
        Account account = new Account("John", "Doe");
        given(accountRepository.getAccountById(account.getId()))
                .willReturn(null);
        //when
        //then
        assertThatThrownBy(()-> underTest.exchangeToUsd(account.getId(),new BigDecimal(1)))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void exchangeToUsdShouldThrowExceptionForInsufficientBalance() {
        //given
        Account account = new Account("John", "Doe");
        account.setWallets(List.of(
                new Wallet(new BigDecimal(5), CurrencyUnit.PLN),
                new Wallet(new BigDecimal(0), CurrencyUnit.USD)
        ));
        //when
        when(accountRepository.getAccountById(account.getId())).thenReturn(account);
        //then
        assertThatThrownBy(()-> underTest.exchangeToUsd(account.getId(),new BigDecimal(55)))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void exchangeToUsdShouldWorkAndSaveChanges() {
        //given
        Account account = new Account("John", "Doe");
        account.setWallets(List.of(
                new Wallet(new BigDecimal(5), CurrencyUnit.PLN),
                new Wallet(new BigDecimal(0), CurrencyUnit.USD)
        ));
        //when
        when(accountRepository.getAccountById(account.getId())).thenReturn(account);
        when(nbpService.getRate()).thenReturn(new Rates(new BigDecimal(1), new BigDecimal(1)));
        underTest.exchangeToUsd(account.getId(),new BigDecimal(1));
        //then
        verify(accountRepository, atLeastOnce()).save(any());
    }

    @Test
    void exchangeToPlnShouldThrowExceptionForNonExistingAccount() {
        //given
        Account account = new Account("John", "Doe");
        given(accountRepository.getAccountById(account.getId()))
                .willReturn(null);
        //when
        //then
        assertThatThrownBy(()-> underTest.exchangeToPln(account.getId(),new BigDecimal(1)))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void exchangeToPlnShouldThrowExceptionForInsufficientBalance() {
        //given
        Account account = new Account("John", "Doe");
        account.setWallets(List.of(
                new Wallet(new BigDecimal(5), CurrencyUnit.PLN),
                new Wallet(new BigDecimal(0), CurrencyUnit.USD)
        ));
        //when
        when(accountRepository.getAccountById(account.getId())).thenReturn(account);
        //then
        assertThatThrownBy(()-> underTest.exchangeToPln(account.getId(),new BigDecimal(55)))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void exchangeToPlnShouldWorkAndSaveChanges() {
        //given
        Account account = new Account("John", "Doe");
        account.setWallets(List.of(
                new Wallet(new BigDecimal(5), CurrencyUnit.PLN),
                new Wallet(new BigDecimal(55), CurrencyUnit.USD)
        ));
        //when
        when(accountRepository.getAccountById(account.getId())).thenReturn(account);
        when(nbpService.getRate()).thenReturn(new Rates(new BigDecimal(1), new BigDecimal(1)));
        underTest.exchangeToPln(account.getId(),new BigDecimal(1));
        //then
        verify(accountRepository, atLeastOnce()).save(any());
    }
}