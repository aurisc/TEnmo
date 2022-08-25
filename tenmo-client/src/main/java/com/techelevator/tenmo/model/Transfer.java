package com.techelevator.tenmo.model;

import com.techelevator.tenmo.services.TransferType;

import java.math.BigDecimal;

public class Transfer {

    private Long transferId;
    private Long transferTypeId;
    private Long transferStatusId;
    private Long accountFrom;
    private Long accountTo;
    private BigDecimal amount;

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(Long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public Long getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(Long transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public Long getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Long accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Long accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String toDetailString()
    {
        String transferType = "";
        String transferStatus = "";
        if (transferTypeId.equals((long) 2)) {
            transferType = "Send";
        } else if (transferTypeId.equals((long) 1)) {
            transferType = "Request";
        }
        if (transferStatusId.equals((long) 1)) {
            transferStatus = "Pending";
        } else if (transferStatusId.equals((long) 2)) {
            transferStatus = "Approved";
        } else if (transferStatusId.equals((long) 3)) {
            transferStatus = "Rejected";
        }

        String transfer = "Transfer id:"+getTransferId()+
                " Account from: "+getAccountFrom()+
                " Account to: "+getAccountTo()+
                " Transfer type: "+ transferType +
                " Transfer status "+ transferStatus +
                " Amount: "+ getAmount();
        return transfer;
    };
    @Override
    public String toString()
    {
        String transfer = "Transfer id:"+getTransferId()+
                " Account from: "+getAccountFrom()+
                " Account to: "+getAccountTo()+
                " Amount: "+ getAmount();
        return transfer;
    }
}
