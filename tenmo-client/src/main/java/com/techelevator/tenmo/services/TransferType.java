package com.techelevator.tenmo.services;

public enum TransferType {

    REQUEST ((long) 1),
    SEND    ((long) 2);

    private final Long typeId;

    TransferType(Long typeId) {
        this.typeId = typeId;
    }

    public Long getTypeId() {
        return typeId;
    }

    @Override
    public String toString() {
        switch (this) {
            case SEND:
                return "Send";
            case REQUEST:
                return "Request";
        }
        return "";
    }
}
