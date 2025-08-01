package com.marco.bankapp.service;

import com.marco.bankapp.exception.AccountNotFoundException;
import com.marco.bankapp.exception.InsufficientFundsException;
import com.marco.bankapp.exception.InvalidAmountException;
import com.marco.bankapp.model.Account;
import com.marco.bankapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountByNumber(String accountNumber) throws AccountNotFoundException {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public Account createAccount(Account account) throws InvalidAmountException {
        account.init(); // Validar balance inicial
        return accountRepository.save(account);
    }

    public Account deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException {

        Account account = getAccountByNumber(accountNumber);
        account.deposit(amount);
        return accountRepository.save(account);
    }

    public Account withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {

        Account account = getAccountByNumber(accountNumber);
        account.withdraw(amount);
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
