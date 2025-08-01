package com.marco.bankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marco.bankapp.exception.AccountNotFoundException;
import com.marco.bankapp.exception.InvalidAmountException;
import com.marco.bankapp.exception.InsufficientFundsException;
import com.marco.bankapp.model.Account;
import com.marco.bankapp.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyDouble;


@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account account1;
    private Account account2;

    @BeforeEach
    void setup() {
        account1 = new Account(1L, "John Doe", "12345", 100.0);
        account2 = new Account(2L, "Jane Smith", "67890", 200.0);
    }

    @Test
    void testGetAllAccounts_success() throws Exception {
        List<Account> accounts = List.of(account1, account2);
        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(accounts.size()))
                .andExpect(jsonPath("$[0].holderName").value("John Doe"))
                .andExpect(jsonPath("$[1].balance").value(200.0));
    }

    @Test
    void testGetAccountByNumber_success() throws Exception {
        when(accountService.getAccountByNumber("12345")).thenReturn(account1);

        mockMvc.perform(get("/api/accounts/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holderName").value("John Doe"))
                .andExpect(jsonPath("$.accountNumber").value("12345"))
                .andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    void testGetAccountByNumber_notFound() throws Exception {
        when(accountService.getAccountByNumber("99999"))
            .thenThrow(new AccountNotFoundException("99999"));


        mockMvc.perform(get("/api/accounts/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found with number: 99999"));
    }

    @Test
    void testCreateAccount_success() throws Exception {
        Account newAccount = new Account(null, "Alice", "54321", 50.0);
        Account savedAccount = new Account(3L, "Alice", "54321", 50.0);

        when(accountService.createAccount(any(Account.class))).thenReturn(savedAccount);

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.holderName").value("Alice"))
                .andExpect(jsonPath("$.balance").value(50.0));
    }

    @Test
    void testCreateAccount_invalidAmount() throws Exception {
        Account newAccount = new Account(null, "Alice", "54321", -10.0);

        when(accountService.createAccount(any(Account.class)))
                .thenThrow(new InvalidAmountException("Initial balance must be positive"));

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Initial balance must be positive"));
    }

    @Test
    void testDeposit_success() throws Exception {
        Account updatedAccount = new Account(1L, "John Doe", "12345", 150.0);

        when(accountService.deposit("12345", 50.0)).thenReturn(updatedAccount);

        mockMvc.perform(post("/api/accounts/12345/deposit")
                .param("amount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.0));
    }

    @Test
    void testDeposit_invalidAmount() throws Exception {
        when(accountService.deposit(eq("12345"), anyDouble()))
                .thenThrow(new InvalidAmountException("Amount must be greater than zero"));

        mockMvc.perform(post("/api/accounts/12345/deposit")
                .param("amount", "-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount must be greater than zero"));
    }

    @Test
    void testWithdraw_success() throws Exception {
        Account updatedAccount = new Account(1L, "John Doe", "12345", 80.0);

        when(accountService.withdraw("12345", 20.0)).thenReturn(updatedAccount);

        mockMvc.perform(post("/api/accounts/12345/withdraw")
                .param("amount", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(80.0));
    }

    @Test
    void testWithdraw_insufficientFunds() throws Exception {
        when(accountService.withdraw(anyString(), anyDouble()))
            .thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(post("/api/accounts/12345/withdraw")
                .param("amount", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }


    @Test
    void testDeleteAccount_success() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());
    }
}
