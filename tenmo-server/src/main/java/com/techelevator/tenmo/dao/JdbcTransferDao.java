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

    @Override
    public Transfer[] getAllTransfers() {
        List<Transfer> transfer = null;
        String sql = "SELECT * FROM transfer";
        try
        {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
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

    @Override
    public Transfer[] getTransferByUserId(Long id) {
        List<Transfer> transfer = null;
        String sql = "SELECT * FROM transfer WHERE account_from = ? OR account_to = ?";
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

    @Transactional
    public Transfer completeTransfer(Transfer transfer) {
        Transfer updatedTransfer = new Transfer();
        updatedTransfer.setTransferId(transfer.getTransferId());
        updatedTransfer.setTransferTypeId(transfer.getTransferTypeId());
        updatedTransfer.setAccountTo(transfer.getAccountTo());
        updatedTransfer.setAccountFrom(transfer.getAccountFrom());
        updatedTransfer.setAmount(transfer.getAmount());
        Account fromAccount = accountDao.getAccountById(transfer.getAccountFrom());
        Account toAccount = accountDao.getAccountById(transfer.getAccountTo());
        BigDecimal fromBalance = fromAccount.getBalance();
        BigDecimal toBalance = toAccount.getBalance();
        if (fromBalance.compareTo(transfer.getAmount()) >= 0) {
            fromBalance = fromBalance.subtract(transfer.getAmount());
            toBalance.add(transfer.getAmount());
            accountDao.updateBalance(fromAccount.getAccountId(), fromBalance);
            accountDao.updateBalance(toAccount.getAccountId(), toBalance);
            updatedTransfer.setTransferStatusId(TransferStatus.APPROVED.getStatusId());
        } else {
            updatedTransfer.setTransferStatusId(TransferStatus.REJECTED.getStatusId());
        }
        return updatedTransfer;
    }

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

    @Override
    public void updateTransfer(Transfer transfer) {
        String SQL = "UPDATE transfer SET transfer_type_id = ?, transfer_status_id= ?  WHERE transfer_id = ?;";
        jdbcTemplate.update(SQL, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getTransferId());
    }


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
