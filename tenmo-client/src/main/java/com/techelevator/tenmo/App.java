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
        Long recipientAccountId;
        Transfer newTransfer = new Transfer();
        newTransfer.setTransferTypeId(TransferType.SEND.getTypeId());
        int menuSelection = -1;
        while (menuSelection != 0) {
            printAllUsers();
            menuSelection = consoleService.promptForInt("Enter ID of user you are sending to: ");
            Long recipientId = (long) menuSelection;
            if (!checkValidUserId(recipientId)) {
                BasicLogger.log("Unable to find User with Id: " + recipientId);
                consoleService.printErrorMessage();
                continue;
            }
            printExternalAccounts(recipientId);
            menuSelection = consoleService.promptForInt("Enter ID of account sending to: ");
            if (menuSelection != 0) {
                recipientAccountId = (long) menuSelection;
            } else {
                continue;
            }
            if (!checkValidUserAccount(recipientId, recipientAccountId)) {
                BasicLogger.log("Unable to find account: " + recipientAccountId
                        + " or account does not belong to user ID: " + recipientId);
                consoleService.printErrorMessage();
                continue;
            }
            newTransfer.setAccountTo(recipientAccountId);
            BigDecimal amountToSend = consoleService.promptForBigDecimal("Enter amount: ");
            printUserAccounts();
            menuSelection = consoleService.promptForInt("Enter ID of account sending from: ");
            Long accountSelection = (long) menuSelection;
            if (!checkValidUserAccount(currentUser.getUser().getId(), accountSelection)) {
                BasicLogger.log("Unable to find account: " + accountSelection
                        + " or account does not belong to user ID: " + currentUser.getUser().getId());
                consoleService.printErrorMessage();
                continue;
            }
            if (!checkSufficientFunds(amountToSend, currentUser.getUser().getId())) {
                BasicLogger.log("Insufficient funds in account: " + accountSelection);
                consoleService.printErrorMessage();
                continue;
            }
            newTransfer.setAccountFrom(accountSelection);
            newTransfer.setAmount(amountToSend);
            if (!checkValidTransfer(newTransfer)) {
                BasicLogger.log("User must send a positive amount to a different account.");
                consoleService.printErrorMessage();
                continue;
            }
            newTransfer.setTransferStatusId(TransferStatus.APPROVED.getStatusId());
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
                System.out.println("ID: " + user.getId() + " User Name: " + user.getUsername());
            }
        }
    }

    private void printUserAccounts() {
        Account[] accounts = accountService.getAccountsForUser(currentUser.getUser().getId());
        if (accounts != null) {
            for (Account account : accounts) {
                System.out.println("ID: " + account.getAccountId() + " Balance: " + account.getBalance());
            }
        }
    }

    private void printExternalAccounts(Long userId) {
        Account[] accounts = accountService.getAccountsForUser(userId);
        for (Account account : accounts) {
            System.out.println("ID: " + account.getAccountId());
        }
    }

    private boolean checkValidUserId(Long id) {
        User user;
        user = accountService.getUserById(id);
        return user != null;
    }

    private boolean checkValidUserAccount(Long userId, Long accountId) {
        if (!checkValidUserId(userId)) {
            return false;
        }
        Account account;
        account = accountService.getAccountById(accountId);
        return (account != null && account.getUserId().equals(userId));
    }

    private boolean checkValidTransfer(Transfer transfer) {

        if (transfer.getAccountFrom().equals(transfer.getAccountTo())) {
            return false;
        }
        return transfer.getAmount().compareTo(new BigDecimal("0.00")) > 0;
    }

    private boolean checkSufficientFunds(BigDecimal amount, Long accountId) {
        BigDecimal accountBalance;
        accountBalance = accountService.getBalanceForAccountId(accountId);
        if (accountBalance == null) {
            return false;
        }
        return amount.compareTo(accountBalance) <= 0;
    }

}
