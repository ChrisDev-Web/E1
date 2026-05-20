package Views;

import Controllers.ProductController;
import Models.Category;
import Models.Product;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public ProductosJPanel() {
        initPanel();
        loadProducts();
    }

    @Override
    public void initPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(Color.WHITE);

        JButton btnNew = new JButton("Nuevo producto");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");

        toolbar.add(btnNew);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);
        add(toolbar, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "SKU", "Nombre", "Categoria", "Precio", "Peso", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnNew.addActionListener(e -> openForm(null));
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadProducts());
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

    private void loadProducts() {
        try {
            categoryNames.clear();

            for (Category category : controller.listCategories()) {
                categoryNames.put(category.getIdCategory(), category.getCategoryName());
            }

            currentProducts.clear();
            currentProducts.addAll(controller.listProducts());
            model.setRowCount(0);

            for (Product product : currentProducts) {
                model.addRow(new Object[]{
                    product.getIdProduct(),
                    product.getSku(),
                    product.getProductName(),
                    categoryNames.getOrDefault(product.getIdCategory(), "Sin categoria"),
                    product.getUnitPrice(),
                    product.getUnitWeightKg(),
                    product.getStatus()
                });
            }
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();

        if (row == -1) {
            AppMessageDialog.showError(this, VIEW_TITLE, "Seleccione un producto.");
            return;
        }

        openForm(currentProducts.get(row));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();

        if (row == -1) {
            AppMessageDialog.showError(this, VIEW_TITLE, "Seleccione un producto.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Eliminar el producto seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteProduct(currentProducts.get(row).getIdProduct());
            loadProducts();
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void openForm(Product editingProduct) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editingProduct == null ? "Nuevo Producto" : "Editar Producto", true);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(480, 360);
        dialog.setLocationRelativeTo(this);

        JTextField txtSku = new JTextField(editingProduct == null ? "" : editingProduct.getSku());
        JTextField txtName = new JTextField(editingProduct == null ? "" : editingProduct.getProductName());
        JTextField txtDescription = new JTextField(editingProduct == null ? "" : editingProduct.getDescription());
        JTextField txtPrice = new JTextField(editingProduct == null ? "0" : String.valueOf(editingProduct.getUnitPrice()));
        JTextField txtWeight = new JTextField(editingProduct == null ? "0" : String.valueOf(editingProduct.getUnitWeightKg()));
        JComboBox<Category> cmbCategory = new JComboBox<>();
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});

        loadCategoryCombo(cmbCategory);
        selectCategory(cmbCategory, editingProduct == null ? 0 : editingProduct.getIdCategory());

        if (editingProduct != null) {
            cmbStatus.setSelectedItem(editingProduct.getStatus());
        }

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
                product.setImagePath("");
                product.setStatus(String.valueOf(cmbStatus.getSelectedItem()));

                if (editingProduct == null) {
                    controller.createProduct(product);
                } else {
                    controller.updateProduct(product);
                }

                dialog.dispose();
                loadProducts();
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
}
