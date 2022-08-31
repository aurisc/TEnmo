package com.techelevator.tenmo.services.serviceExceptions;
//Error exception class for a different log in case of error
public class AccountServiceException extends Exception{
    public AccountServiceException(String message) {
        super(message);
    }
}
