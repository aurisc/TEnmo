package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "account")
public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        return accountDao.getBalance(principal.getName());
    }

    @RequestMapping(path = "account/user/{id}", method = RequestMethod.GET)
    public Account[] getAccountsByUserId(@PathVariable Long id) {
        return accountDao.getAccountsByUserId(id);
    }

    @RequestMapping(path = "user", method = RequestMethod.GET)
    public User[] getUsers() {
        return userDao.findAll().toArray(User[]::new);
    }

    @RequestMapping(path = "user/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable Long id) {
        return userDao.findById(id);
    }
}
