package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;

public interface TransferDao {

    Transfer[] getAllTransfers();

    Transfer[] getTransferByUserId(Long id);

    void updateTransfer(Transfer transfer, Long typeId, Long statusId, Long transferId);

    Transfer getTransferById(Long id);

    void addTransfer(Transfer transfer, Long typeid, Long statusId,  Long idFrom, Long idTo, BigDecimal amount);

}
