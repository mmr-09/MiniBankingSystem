package models;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionTableModel extends AbstractTableModel {
    private final String[] columnNames = {
            "STT",          // số thứ tự tự động
            "Số TK",
            "Loại GD",
            "Số tiền",
            "Mô tả",
            "Ngày"
    };

    private List<Transaction> transactions = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public TransactionTableModel() {}

    public TransactionTableModel(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setTransactions(List<Transaction> list) {
        this.transactions = list;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return transactions == null ? 0 : transactions.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (transactions == null || rowIndex >= transactions.size()) return null;
        Transaction t = transactions.get(rowIndex);
        switch (columnIndex) {
            case 0: return rowIndex + 1;           // STT (bắt đầu từ 1)
            case 1: return t.getAccountNumber();
            case 2: return t.getType();
            case 3: return t.getAmount();          // 👉 Double gốc
            case 4: return t.getDescription();
            case 5: return sdf.format(t.getDate());
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // Giúp JTable biết kiểu dữ liệu để render/format
        if (columnIndex == 3) return Double.class; // Số tiền
        return String.class;
    }
}
