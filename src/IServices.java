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
    void createAccount(String username, String accountNumber, String ownerName) throws RemoteException;
    void deleteAccount(String accountNumber) throws RemoteException;
    ArrayList<Account> getAllAccounts() throws RemoteException;
    void deposit(String accountNumber, double amount) throws RemoteException;
    void withdraw(String accountNumber, double amount) throws RemoteException;
    void transfer(String fromAccountNumber, String toAccountNumber, double amount) throws RemoteException;
    double getBalance(String accountNumber) throws RemoteException;
    ArrayList<Transaction> getTransactions(String accountNumber) throws RemoteException;
    int getAccountCount() throws RemoteException;
    double getTotalFunds() throws RemoteException;
    ArrayList<Transaction> getAllTransactions() throws RemoteException;
}