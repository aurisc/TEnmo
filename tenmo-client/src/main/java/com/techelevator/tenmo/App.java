package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.tenmo.services.serviceExceptions.AccountServiceException;
import com.techelevator.util.BasicLogger;

import java.math.BigDecimal;

public class App {
    //Local API to be used
    private static final String API_BASE_URL = "http://localhost:8080/";
    private boolean validTransfer;
    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    //First menu to login/register
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }
    //Create your account
    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }
    //Login with a registered account
    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        accountService.setUser(currentUser);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }
    //Main menu of an account after login
    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }
    //See the balance of your account
	private void viewCurrentBalance() {
        BigDecimal balance = accountService.getBalance();
        System.out.println("Current account balance: " + balance);
	}
    //Show all transfer history
	private void viewTransferHistory() {
		// COMPLETE
		Transfer[] transfers = accountService.getTransferHistory();
        if (transfers != null) {
            System.out.println("Transfers for user: " + currentUser.getUser().getUsername());
            for (Transfer transfer : transfers) {
                printTransferInfo(transfer);
            }
            int menuSelection = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
            if(menuSelection != 0) { //Show more details of a transfer
                Transfer selectedTransfer = checkForAccount(transfers,(long)menuSelection);
                System.out.println(selectedTransfer.toString());
            }
        }
	}

	private void viewPendingRequests() {
		// TODO Get all pending request
		Transfer[] pendingTransfers = accountService.getPendingTransfers();
        if (pendingTransfers != null) {
            System.out.println("Pending transfers for user: " + currentUser.getUser().getUsername());
            for (Transfer transfer : pendingTransfers) {
                printPendingTransfer(transfer);
            }
            int menuSelection = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
            if (menuSelection != 0) { //Show details of pending transfer
                Transfer selectedTransfer = checkForAccount(pendingTransfers, (long) menuSelection);
                System.out.println(selectedTransfer.toString());
                if (selectedTransfer.getFromUser().getUsername().equals(currentUser.getUser().getUsername())) { //allow approval of request
                    System.out.println("1. Approve\n2. Reject\n0. Don't approve or reject");
                    menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
                    switch (menuSelection) {
                        case 1:
                            // Approve
                            boolean success = false;
                            try {
                                checkValidTransferAmount(selectedTransfer.getAmount());
                            } catch (AccountServiceException e) {
                                consoleService.printErrorMessage(e.getMessage());
                                BasicLogger.log(e.getMessage());
                                break;
                            }
                            selectedTransfer.setStatus(TransferStatus.APPROVED);
                            success = accountService.updateTransfer(selectedTransfer);
                            if (success) {
                                System.out.println("Pending transfer approved.");
                            }
                            break;
                        case 2:
                            // Reject
                            selectedTransfer.setStatus(TransferStatus.REJECTED);
                            success = accountService.updateTransfer(selectedTransfer);
                            if (success) {
                                System.out.println("Pending transfer rejected.");
                            }
                            break;
                        case 0:
                            // Neither approve nor reject
                            consoleService.pause();
                            break;
                        default:
                            System.out.println("Invalid selection");
                    }
                }

            }
        }
	}
    //Sending money to another account
	private void sendBucks() {
		// COMPLETE
        User userSelection = null;
        BigDecimal amountToTransfer = null;
        int menuSelection = -1;
        while (menuSelection !=0) { // Main loop for starting Send transfer
            if (userSelection == null) { // User not selected yet
                printAllUsers();
                menuSelection = consoleService.promptForInt("Select user ID (0 to cancel.): ");
                if (menuSelection != 0) { //Select use check
                    try {
                        userSelection = getValidatedUserId((long) menuSelection);
                    } catch (AccountServiceException e) {
                        consoleService.printErrorMessage(e.getMessage());
                        BasicLogger.log(e.getMessage());
                    }
                } else {
                    continue;
                }
            }
            amountToTransfer = consoleService.promptForBigDecimal("Enter amount to transfer: ");
            if (amountToTransfer != null) { // Confirm value is not blank
                try {
                    checkValidTransferAmount(amountToTransfer); //Confirm amount is valid
                } catch (AccountServiceException e) {
                    consoleService.printErrorMessage(e.getMessage());
                    BasicLogger.log(e.getMessage());
                    break;
                }
                if (validTransfer)
                {
                Transfer transfer =
                        new Transfer(currentUser.getUser(), userSelection, TransferType.SEND,
                                TransferStatus.APPROVED, amountToTransfer); //Approve send money
                Transfer returnedTransfer = accountService.createTransfer(transfer);
                if (returnedTransfer != null) {
                    System.out.println("Successfully created transfer. ID: " + returnedTransfer.getTransferId());
                    validTransfer = false;
                    break;
                }
                }
                break;
            }

        }
	}

	private void requestBucks() {
		// COMPLETE Call to another user to request money
        User userSelection = null;
        BigDecimal amountToTransfer = null;
        int menuSelection = -1;
        while (menuSelection !=0) { // Main loop for starting Request transfer
            if (userSelection == null) { // User not selected yet
                printAllUsers();
                menuSelection = consoleService.promptForInt("Select user ID (0 to cancel.): ");
                if (menuSelection != 0) {
                    try {
                        userSelection = getValidatedUserId((long) menuSelection);
                    } catch (AccountServiceException e) {
                        consoleService.printErrorMessage(e.getMessage());
                        BasicLogger.log(e.getMessage());
                    }
                } else {
                    continue;
                }
            }
            amountToTransfer = consoleService.promptForBigDecimal("Enter amount to transfer: "); //Request amount from user
            if (amountToTransfer != null && amountToTransfer.compareTo(new BigDecimal(0)) > 0) {
                Transfer transfer =
                        new Transfer(userSelection, currentUser.getUser(), TransferType.REQUEST,
                                TransferStatus.PENDING, amountToTransfer); //Pending transfer request sent to user
                Transfer returnedTransfer = accountService.createTransfer(transfer);
                if (returnedTransfer != null) {
                    System.out.println("Successfully created transfer. ID: " + returnedTransfer.getTransferId());
                    break;
                }
            } else {
                System.out.println("Amount must be greater than zero.");
                break;
            }
        }
		
	}
    //Get all accounts
    private void printAllUsers() {
        User[] users = accountService.getUsers();
        if (users != null) {
            for (User user : users) {
                if (user.getUsername().equals(currentUser.getUser().getUsername())) {
                    continue;
                }
                System.out.println("ID: " + user.getId() + "    User Name: " + user.getUsername());
            }
        }
    }
    //Print the information of a transfer
    private void printTransferInfo(Transfer transfer)
    {
        if(transfer.getFromUser().getUsername().equals(currentUser.getUser().getUsername())) {
            System.out.println("Transfer Id: " + transfer.getTransferId() +
                    " To: " + transfer.getToUser().getUsername() + " Amount: " + transfer.getAmount());
        } else {
            System.out.println("Transfer Id: " + transfer.getTransferId() +
                    " From: " + transfer.getFromUser().getUsername() + " Amount: " + transfer.getAmount());
        }
    }
    //Print out pending transfer information
    private void printPendingTransfer(Transfer transfer) {
        System.out.println("Transfer Id: " + transfer.getTransferId() +
                " Requester: " + transfer.getToUser().getUsername() + " Amount: " + transfer.getAmount());
    }
    //Get the ID of a user
    private User getValidatedUserId(Long id) throws AccountServiceException {
        User user = null;
        user = accountService.getUserById(id);
        if (user == null) {
            throw new AccountServiceException("User ID not found: " + id);
        }
        if (currentUser.getUser().getId().equals(id)) {
            throw new AccountServiceException("Currently unable to send or request funds to self.");
        }
        return user;
    }
    //Makes sure money is in account and not going below 0
    private void checkValidTransferAmount(BigDecimal transferAmount) throws AccountServiceException {
        BigDecimal userBalance = null;
        validTransfer = false;
        if (transferAmount.compareTo(new BigDecimal(0)) <= 0) {
            validTransfer = false;
            throw new AccountServiceException("Amount must be greater than zero.");
        }
        userBalance = accountService.getBalance();
        if (userBalance != null) {
            if (userBalance.compareTo(transferAmount) < 0) {
                validTransfer = false;
                throw new AccountServiceException("Insufficient funds.");

            }
        } else {
            validTransfer = false;
            throw new AccountServiceException("Unable to retrieve balance.");
        }
        validTransfer = true;
    }
    //Double checks account exist
    private Transfer checkForAccount(Transfer[] transfers, Long id)
    {
        Transfer selectedTransfer = null;
        for (Transfer transfer : transfers) {
            if(transfer.getTransferId().equals(id)) {
                selectedTransfer = transfer;
                break;
            }
        }
        return selectedTransfer;
    }
}
