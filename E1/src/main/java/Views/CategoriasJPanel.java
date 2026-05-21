package Views;

import Config.ImageStorage;
import Controllers.CategoryController;
import Models.Category;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;

public class CategoriasJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "categorias";
    private static final String VIEW_TITLE = "Categorias";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.TAGS, 28);

    private final CategoryController controller = new CategoryController();
    private final List<Category> currentCategories = new ArrayList<>();

    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<Integer> cmbPageSize;
    private JLabel lblPageInfo;
    private JButton btnPrevious;
    private JButton btnNext;
    private int currentPage = 1;

    public CategoriasJPanel() {
        initPanel();
        loadCategories("");
    }

    @Override
    public void initPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(Color.WHITE);

        txtSearch = new JTextField(22);
        JButton btnSearch = new JButton("Buscar");
        JButton btnNew = new JButton("Nueva categoria");
        JButton btnRefresh = new JButton("Actualizar");
        cmbPageSize = new JComboBox<>(new Integer[]{10, 20, 50});

        toolbar.add(new JLabel("Buscar:"));
        toolbar.add(txtSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnNew);
        toolbar.add(btnRefresh);
        toolbar.add(new JLabel("Filas:"));
        toolbar.add(cmbPageSize);
        add(toolbar, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Acciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(3).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ActionCellEditor());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        paginationPanel.setBackground(Color.WHITE);
        btnPrevious = new JButton("Anterior");
        btnNext = new JButton("Siguiente");
        lblPageInfo = new JLabel("Pagina 1 de 1");
        paginationPanel.add(btnPrevious);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNext);
        add(paginationPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> {
            currentPage = 1;
            loadCategories(txtSearch.getText());
        });
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            currentPage = 1;
            loadCategories("");
        });
        btnNew.addActionListener(e -> openForm(null));
        cmbPageSize.addActionListener(e -> {
            currentPage = 1;
            renderCurrentPage();
        });
        btnPrevious.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderCurrentPage();
            }
        });
        btnNext.addActionListener(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                renderCurrentPage();
            }
        });
    }

    @Override
    public String getViewKey() {
        return VIEW_KEY;
    }

    @Override
    public String getViewTitle() {
        return VIEW_TITLE;
    }

    @Override
    public Icon getViewIcon() {
        return VIEW_ICON;
    }

    private void loadCategories(String query) {
        try {
            currentCategories.clear();
            currentCategories.addAll(controller.searchCategories(query));
            renderCurrentPage();
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void renderCurrentPage() {
        model.setRowCount(0);

        int pageSize = getPageSize();
        int totalPages = getTotalPages();
        currentPage = Math.min(Math.max(currentPage, 1), totalPages);
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, currentCategories.size());

        for (int index = fromIndex; index < toIndex; index++) {
            Category category = currentCategories.get(index);
            model.addRow(new Object[]{
                category.getIdCategory(),
                category.getCategoryName(),
                category.getDescription(),
                "Acciones"
            });
        }

        lblPageInfo.setText("Pagina " + currentPage + " de " + totalPages + " (" + currentCategories.size() + " registros)");
        btnPrevious.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private int getPageSize() {
        Integer selected = (Integer) cmbPageSize.getSelectedItem();
        return selected == null || selected <= 0 ? 10 : selected;
    }

    private int getTotalPages() {
        if (currentCategories.isEmpty()) {
            return 1;
        }

        return (int) Math.ceil((double) currentCategories.size() / getPageSize());
    }

    private Category getCategoryAt(int row) {
        int modelRow = table.convertRowIndexToModel(row);
        int index = ((currentPage - 1) * getPageSize()) + modelRow;
        return currentCategories.get(index);
    }

    private void openDetail(Category category) {
        AppMessageDialog.showInfo(
                this,
                VIEW_TITLE,
                "ID: " + category.getIdCategory()
                + "\nNombre: " + category.getCategoryName()
                + "\nDescripcion: " + defaultText(category.getDescription())
                + "\nImagen: " + defaultText(category.getImagePath())
        );
    }

    private void deleteCategory(Category category) {
        int option = JOptionPane.showConfirmDialog(this, "Eliminar la categoria seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

            try {
                controller.deleteCategory(category.getIdCategory());
                currentPage = 1;
                loadCategories(txtSearch.getText());
            } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void openForm(Category editingCategory) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editingCategory == null ? "Nueva Categoria" : "Editar Categoria", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(480, 260);
        dialog.setLocationRelativeTo(this);

        JTextField txtName = new JTextField(editingCategory == null ? "" : editingCategory.getCategoryName());
        JTextField txtDescription = new JTextField(editingCategory == null ? "" : editingCategory.getDescription());
        JTextField txtImage = new JTextField(editingCategory == null ? "" : editingCategory.getImagePath());
        txtImage.setEditable(false);

        final File[] selectedImage = new File[1];
        JButton btnImage = new JButton("Elegir imagen");
        btnImage.addActionListener(e -> {
            File image = ImageStorage.chooseImage(dialog);

            if (image != null) {
                selectedImage[0] = image;
                txtImage.setText(image.getAbsolutePath());
            }
        });

        dialog.add(new JLabel(" Name:"));
        dialog.add(txtName);
        dialog.add(new JLabel(" Description:"));
        dialog.add(txtDescription);
        dialog.add(new JLabel(" Image:"));
        dialog.add(txtImage);
        dialog.add(new JLabel());
        dialog.add(btnImage);

        JButton btnSave = new JButton("Guardar");
        btnSave.addActionListener(e -> {
            try {
                Category category = editingCategory == null ? new Category() : editingCategory;
                category.setCategoryName(txtName.getText());
                category.setDescription(txtDescription.getText());

                if (selectedImage[0] != null) {
                    category.setImagePath(ImageStorage.saveImage(selectedImage[0], "categories", txtName.getText()));
                } else if (editingCategory == null) {
                    category.setImagePath("");
                }

                if (editingCategory == null) {
                    controller.createCategory(category);
                } else {
                    controller.updateCategory(category);
                }

                dialog.dispose();
                currentPage = 1;
                loadCategories(txtSearch.getText());
            } catch (Exception ex) {
                AppMessageDialog.showError(dialog, VIEW_TITLE, ex.getMessage());
            }
        });

        JButton btnCancel = new JButton("Cerrar");
        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnCancel);
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private String defaultText(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private class ActionCellRenderer extends JPanel implements TableCellRenderer {

        ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
            add(new JButton("Ver"));
            add(new JButton("Editar"));
            add(new JButton("Eliminar"));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    private class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        private int row;

        ActionCellEditor() {
            JButton btnView = new JButton("Ver");
            JButton btnEdit = new JButton("Editar");
            JButton btnDelete = new JButton("Eliminar");

            btnView.addActionListener(e -> {
                openDetail(getCategoryAt(row));
                fireEditingStopped();
            });
            btnEdit.addActionListener(e -> {
                openForm(getCategoryAt(row));
                fireEditingStopped();
            });
            btnDelete.addActionListener(e -> {
                deleteCategory(getCategoryAt(row));
                fireEditingStopped();
            });

            panel.add(btnView);
            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
