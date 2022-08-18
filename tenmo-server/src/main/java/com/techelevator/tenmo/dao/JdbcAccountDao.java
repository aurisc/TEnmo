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
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, account.user_id, balance FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            Account account = mapRowToAccount(results);
            accounts.add(account);
        }
        return accounts.toArray(Account[]::new);
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccount_id(rs.getLong("account_id"));
        account.setUser_id(rs.getLong("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
