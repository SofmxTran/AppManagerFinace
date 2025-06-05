package app.vku.vn.financemanager.dialog;

import app.vku.vn.financemanager.dao.CategoryDAO;
import app.vku.vn.financemanager.model.Category;

import javax.swing.*;
import java.awt.*;

public class CategoryDialog extends JDialog {
    private JTextField nameField;
    private JTextField descriptionField;
    private CategoryDAO categoryDAO;
    private Category category;
    private boolean confirmed = false;

    public CategoryDialog(Window owner, String title, Category category) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.category = category;
        this.categoryDAO = new CategoryDAO();
        initComponents();
        if (category != null) {
            loadCategoryData();
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Tên danh mục:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Mô tả:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        add(inputPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(e -> saveCategory());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCategoryData() {
        nameField.setText(category.getName());
        descriptionField.setText(category.getDescription());
    }

    private void saveCategory() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên danh mục!");
            return;
        }

        if (category == null) {
            category = new Category(name, description);
            categoryDAO.save(category);
        } else {
            category.setName(name);
            category.setDescription(description);
            categoryDAO.update(category);
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Category getCategory() {
        return category;
    }
} 