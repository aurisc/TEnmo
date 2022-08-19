package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public BigDecimal getBalance(String userName) {
        BigDecimal balance = new BigDecimal("0.00");
        String sql = "SELECT account_id, account.user_id, balance FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userName);
        while (results.next()) {
            Account account = mapRowToAccount(results);
            balance = balance.add(account.getBalance());
        }
        return balance;
    }

    @Override
    public Account[] getAccountsByUserId(Long userId) {
        List<Account> accountList = new ArrayList<>();
        String sql = "SELECT account_id, account.user_id, balance FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE account.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            Account account = mapRowToAccount(results);
            accountList.add(account);
        }
        Account[] accounts = new Account[accountList.size()];
        accounts = accountList.toArray(accounts);
        return accounts;
    }
    //NEW
    @Override
    public Account getAccountById(Long accountId) {
        Account account = null;
        String sql = "SELECT account_id, account.user_id, balance FROM account WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }
    //new
    @Override
    public void updateBalance(Long accountId, BigDecimal amount) {
        String sql = "UPDATE account set balance = balance + ? WHERE account_id = ?";
        jdbcTemplate.update(sql, amount, accountId);
    }
    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setUserId(rs.getLong("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
