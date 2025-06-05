package app.vku.vn.financemanager;

import app.vku.vn.financemanager.dao.CategoryDAO;
import app.vku.vn.financemanager.dao.TransactionDAO;
import app.vku.vn.financemanager.dialog.CategoryDialog;
import app.vku.vn.financemanager.dialog.ManageCategoriesDialog;
import app.vku.vn.financemanager.dialog.TransactionDialog;
import app.vku.vn.financemanager.model.Category;
import app.vku.vn.financemanager.model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainWindow extends JFrame {
    private JTable transactionTable;
    private JComboBox<Category> categoryComboBox;
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<Transaction.TransactionType> typeComboBox;
    private DefaultTableModel tableModel;
    private CategoryDAO categoryDAO;
    private TransactionDAO transactionDAO;

    public MainWindow() {
        categoryDAO = new CategoryDAO();
        transactionDAO = new TransactionDAO();

        setTitle("Quản Lý Tài Chính");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create menu bar
        createMenuBar();

        // Create input panel
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Create table panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load initial data
        loadCategories();
        loadTransactions();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Danh mục");
        
        JMenuItem addCategoryItem = new JMenuItem("Thêm danh mục");
        JMenuItem manageCategoriesItem = new JMenuItem("Quản lý danh mục");
        
        addCategoryItem.addActionListener(e -> showAddCategoryDialog());
        manageCategoriesItem.addActionListener(e -> showManageCategoriesDialog());
        
        menu.add(addCategoryItem);
        menu.add(manageCategoriesItem);
        menuBar.add(menu);
        
        setJMenuBar(menuBar);
    }

    private void showAddCategoryDialog() {
        CategoryDialog dialog = new CategoryDialog(this, "Thêm danh mục mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadCategories();
        }
    }

    private void showManageCategoriesDialog() {
        ManageCategoriesDialog dialog = new ManageCategoriesDialog(this);
        dialog.setVisible(true);
        loadCategories();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Category selection
        panel.add(new JLabel("Danh mục:"));
        categoryComboBox = new JComboBox<>();
        panel.add(categoryComboBox);

        // Amount input
        panel.add(new JLabel("Số tiền:"));
        amountField = new JTextField();
        panel.add(amountField);

        // Description input
        panel.add(new JLabel("Mô tả:"));
        descriptionField = new JTextField();
        panel.add(descriptionField);

        // Transaction type
        panel.add(new JLabel("Loại giao dịch:"));
        typeComboBox = new JComboBox<>(Transaction.TransactionType.values());
        panel.add(typeComboBox);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        String[] columnNames = {"ID", "Danh mục", "Số tiền", "Mô tả", "Loại", "Ngày"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Thêm giao dịch");
        JButton editButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton refreshButton = new JButton("Làm mới");

        // Add action listeners
        addButton.addActionListener(e -> addTransaction());
        editButton.addActionListener(e -> editTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        refreshButton.addActionListener(e -> refreshData());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private void loadCategories() {
        categoryComboBox.removeAllItems();
        List<Category> categories = categoryDAO.getAll();
        if (categories != null) {
            for (Category category : categories) {
                categoryComboBox.addItem(category);
            }
        }
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = transactionDAO.getAll();
        if (transactions != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Transaction transaction : transactions) {
                Object[] row = {
                    transaction.getId(),
                    transaction.getCategory().getName(),
                    transaction.getAmount(),
                    transaction.getDescription(),
                    transaction.getType(),
                    transaction.getDate().format(formatter)
                };
                tableModel.addRow(row);
            }
        }
    }

    private void addTransaction() {
        try {
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục!");
                return;
            }

            BigDecimal amount = new BigDecimal(amountField.getText());
            String description = descriptionField.getText();
            Transaction.TransactionType type = (Transaction.TransactionType) typeComboBox.getSelectedItem();

            Transaction transaction = new Transaction(description, amount, LocalDateTime.now(), type, selectedCategory);
            transactionDAO.save(transaction);

            clearInputFields();
            loadTransactions();
            JOptionPane.showMessageDialog(this, "Thêm giao dịch thành công!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void editTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giao dịch cần sửa!");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        Transaction transaction = transactionDAO.getById(id);
        if (transaction != null) {
            TransactionDialog dialog = new TransactionDialog(this, "Sửa giao dịch", transaction);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                loadTransactions();
            }
        }
    }

    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giao dịch cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa giao dịch này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            transactionDAO.delete(id);
            loadTransactions();
            JOptionPane.showMessageDialog(this, "Xóa giao dịch thành công!");
        }
    }

    private void refreshData() {
        loadCategories();
        loadTransactions();
    }

    private void clearInputFields() {
        amountField.setText("");
        descriptionField.setText("");
        typeComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
