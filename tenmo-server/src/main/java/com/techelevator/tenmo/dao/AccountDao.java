package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    //List<User> findAll();
    //User findByUsername(String username);
    BigDecimal getBalance(String userName);
    Account[] getAccountsByUserId(Long userId);
    //new
    Account getAccountsById(Long accountId);
    //new
    void updateBalance(Long accountId, BigDecimal amount);

}
