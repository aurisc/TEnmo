package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;

public interface TransferDao {

    Transfer[] getAllTransfers();

    Transfer[] getTransferByUserId(Long id);

    void updateTransfer(Transfer transfer);

    Transfer getTransferById(Long id);

    Transfer addTransfer(Transfer transfer);

    Transfer completeTransfer(Transfer transfer);

}
