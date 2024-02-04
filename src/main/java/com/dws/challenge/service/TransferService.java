package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
class TransferService {
    @Getter
    private final AccountsRepository accountsRepository;

    @Getter
    private final EmailNotificationService emailNotificationService;

    @Autowired
    public TransferService(AccountsRepository accountsRepository,EmailNotificationService emailNotificationService) {
        this.accountsRepository = accountsRepository;
        this.emailNotificationService = emailNotificationService;
    }

    public void transferMoney(String accountFromId, String accountToId, BigDecimal amount){

        Account accountFrom = accountsRepository.getAccount(accountFromId);
        Account accountTo = accountsRepository.getAccount(accountToId);

        if(accountFrom == null || accountTo == null || amount.compareTo(BigDecimal.ZERO)<0 ){
            throw new IllegalStateException("Invalid money transfer details");
        }

        synchronized (accountFrom){
            synchronized (accountTo){
                accountFrom.debit(amount);
                accountTo.credit(amount);
            }
        }

        notifyTransfer(accountTo,accountFrom,amount);
    }

    private void notifyTransfer(Account accountFrom,Account accountTo,BigDecimal amount){
        String transferDescription = "Transfer of Rs" + amount + " from account "
                + accountFrom.getAccountId() + " to account " + accountTo.getAccountId();

        emailNotificationService.notifyAboutTransfer(accountTo,transferDescription);
        emailNotificationService.notifyAboutTransfer(accountFrom,transferDescription);
    }




}
