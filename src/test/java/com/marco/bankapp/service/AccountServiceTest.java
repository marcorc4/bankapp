package com.marco.bankapp.service;

import com.marco.bankapp.exception.AccountNotFoundException;
import com.marco.bankapp.exception.InsufficientFundsException;
import com.marco.bankapp.model.Account;
import com.marco.bankapp.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setup() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    void testDeposit_success() {
        Account account = new Account(1L, "John Doe", "12345", 100.0);
        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        Account updated = accountService.deposit("12345", 50.0);
        assertEquals(150.0, updated.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void testDeposit_accountNotFound() {
        when(accountRepository.findByAccountNumber("99999")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.deposit("99999", 50.0);
        });
    }

    @Test
    void testWithdraw_insufficientFunds() {
        Account account = new Account(1L, "John Doe", "12345", 100.0);
        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));
        assertThrows(InsufficientFundsException.class, () -> {
            accountService.withdraw("12345", 150.0);
        });
    }

    @Test
    void testWithdraw_success() {
        Account account = new Account(1L, "John Doe", "12345", 200.0);
        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        Account updated = accountService.withdraw("12345", 50.0);
        assertEquals(150.0, updated.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void testGetAccountByNumber_notFound() {
        when(accountRepository.findByAccountNumber("99999")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountByNumber("99999");
        });
    }

    @Test
    void testCreateAccount_success() {
        Account account = new Account(null, "Jane Doe", "54321", 100.0);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> {
            Account acc = i.getArgument(0);
            acc.setId(2L); // Simula ID asignado por BD
            return acc;
        });

        Account created = accountService.createAccount(account);
        assertNotNull(created.getId());
        assertEquals("54321", created.getAccountNumber());
        verify(accountRepository).save(account);
    }

}
