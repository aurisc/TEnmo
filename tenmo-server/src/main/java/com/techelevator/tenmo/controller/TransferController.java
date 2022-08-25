package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.*;
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
@RequestMapping(path = "/transfer")
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
    @RequestMapping(path = "", method = RequestMethod.POST)
    public TransferDTO createTransfer(@Valid @RequestBody TransferDTO transferDTO, Principal principal) throws TransferException {
        //compareTo =0 equal, 1 greater than, -1 less than
        if (transferDTO.getAmount().compareTo(new BigDecimal(0)) < 1) {
            throw new TransferException("Transfer amount should be greater than zero");
        }
        User user = userDao.findByUsername(principal.getName());
        if (user == null) {
            throw new TransferException("User not found");
        }
        Account fromAccount = accountDao.getAccountsByUserId(transferDTO.getFromUser().getId())[0];
        Account toAccount = accountDao.getAccountsByUserId(transferDTO.getToUser().getId())[0];
        if (fromAccount == null) {
            throw new TransferException("From account not found");
        }
        if (fromAccount.getUserId().compareTo(user.getId()) != 0) {
            throw new TransferException("You can only send TE bucks from your own account");
        }
        if (fromAccount.getBalance().compareTo(transferDTO.getAmount()) < 0) {
            throw new TransferException("You do not have enough TE bucks in your own account");
        }
        if (toAccount == null) {
            throw new TransferException("To account not found");
        }
        if (toAccount.getUserId().compareTo(user.getId()) == 0) {
            throw new TransferException("You are not allowed to send TE bucks to your own account");
        }
        //transfer.setTransferTypeId(2L); // send
        //transfer.setTransferStatusId(2L); // approved

        //accountDao.updateBalance(transfer.getAccountFrom(), transfer.getAmount().negate());
        //accountDao.updateBalance(transfer.getAccountTo(), transfer.getAmount());

        Transfer transfer = transferDao.addTransfer(new Transfer(transferDTO.getType().getTypeId(),
                transferDTO.getStatus().getStatusId(), fromAccount.getAccountId(), toAccount.getAccountId(),
                transferDTO.getAmount()));
        if (transfer.getTransferTypeId().equals(TransferType.SEND.getTypeId()) &&
                transfer.getTransferStatusId().equals(TransferStatus.APPROVED.getStatusId())) {
            transfer = transferDao.completeTransfer(transfer);
        }
        transferDTO.setTransferId(transfer.getTransferId());
        return transferDTO;
    }

    @RequestMapping(path = "/history", method = RequestMethod.GET)
    public TransferDTO[] getAllTransferHistory(Principal principal)
    {
        Transfer[] transfers = null;
        TransferDTO[] transferHistory;
        Long id = userDao.findByUsername(principal.getName()).getId();
        Account[] accounts = accountDao.getAccountsByUserId(id);
        Long accountId = accounts[0].getAccountId();
        transfers = transferDao.getAllTransfers(accountId);
        if (transfers != null) {
            transferHistory = new TransferDTO[transfers.length];
            int index = 0;
            for (Transfer transfer : transfers) {
                transferHistory[index] = convertTransferToDTO(transfer);
                index++;
            }
        } else {
            transferHistory = null;
        }
        return transferHistory;
    }

    private TransferDTO convertTransferToDTO(Transfer transfer) {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setTransferId(transfer.getTransferId());
        transferDTO.setType(TransferType.getById(transfer.getTransferTypeId()));
        transferDTO.setStatus(TransferStatus.getById(transfer.getTransferStatusId()));
        transferDTO.setFromUser(accountDao.getAccountUser(transfer.getAccountFrom()));
        transferDTO.setToUser(accountDao.getAccountUser(transfer.getAccountTo()));
        transferDTO.setAmount(transfer.getAmount());
        return transferDTO;
    }
}
