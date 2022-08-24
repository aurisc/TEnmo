package com.techelevator.tenmo.model;

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
        String transfer = "Transfer id:"+getTransferId()+
                " Account from: "+getAccountFrom()+
                " Account to: "+getAccountTo()+
                " Transfer type: "+getTransferTypeId()+
                " Transfer status "+getTransferStatusId()+
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
