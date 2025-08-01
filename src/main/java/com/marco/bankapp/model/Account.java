package com.marco.bankapp.model;

import com.marco.bankapp.exception.InsufficientFundsException;
import com.marco.bankapp.exception.InvalidAmountException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String holderName;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    private double balance;

    //validates initial amount cant be negative
    public void init() throws InvalidAmountException {
        if (balance < 0) {
            throw new InvalidAmountException(balance);
        }
    }


    //Deposits a valid amount into the account.
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        balance += amount;
    }

    //Withdraws a valid amount from the account.
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        balance -= amount;
    }
}
