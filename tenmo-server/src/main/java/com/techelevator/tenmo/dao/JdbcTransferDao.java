package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao
{

    private JdbcTemplate jdbcTemplate;

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
    public void updateTransfer(Transfer transfer, Long typeId, Long statusId, Long transferId) {
        Transfer transfer1 = getTransferById(transferId);
        transfer1.setTransferId(transfer.getTransferId());
        transfer1.setAccountFrom(transfer.getAccountFrom());
        transfer1.setAccountTo(transfer.getAccountTo());
        transfer1.setAmount(transfer.getAmount());
        transfer1.setTransferTypeId(typeId);
        transfer1.setTransferStatusId(statusId);
        String SQL = "UPDATE transfer SET transfer_type_id = ?, transfer_status_id= ?  WHERE transfer_id = ?;";
        jdbcTemplate.update(SQL, typeId, statusId, transferId);
    }


    @Override
    public void addTransfer(Transfer transfer, Long typeid, Long statusId,  Long idFrom, Long idTo, BigDecimal amount) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";
        jdbcTemplate.update(sql, typeid, statusId, idFrom, idTo, amount);
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
