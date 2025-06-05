package app.vku.vn.financemanager.dialog;

import app.vku.vn.financemanager.dao.CategoryDAO;
import app.vku.vn.financemanager.dao.TransactionDAO;
import app.vku.vn.financemanager.model.Category;
import app.vku.vn.financemanager.model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionDialog extends JDialog {
    private JComboBox<Category> categoryComboBox;
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<Transaction.TransactionType> typeComboBox;
    private CategoryDAO categoryDAO;
    private TransactionDAO transactionDAO;
    private Transaction transaction;
    private boolean confirmed = false;

    public TransactionDialog(Frame owner, String title, Transaction transaction) {
        super(owner, title, true);
        this.transaction = transaction;
        this.categoryDAO = new CategoryDAO();
        this.transactionDAO = new TransactionDAO();
        initComponents();
        loadCategories();
        if (transaction != null) {
            loadTransactionData();
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Category selection
        inputPanel.add(new JLabel("Danh mục:"));
        categoryComboBox = new JComboBox<>();
        inputPanel.add(categoryComboBox);

        // Amount input
        inputPanel.add(new JLabel("Số tiền:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        // Description input
        inputPanel.add(new JLabel("Mô tả:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        // Transaction type
        inputPanel.add(new JLabel("Loại giao dịch:"));
        typeComboBox = new JComboBox<>(Transaction.TransactionType.values());
        inputPanel.add(typeComboBox);

        add(inputPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> saveTransaction());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
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

    private void loadTransactionData() {
        categoryComboBox.setSelectedItem(transaction.getCategory());
        amountField.setText(transaction.getAmount().toString());
        descriptionField.setText(transaction.getDescription());
        typeComboBox.setSelectedItem(transaction.getType());
    }

    private void saveTransaction() {
        try {
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục!");
                return;
            }

            BigDecimal amount = new BigDecimal(amountField.getText());
            String description = descriptionField.getText();
            Transaction.TransactionType type = (Transaction.TransactionType) typeComboBox.getSelectedItem();

            if (transaction == null) {
                transaction = new Transaction(description, amount, LocalDateTime.now(), type, selectedCategory);
                transactionDAO.save(transaction);
            } else {
                transaction.setCategory(selectedCategory);
                transaction.setAmount(amount);
                transaction.setDescription(description);
                transaction.setType(type);
                transactionDAO.update(transaction);
            }

            confirmed = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Transaction getTransaction() {
        return transaction;
    }
} 