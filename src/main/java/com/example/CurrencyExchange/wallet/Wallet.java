package com.example.CurrencyExchange.wallet;

import com.example.CurrencyExchange.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@NoArgsConstructor
@JsonIgnoreProperties("owner")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account owner;

    @Column
    private BigDecimal balance;

    @Column
    private CurrencyUnit currencyUnit;

    public Wallet(BigDecimal balance, CurrencyUnit currencyUnit) {
        this.balance = balance;
        this.currencyUnit = currencyUnit;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", balance=" + balance +
                ", currencyUnit=" + currencyUnit +
                '}';
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public CurrencyUnit getCurrencyUnit() {
        return currencyUnit;
    }

}
