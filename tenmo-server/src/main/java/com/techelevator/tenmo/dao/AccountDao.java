package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.UserDTO;

import java.math.BigDecimal;
//DAO outline
public interface AccountDao {
    BigDecimal getBalance(String userName);
    BigDecimal getBalanceById(Long accountId);
    Account[] getAccountsByUserId(Long userId);
    Account getAccountById(Long accountId);
    void updateBalance(Long accountId, BigDecimal amount);
    UserDTO getAccountUser(Long id);

}
