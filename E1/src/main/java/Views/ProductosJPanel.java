package Views;

import Config.ImageStorage;
import Controllers.ProductController;
import Models.Category;
import Models.Product;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
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

public class ProductosJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "productos";
    private static final String VIEW_TITLE = "Productos";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.CUBE, 28);

    private final ProductController controller = new ProductController();
    private final List<Product> currentProducts = new ArrayList<>();
    private final Map<Integer, String> categoryNames = new HashMap<>();

    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<Integer> cmbPageSize;
    private JLabel lblPageInfo;
    private JButton btnPrevious;
    private JButton btnNext;
    private int currentPage = 1;

    public ProductosJPanel() {
        initPanel();
        loadProducts("");
    }

    @Override
    public void initPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(Color.WHITE);

        txtSearch = new JTextField(22);
        JButton btnSearch = new JButton("Buscar");
        JButton btnNew = new JButton("Nuevo producto");
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

        model = new DefaultTableModel(new String[]{"SKU", "Nombre", "Categoria", "Estado", "Acciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionCellEditor());
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
            loadProducts(txtSearch.getText());
        });
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            currentPage = 1;
            loadProducts("");
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

    private void loadProducts(String query) {
        try {
            categoryNames.clear();

            for (Category category : controller.listCategories()) {
                categoryNames.put(category.getIdCategory(), category.getCategoryName());
            }

            currentProducts.clear();
            currentProducts.addAll(controller.searchProducts(query));
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
        int toIndex = Math.min(fromIndex + pageSize, currentProducts.size());

        for (int index = fromIndex; index < toIndex; index++) {
            Product product = currentProducts.get(index);
            model.addRow(new Object[]{
                product.getSku(),
                product.getProductName(),
                categoryNames.getOrDefault(product.getIdCategory(), "Sin categoria"),
                product.getStatus(),
                "Acciones"
            });
        }

        lblPageInfo.setText("Pagina " + currentPage + " de " + totalPages + " (" + currentProducts.size() + " registros)");
        btnPrevious.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private int getPageSize() {
        Integer selected = (Integer) cmbPageSize.getSelectedItem();
        return selected == null || selected <= 0 ? 10 : selected;
    }

    private int getTotalPages() {
        if (currentProducts.isEmpty()) {
            return 1;
        }

        return (int) Math.ceil((double) currentProducts.size() / getPageSize());
    }

    private Product getProductAt(int row) {
        int modelRow = table.convertRowIndexToModel(row);
        int index = ((currentPage - 1) * getPageSize()) + modelRow;
        return currentProducts.get(index);
    }

    private void openDetail(Product product) {
        AppMessageDialog.showInfo(
                this,
                VIEW_TITLE,
                "ID: " + product.getIdProduct()
                + "\nSKU: " + product.getSku()
                + "\nNombre: " + product.getProductName()
                + "\nCategoria: " + categoryNames.getOrDefault(product.getIdCategory(), "-")
                + "\nDescripcion: " + defaultText(product.getDescription())
                + "\nPrecio: " + product.getUnitPrice()
                + "\nPeso kg: " + product.getUnitWeightKg()
                + "\nEstado: " + product.getStatus()
                + "\nImagen: " + defaultText(product.getImagePath())
        );
    }

    private void deleteProduct(Product product) {
        int option = JOptionPane.showConfirmDialog(this, "Eliminar el producto seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteProduct(product.getIdProduct());
            currentPage = 1;
            loadProducts(txtSearch.getText());
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void openForm(Product editingProduct) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editingProduct == null ? "Nuevo Producto" : "Editar Producto", true);
        dialog.setLayout(new GridLayout(9, 2, 10, 10));
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);

        JTextField txtSku = new JTextField(editingProduct == null ? "" : editingProduct.getSku());
        JTextField txtName = new JTextField(editingProduct == null ? "" : editingProduct.getProductName());
        JTextField txtDescription = new JTextField(editingProduct == null ? "" : editingProduct.getDescription());
        JTextField txtPrice = new JTextField(editingProduct == null ? "0" : String.valueOf(editingProduct.getUnitPrice()));
        JTextField txtWeight = new JTextField(editingProduct == null ? "0" : String.valueOf(editingProduct.getUnitWeightKg()));
        JTextField txtImage = new JTextField(editingProduct == null ? "" : editingProduct.getImagePath());
        txtImage.setEditable(false);

        JComboBox<Category> cmbCategory = new JComboBox<>();
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        final File[] selectedImage = new File[1];

        loadCategoryCombo(cmbCategory);
        selectCategory(cmbCategory, editingProduct == null ? 0 : editingProduct.getIdCategory());

        if (editingProduct != null) {
            cmbStatus.setSelectedItem(editingProduct.getStatus());
        }

        JButton btnImage = new JButton("Elegir imagen");
        btnImage.addActionListener(e -> {
            File image = ImageStorage.chooseImage(dialog);

            if (image != null) {
                selectedImage[0] = image;
                txtImage.setText(image.getAbsolutePath());
            }
        });

        dialog.add(new JLabel(" SKU:"));
        dialog.add(txtSku);
        dialog.add(new JLabel(" Nombre:"));
        dialog.add(txtName);
        dialog.add(new JLabel(" Categoria:"));
        dialog.add(cmbCategory);
        dialog.add(new JLabel(" Descripcion:"));
        dialog.add(txtDescription);
        dialog.add(new JLabel(" Precio:"));
        dialog.add(txtPrice);
        dialog.add(new JLabel(" Peso kg:"));
        dialog.add(txtWeight);
        dialog.add(new JLabel(" Estado:"));
        dialog.add(cmbStatus);
        dialog.add(new JLabel(" Image:"));
        dialog.add(txtImage);
        dialog.add(new JLabel());
        dialog.add(btnImage);

        JButton btnSave = new JButton("Guardar");
        btnSave.addActionListener(e -> {
            try {
                Category category = (Category) cmbCategory.getSelectedItem();

                if (category == null || category.getIdCategory() <= 0) {
                    throw new Exception("Seleccione una categoria.");
                }

                Product product = editingProduct == null ? new Product() : editingProduct;
                product.setSku(txtSku.getText());
                product.setProductName(txtName.getText());
                product.setDescription(txtDescription.getText());
                product.setIdCategory(category.getIdCategory());
                product.setUnitPrice(new BigDecimal(txtPrice.getText().trim()));
                product.setUnitWeightKg(new BigDecimal(txtWeight.getText().trim()));

                if (selectedImage[0] != null) {
                    product.setImagePath(ImageStorage.saveImage(selectedImage[0], "products", txtName.getText()));
                } else if (editingProduct == null) {
                    product.setImagePath("");
                }

                product.setStatus(String.valueOf(cmbStatus.getSelectedItem()));

                if (editingProduct == null) {
                    controller.createProduct(product);
                } else {
                    controller.updateProduct(product);
                }

                dialog.dispose();
                currentPage = 1;
                loadProducts(txtSearch.getText());
            } catch (NumberFormatException ex) {
                AppMessageDialog.showError(dialog, VIEW_TITLE, "Precio y peso deben ser numericos.");
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

    private void loadCategoryCombo(JComboBox<Category> comboBox) {
        DefaultComboBoxModel<Category> comboModel = new DefaultComboBoxModel<>();

        try {
            for (Category category : controller.listCategories()) {
                comboModel.addElement(category);
            }
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }

        comboBox.setModel(comboModel);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Category) {
                    setText(((Category) value).getCategoryName());
                }

                return this;
            }
        });
    }

    private void selectCategory(JComboBox<Category> comboBox, int idCategory) {
        for (int index = 0; index < comboBox.getItemCount(); index++) {
            Category category = comboBox.getItemAt(index);

            if (category.getIdCategory() == idCategory) {
                comboBox.setSelectedIndex(index);
                return;
            }
        }
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
                openDetail(getProductAt(row));
                fireEditingStopped();
            });
            btnEdit.addActionListener(e -> {
                openForm(getProductAt(row));
                fireEditingStopped();
            });
            btnDelete.addActionListener(e -> {
                deleteProduct(getProductAt(row));
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
