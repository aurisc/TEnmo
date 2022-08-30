package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao
{

    private JdbcAccountDao accountDao;
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = new JdbcAccountDao(jdbcTemplate);
    }
    //Shows all transfers
    @Override
    public Transfer[] getAllTransfers(Long id) {
        List<Transfer> transfer = new ArrayList<Transfer>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer " +
                "WHERE account_from = ? OR account_to = ?;";
        try
        {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
            while (results.next()) {
                Transfer mapTransfer = mapToRowTransfer(results);
                transfer.add(mapTransfer);
            }
        }
        catch (ResourceAccessException | RestClientResponseException e)
        {
        }
        return transfer.toArray(new Transfer[0]);
    }
    //Get all pending transfers
    @Override
    public Transfer[] getPendingTransfers(Long accountId) {
        List<Transfer> transfer = new ArrayList<Transfer>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer " +
                "WHERE (account_from = ? OR account_to = ?) AND (transfer_status_id = ?);";
        try
        {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId, TransferStatus.PENDING.getStatusId());
            while (results.next()) {
                Transfer mapTransfer = mapToRowTransfer(results);
                transfer.add(mapTransfer);
            }
        }
        catch (ResourceAccessException | RestClientResponseException e)
        {
        }
        return transfer.toArray(new Transfer[0]);
    }
    //get a transfer by the user ID
    @Override
    public Transfer[] getTransferByUserId(Long id) {
        List<Transfer> transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer JOIN account ON account.account_id = transfer.account_from " +
                "WHERE account.user_id = ?";
        try
        {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            while (results.next()) {
                Transfer mapTransfer = mapToRowTransfer(results);
                transfer.add(mapTransfer);
            }
        }
        catch (ResourceAccessException | RestClientResponseException e)
        {
        }
        return transfer.toArray(new Transfer[0]);
    }
    //Complete the transaction of moving money between accounts
    @Transactional
    public Transfer completeTransfer(Transfer transfer) {
        BigDecimal fromBalance = accountDao.getBalanceById(transfer.getAccountFrom());
        BigDecimal toBalance = accountDao.getBalanceById(transfer.getAccountTo());
        fromBalance = fromBalance.subtract(transfer.getAmount());
        toBalance = toBalance.add(transfer.getAmount());
        accountDao.updateBalance(transfer.getAccountFrom(), fromBalance);
        accountDao.updateBalance(transfer.getAccountTo(), toBalance);
        transfer.setTransferStatusId(TransferStatus.APPROVED.getStatusId());
        return transfer;
    }
    //Show transfer from its ID
    @Override
    public Transfer getTransferById(Long id) {
        Transfer transfer = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        try
        {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            transfer = mapToRowTransfer(results);
        }
        catch (ResourceAccessException | RestClientResponseException e)
        {
        }
        return transfer;
    }
    //Update the transfer of an account
    @Override
    public boolean updateTransfer(Transfer transfer) {
        String SQL = "UPDATE transfer SET transfer_status_id= ?  WHERE transfer_id = ?;";
        return jdbcTemplate.update(SQL, transfer.getTransferStatusId(), transfer.getTransferId()) == 1;
    }

    //Add a transfer into the database
    @Override
    public Transfer addTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";
        Long transferId = jdbcTemplate.queryForObject(sql, Long.class, transfer.getTransferTypeId(), transfer.getTransferStatusId(),
                transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        if (transferId != null) {
            transfer.setTransferId(transferId);
        }
        return transfer;
    }
    //Adjust values to transfer class
    private Transfer mapToRowTransfer(SqlRowSet results){
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getLong("transfer_id"));
        transfer.setTransferTypeId(results.getLong("transfer_type_id"));
        transfer.setTransferStatusId(results.getLong("transfer_status_id"));
        transfer.setAccountFrom(results.getLong("account_from"));
        transfer.setAccountTo(results.getLong("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }
}
