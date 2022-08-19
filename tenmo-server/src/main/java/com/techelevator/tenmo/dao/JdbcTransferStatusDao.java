package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferStatus;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransferStatusDao implements TransferStatusDao {


    @Override
    public TransferStatus[] getAllTransferStatus() {
        return new TransferStatus[0];
    }

    @Override
    public TransferStatus getTransferStatus(Long id) {
        return null;
    }

    @Override
    public void setTransferStatus(Long id) {

    }
}
