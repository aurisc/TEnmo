package com.techelevator.tenmo.controller;
//Error outside of a basic error
public class TransferException extends Exception {
    public TransferException() {
        super();
    }

    public TransferException(String message) {
        super(message);
    }
}