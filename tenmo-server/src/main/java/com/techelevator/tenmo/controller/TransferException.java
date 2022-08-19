package com.techelevator.tenmo.controller;

public class TransferException extends Exception {
    public TransferException() {
        super();
    }

    public TransferException(String message) {
        super(message);
    }
}