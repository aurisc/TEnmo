package com.techelevator.tenmo.model;

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

    public static TransferType getById(Long id) {
        for (TransferType type : values()) {
            if (type.typeId.equals(id)) {
                return type;
            }
        }
        return null;
    }

}
