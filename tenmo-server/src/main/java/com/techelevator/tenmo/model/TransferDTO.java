package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferDTO {

    private Long transferId;
    @NotNull
    private UserDTO fromUser;
    @NotNull
    private UserDTO toUser;
    @NotNull
    private TransferType type;
    @NotNull
    private TransferStatus status;
    @Positive
    private BigDecimal amount;

    public TransferDTO() {
    }

    public TransferDTO(UserDTO fromUser, UserDTO toUser, TransferType type, TransferStatus status, BigDecimal amount) {
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

    public UserDTO getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserDTO fromUser) {
        this.fromUser = fromUser;
    }

    public UserDTO getToUser() {
        return toUser;
    }

    public void setToUser(UserDTO toUser) {
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
}
