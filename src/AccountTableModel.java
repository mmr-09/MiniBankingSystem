package models;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AccountTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Số tài khoản", "Chủ tài khoản", "Số dư"};
    private List<Account> accounts = new ArrayList<>(); // ✅ đảm bảo khởi tạo

    public AccountTableModel() {} // ✅ constructor rỗng cho Admin

    public AccountTableModel(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public int getRowCount() {
        return accounts.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Account acc = accounts.get(rowIndex);
        switch (columnIndex) {
            case 0: return acc.getAccountNumber();
            case 1: return acc.getOwnerName();
            case 2: return acc.getBalance();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // ✅ Thêm setter để Admin cập nhật dữ liệu
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        fireTableDataChanged();
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
