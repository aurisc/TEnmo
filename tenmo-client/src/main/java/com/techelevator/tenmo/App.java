package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.tenmo.services.serviceExceptions.TransferServiceException;
import com.techelevator.util.BasicLogger;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);

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

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        accountService.setUser(currentUser);
        transferService.setUser(currentUser);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

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

	private void viewCurrentBalance() {
        BigDecimal balance = accountService.getBalance();
        System.out.println("Current account balance: " + balance);
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		Transfer[] transfers = transferService.getTransfersCurrentUser();
        if (transfers != null) {
            System.out.println("Transfers for user: " + currentUser.getUser().getUsername());
            for (Transfer transfer : transfers) {
                System.out.println(transfer.toString());
            }
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        Transfer newTransfer = new Transfer();
        newTransfer.setTransferTypeId(TransferType.SEND.getTypeId());
        printAllUsers();
        int menuSelection = consoleService.promptForInt("Enter ID of user you are sending to: ");
        if (menuSelection != 0) {
            Long recipientId = (long) menuSelection;
            User recipient = accountService.getUserById(recipientId);
            printExternalAccounts(recipient);
            Long recipientAccountId = (long) consoleService.promptForInt("Enter ID of account sending to: ");
            Account toAccount = accountService.getAccountById(recipientAccountId);
            if (toAccount != null) {
                newTransfer.setAccountTo(toAccount.getAccountId());
            }
            BigDecimal amountToSend = consoleService.promptForBigDecimal("Enter amount: ");
            printUserAccounts();
            Long accountSelection = (long) consoleService.promptForInt("Enter ID of account sending from: ");
            Account fromAccount = accountService.getAccountById(accountSelection);
            if (fromAccount != null) {
                newTransfer.setAccountFrom(fromAccount.getAccountId());
                newTransfer.setAmount(amountToSend);
                try {
                    newTransfer.setTransferStatusId(transferService.validateTransfer(newTransfer, fromAccount.getBalance()).getStatusId());
                } catch (TransferServiceException e) {
                    newTransfer.setTransferStatusId(TransferStatus.REJECTED.getStatusId());
                    consoleService.printErrorMessage();
                    BasicLogger.log(e.getMessage());
                }
            }
            Transfer createdTransfer = transferService.createTransfer(newTransfer);
            if (createdTransfer == null) {
                consoleService.printErrorMessage();
                BasicLogger.log("Unable to create new transfer.");
            }
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

    private void printAllUsers() {
        User[] users = accountService.getUsers();
        if (users != null) {
            for (User user : users) {
                System.out.println("ID: " + user.getId() + "User Name: " + user.getUsername());
            }
        }
    }

    private void printUserAccounts() {
        Account[] accounts = accountService.getAccountsForUser(currentUser.getUser());
        if (accounts != null) {
            for (Account account : accounts) {
                System.out.println("ID: " + account.getAccountId() + "Balance: " + account.getBalance());
            }
        }
    }

    private void printExternalAccounts(User user) {
        Account[] accounts = accountService.getAccountsForUser(user);
        for (Account account : accounts) {
            System.out.println("ID: " + account.getAccountId());
        }
    }

}
