package com.techelevator.tenmo.model;

import com.techelevator.tenmo.services.TransferStatus;
import com.techelevator.tenmo.services.TransferType;

import java.math.BigDecimal;

/*
 * The Transfer model class used in the client application; represents information sent to and from the server when a
 * transfer is created, retrieved, or updated. Does not contain account information and corresponds with TransferDTO
 * model class on the server application.
 */
public class Transfer {

    //Transfer model to allow transfer of funds
    private Long transferId;
    private User fromUser;
    private User toUser;
    private TransferType type;
    private TransferStatus status;
    private BigDecimal amount;

    public Transfer() {
    }

    public Transfer(User fromUser, User toUser, TransferType type, TransferStatus status, BigDecimal amount) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.type = type;
        this.status = status;
        this.amount = amount;
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public TransferType getType() {
        return type;
    }

    public void setType(TransferType type) {
        this.type = type;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    //write out the information of a transfer
    @Override
    public String toString()
    {
        return "Transfer id:"+ transferId + "\n" +
                " From: " + fromUser.getUsername() + "\n" +
                " To: " + toUser.getUsername() + "\n" +
                " Transfer type: "+ type.toString() + "\n" +
                " Transfer status "+ status.toString() + "\n" +
                " Amount: " + amount;
    }
}
