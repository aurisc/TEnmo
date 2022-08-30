package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
//Outline DAO for Transfer
public interface TransferDao {

    Transfer[] getAllTransfers(Long id);

    Transfer[] getPendingTransfers(Long accountId);

    Transfer[] getTransferByUserId(Long id);

    boolean updateTransfer(Transfer transfer);

    Transfer getTransferById(Long id);

    Transfer addTransfer(Transfer transfer);

    Transfer completeTransfer(Transfer transfer);

}
