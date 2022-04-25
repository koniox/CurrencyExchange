package com.example.CurrencyExchange.account;

import com.example.CurrencyExchange.exception.AccountNotFoundException;
import com.example.CurrencyExchange.exception.InsufficientBalanceException;
import com.example.CurrencyExchange.wallet.CurrencyUnit;
import com.example.CurrencyExchange.wallet.Wallet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldFetchOneAccountById() throws Exception {
        //given
        Integer id = 1;
        Account account = new Account("John", "Doe");
        account.setId(id);
        given(accountService.getAccount(account.getId())).willReturn(account);
        //when
        //then
        mockMvc.perform(get("/api/v1/account/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(account.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(account.getLastName())));
    }

    @Test
    void shouldReturn404WhenGetAccountById() throws Exception {
        //given
        Integer id = 1;
        given(accountService.getAccount(id)).willThrow(AccountNotFoundException.class);
        //when
        //then
        mockMvc.perform(get("/api/v1/account/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAccount() throws Exception {
        //given
        AccountRequest accountRequest = new AccountRequest("John", "Doe", new BigDecimal(50));
        Account account = new Account(accountRequest.getFirstName(),accountRequest.getLastName());
        account.setId(1);
        account.setWallets(List.of(
                new Wallet(accountRequest.getPlnBalance(), CurrencyUnit.PLN),
                new Wallet(new BigDecimal(0), CurrencyUnit.USD)
        ));
        given(accountService.createAccount(accountRequest)).willReturn(account);
        //when
        //then
        mockMvc.perform(post("/api/v1/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(accountRequest.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(accountRequest.getLastName())))
                .andExpect(jsonPath("$.wallets[0].balance", is(accountRequest.getPlnBalance().intValue())));
    }

    @Test
    void shouldThrow400WhenCreateAccountEmptyName() throws Exception {
        //given
        AccountRequest accountRequest = new AccountRequest("", "", new BigDecimal(0));
        //when
        //then
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        Map<String, String> errors = new HashMap<>();
        errors.put("lastName", "Last name can't be empty");
        errors.put("firstName", "First name can't be empty");
        errors.put("plnBalance", "Minimal balance value must be 1");
        String expectedResponseBody = objectMapper.writeValueAsString(errors);
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringCase(expectedResponseBody);
    }

    @Test
    void shouldExchangePlnToUsd() throws Exception {
        //given
        BigDecimal amountToExchange = new BigDecimal(5);
        given(accountService.exchangeToUsd(1, amountToExchange)).willReturn(BigDecimal.valueOf(1));
        //when
        //then
        mockMvc.perform(put("/api/v1/account/{id}/to-usd", 1)
                        .param("amountToExchange", amountToExchange.toString()))
                        .andExpect(status().isOk());
    }

    @Test
    void shouldThrow404WhenExchangePlnToUsd() throws Exception {
        //given
        BigDecimal amountToExchange = new BigDecimal(5);
        given(accountService.exchangeToUsd(1, amountToExchange)).willThrow(AccountNotFoundException.class);
        //when
        //then
        mockMvc.perform(put("/api/v1/account/{id}/to-usd", 1)
                        .param("amountToExchange", amountToExchange.toString()))
                        .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrow400WhenExchangePlnToUsd() throws Exception {
        //given
        BigDecimal amountToExchange = new BigDecimal(5);
        given(accountService.exchangeToUsd(1, amountToExchange)).willThrow(InsufficientBalanceException.class);
        //when
        //then
        mockMvc.perform(put("/api/v1/account/{id}/to-usd", 1)
                        .param("amountToExchange", amountToExchange.toString()))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldExchangeUsdToPln() throws Exception {
        //given
        BigDecimal amountToExchange = new BigDecimal(5);
        given(accountService.exchangeToPln(1, amountToExchange)).willReturn(BigDecimal.valueOf(1));
        //when
        //then
        mockMvc.perform(put("/api/v1/account/{id}/to-pln", 1)
                        .param("amountToExchange", amountToExchange.toString()))
                        .andExpect(status().isOk());
    }

    @Test
    void shouldThrow404WhenExchangeUsdToPln() throws Exception {
        //given
        BigDecimal amountToExchange = new BigDecimal(5);
        given(accountService.exchangeToPln(1, amountToExchange)).willThrow(AccountNotFoundException.class);
        //when
        //then
        mockMvc.perform(put("/api/v1/account/{id}/to-pln", 1)
                        .param("amountToExchange", amountToExchange.toString()))
                        .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrow400WhenExchangeUsdToPln() throws Exception {
        //given
        BigDecimal amountToExchange = new BigDecimal(5);
        given(accountService.exchangeToPln(1, amountToExchange)).willThrow(InsufficientBalanceException.class);
        //when
        //then
        mockMvc.perform(put("/api/v1/account/{id}/to-pln", 1)
                        .param("amountToExchange", amountToExchange.toString()))
                        .andExpect(status().isBadRequest());
    }
}