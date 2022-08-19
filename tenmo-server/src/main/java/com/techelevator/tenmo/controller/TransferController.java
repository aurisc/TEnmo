package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "transfer")
public class TransferController {
    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;

    public TransferController(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    //NEW Send TE Bucks
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public void sendTransfer(@Valid @RequestBody Transfer transfer, Principal principal) throws TransferException {
        //compareTo =0 equal, 1 greater than, -1 less than
        if(transfer.getAmount().compareTo(new BigDecimal(0)) < 1) {
            throw new TransferException("Transfer amount should be greater than zero");
        }
        User user = userDao.findByUsername(principal.getName());
        if(user==null) {
            throw new TransferException("User not found");
        }
        Account fromAccount = accountDao.getAccountsById(transfer.getAccountFrom());
        if(fromAccount==null) {
            throw new TransferException("From account not found");
        }
        if(fromAccount.getUser_id().compareTo(user.getId())!=0) {
            throw new TransferException("You can only send TE bucks from your own account");
        }
        if(fromAccount.getBalance().compareTo(transfer.getAmount())<0) {
            throw new TransferException("You do not have enough TE bucks in your own account");
        }
        Account toAccount = accountDao.getAccountsById(transfer.getAccountTo());
        if(toAccount==null) {
            throw new TransferException("To account not found");
        }
        if(toAccount.getUser_id().compareTo(user.getId())==0) {
            throw new TransferException("You are not allowed to send TE bucks to your own account");
        }
        transfer.setTransferTypeId(2L); // send
        transfer.setTransferStatusId(2L); // approved
        accountDao.updateBalance(transfer.getAccountFrom(), transfer.getAmount().negate());
        accountDao.updateBalance(transfer.getAccountTo(), transfer.getAmount());
        transferDao.addTransfer(transfer);
    }
