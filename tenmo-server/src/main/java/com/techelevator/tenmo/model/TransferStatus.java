package com.techelevator.tenmo.model;

public enum TransferStatus {

    PENDING  ((long)1),
    APPROVED ((long)2),
    REJECTED ((long)3);

    private final Long statusId;

    TransferStatus(Long statusId) {
        this.statusId = statusId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public static TransferStatus getById(Long id) {
        for (TransferStatus status : values()) {
            if (status.statusId.equals(id)) {
                return status;
            }
        }
        return null;
    }
}
