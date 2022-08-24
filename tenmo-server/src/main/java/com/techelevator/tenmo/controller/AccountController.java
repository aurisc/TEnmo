package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "account/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        return accountDao.getBalance(principal.getName());
    }

    @RequestMapping(path = "account/balance/{id}", method = RequestMethod.GET)
    public BigDecimal getBalanceById(@PathVariable Long id) {
        return accountDao.getBalanceById(id);
    }

    @RequestMapping(path = "account/user/{id}", method = RequestMethod.GET)
    public Account[] getAccountsByUserId(@PathVariable Long id) {
        return accountDao.getAccountsByUserId(id);
    }

    @RequestMapping(path = "account/{id}", method = RequestMethod.GET)
    public Account getAccountById(@PathVariable Long id) {
        return accountDao.getAccountById(id);
    }

    @RequestMapping(path = "user", method = RequestMethod.GET)
    public User[] getUsers() {
        List<User> userList = userDao.findAll();
        User[] users = new User[userList.size()];
        users = userList.toArray(users);
        return users;
    }

    @RequestMapping(path = "user/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable Long id) {
        return userDao.findById(id);
    }

    @RequestMapping(path = "user/account/{id}", method = RequestMethod.GET)
    public String getAccountUsername(@PathVariable Long id) {
        return accountDao.getAccountUsername(id);
    }
}
