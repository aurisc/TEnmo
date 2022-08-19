package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferStatus;

public interface TransferStatusDao {

    TransferStatus[] getAllTransferStatus();

    TransferStatus getTransferStatus(Long id);

    void setTransferStatus(Long id);
}
