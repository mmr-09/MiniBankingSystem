package interfaces;

import models.Account;
import models.Transaction;
import models.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IServices extends Remote {
    User login(String username, String password) throws RemoteException;
    void register(String username, String password, String role) throws RemoteException;

    // <-- CHÚ Ý: 4 tham số như dưới (username, password, role, ownerName)
    void createAccount(String username, String password, String role, String ownerName) throws RemoteException;

    void deleteAccount(String accountNumber) throws RemoteException;
    ArrayList<Account> getAllAccounts() throws RemoteException;

    void deposit(String accountNumber, double amount) throws RemoteException;
    void withdraw(String accountNumber, double amount) throws RemoteException;
    void transfer(String fromAccountNumber, String toAccountNumber, double amount) throws RemoteException;
    double getBalance(String accountNumber) throws RemoteException;
    ArrayList<Transaction> getTransactions(String accountNumber) throws RemoteException;
    ArrayList<Transaction> getAllTransactions() throws RemoteException;

    int getAccountCount() throws RemoteException;
    double getTotalFunds() throws RemoteException;
}