package app.vku.vn.financemanager.dialog;

import app.vku.vn.financemanager.dao.CategoryDAO;
import app.vku.vn.financemanager.model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCategoriesDialog extends JDialog {
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private CategoryDAO categoryDAO;

    public ManageCategoriesDialog(Frame owner) {
        super(owner, "Quản lý danh mục", true);
        this.categoryDAO = new CategoryDAO();
        initComponents();
        loadCategories();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Create table
        String[] columnNames = {"ID", "Tên danh mục", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Thêm");
        JButton editButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton closeButton = new JButton("Đóng");

        addButton.addActionListener(e -> addCategory());
        editButton.addActionListener(e -> editCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCategories() {
        tableModel.setRowCount(0);
        List<Category> categories = categoryDAO.getAll();
        if (categories != null) {
            for (Category category : categories) {
                Object[] row = {
                    category.getId(),
                    category.getName(),
                    category.getDescription()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void addCategory() {
        CategoryDialog dialog = new CategoryDialog(this, "Thêm danh mục mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadCategories();
        }
    }

    private void editCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục cần sửa!");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        Category category = categoryDAO.getById(id);
        if (category != null) {
            CategoryDialog dialog = new CategoryDialog(this, "Sửa danh mục", category);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                loadCategories();
            }
        }
    }

    private void deleteCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa danh mục này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            categoryDAO.delete(id);
            loadCategories();
            JOptionPane.showMessageDialog(this, "Xóa danh mục thành công!");
        }
    }
}