package com.dws.challenge.service;


import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testTransferMoney_Success() {
        Account accountFrom = new Account("account1", BigDecimal.valueOf(100));
        Account accountTo = new Account("account2", BigDecimal.valueOf(50));

        when(accountsRepository.getAccount("account1")).thenReturn(accountFrom);
        when(accountsRepository.getAccount("account2")).thenReturn(accountTo);

        transferService.transferMoney("account1", "account2", BigDecimal.TEN);

        assertEquals(BigDecimal.valueOf(90), accountFrom.getBalance());
        assertEquals(BigDecimal.valueOf(60), accountTo.getBalance());

        verify(emailNotificationService, times(2)).notifyAboutTransfer(any(), any());
    }

    @Test
    void testTransferMoney_InvalidDetails() {
        when(accountsRepository.getAccount("account1")).thenReturn(null);
        when(accountsRepository.getAccount("account2")).thenReturn(new Account("account2", BigDecimal.ZERO));

        assertThrows(IllegalStateException.class,
                () -> transferService.transferMoney("account1", "account2", BigDecimal.TEN));

        verify(emailNotificationService, never()).notifyAboutTransfer(any(), any());
    }
}