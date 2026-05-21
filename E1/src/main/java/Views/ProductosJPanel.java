package Views;

import Config.ImageStorage;
import Controllers.ProductController;
import Models.Category;
import Models.Product;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;

public class ProductosJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "productos";
    private static final String VIEW_TITLE = "Productos";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.CUBE, 28);

    private final ProductController controller = new ProductController();
    private final ProductTableModel productTableModel = new ProductTableModel();
    private final List<Product> currentProducts = new ArrayList<>();
    private final Map<Integer, String> categoryNames = new HashMap<>();

    private JTextField txtSearch;
    private JTable table;
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
        setLayout(new BorderLayout(0, 18));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
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

    private JPanel createHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        wrapper.add(
                ViewStyle.createTitlePanel(
                        VIEW_ICON,
                        "Gestion de Productos",
                        "Administre SKU, categoria, precio, peso e imagen con una interfaz mas ordenada."
                ),
                BorderLayout.NORTH
        );

        JPanel toolbarCard = ViewStyle.createToolbarCard();

        txtSearch = new JTextField(22);
        txtSearch.setPreferredSize(new Dimension(250, 36));
        installSearchDebounce();

        JButton btnClear = ViewStyle.createToolbarButton(
                "Limpiar",
                ViewIcons.build(FontAwesome.ERASER, 14, Color.WHITE),
                new Color(96, 125, 139)
        );
        btnClear.addActionListener(e -> clearFilters());

        JButton btnRefresh = ViewStyle.createToolbarButton(
                "Actualizar",
                ViewIcons.build(FontAwesome.REFRESH, 14, Color.WHITE),
                new Color(2, 136, 209)
        );
        btnRefresh.addActionListener(e -> loadProducts(txtSearch.getText()));

        JButton btnNew = ViewStyle.createToolbarButton(
                "Nuevo Producto",
                ViewIcons.build(FontAwesome.PLUS, 14, Color.WHITE),
                new Color(46, 125, 50)
        );
        btnNew.addActionListener(e -> openForm(null));

        toolbarCard.add(new JLabel("Buscar:"));
        toolbarCard.add(txtSearch);
        toolbarCard.add(btnClear);
        toolbarCard.add(btnRefresh);
        toolbarCard.add(btnNew);

        wrapper.add(toolbarCard, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createCenterPanel() {
        table = new JTable(productTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configureTable();
        return ViewStyle.createTableCard(table);
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        lblPageInfo = new JLabel("Pagina 1 de 1 | Total: 0");
        cmbPageSize = new JComboBox<>(new Integer[]{10, 20, 50});
        cmbPageSize.setPreferredSize(new Dimension(76, 30));
        cmbPageSize.addActionListener(e -> {
            currentPage = 1;
            renderCurrentPage();
        });

        leftPanel.add(lblPageInfo);
        leftPanel.add(new JLabel("Mostrar:"));
        leftPanel.add(cmbPageSize);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonsPanel.setOpaque(false);

        btnPrevious = ViewStyle.createToolbarButton("Anterior", null, new Color(96, 125, 139));
        btnPrevious.addActionListener(e -> changePage(-1));

        btnNext = ViewStyle.createToolbarButton("Siguiente", null, new Color(96, 125, 139));
        btnNext.addActionListener(e -> changePage(1));

        buttonsPanel.add(btnPrevious);
        buttonsPanel.add(btnNext);

        footerPanel.add(leftPanel, BorderLayout.WEST);
        footerPanel.add(buttonsPanel, BorderLayout.EAST);
        return footerPanel;
    }

    private void configureTable() {
        int actionsColumn = productTableModel.getActionColumnIndex();
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(actionsColumn).setPreferredWidth(140);
        table.getColumnModel().getColumn(actionsColumn).setMinWidth(140);
        table.getColumnModel().getColumn(actionsColumn).setMaxWidth(140);
        table.getColumnModel().getColumn(actionsColumn).setCellRenderer(new ProductActionCellRenderer());
        table.getColumnModel().getColumn(actionsColumn).setCellEditor(new ProductActionCellEditor());
    }

    private void installSearchDebounce() {
        Timer timer = new Timer(280, e -> {
            currentPage = 1;
            loadProducts(txtSearch.getText());
        });
        timer.setRepeats(false);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timer.restart();
            }
        });

        txtSearch.addActionListener(e -> {
            timer.stop();
            currentPage = 1;
            loadProducts(txtSearch.getText());
        });
    }

    private void loadProducts(String query) {
        try {
            categoryNames.clear();

            for (Category category : controller.listCategories()) {
                categoryNames.put(category.getIdCategory(), category.getCategoryName());
            }

            currentProducts.clear();
            currentProducts.addAll(controller.searchProducts(query == null ? "" : query.trim()));
            renderCurrentPage();
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void renderCurrentPage() {
        int totalPages = getTotalPages();
        currentPage = Math.min(Math.max(currentPage, 1), totalPages);

        int pageSize = getPageSize();
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, currentProducts.size());

        if (fromIndex > toIndex) {
            fromIndex = 0;
            toIndex = Math.min(pageSize, currentProducts.size());
        }

        productTableModel.setItems(currentProducts.subList(fromIndex, toIndex));
        lblPageInfo.setText("Pagina " + currentPage + " de " + totalPages + " | Total: " + currentProducts.size());
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

    private void changePage(int delta) {
        int nextPage = currentPage + delta;

        if (nextPage < 1 || nextPage > getTotalPages()) {
            return;
        }

        currentPage = nextPage;
        renderCurrentPage();
    }

    private void clearFilters() {
        txtSearch.setText("");
        currentPage = 1;
        loadProducts("");
    }

    private void openDetail(Product product) {
        JDialog dialog = new JDialog(resolveWindow(), "Detalle de Producto", Dialog.ModalityType.APPLICATION_MODAL);
        DialogUtils.applyDialogTheme(dialog);
        dialog.setLayout(new BorderLayout(0, 14));
        dialog.setPreferredSize(new Dimension(760, 420));

        dialog.add(DialogUtils.createHeader(
                ViewIcons.build(FontAwesome.CUBE, 24, Color.WHITE),
                "Detalle de Producto",
                "Datos comerciales y operativos del producto seleccionado.",
                new Color(27, 94, 157)
        ), BorderLayout.NORTH);

        JPanel card = DialogUtils.createCard("Ficha del producto");
        JPanel grid = new JPanel(new GridLayout(3, 2, 12, 12));
        grid.setOpaque(false);
        grid.add(DialogUtils.createInfoTile("SKU", product.getSku()));
        grid.add(DialogUtils.createInfoTile("Nombre", product.getProductName()));
        grid.add(DialogUtils.createInfoTile("Categoria", categoryNames.getOrDefault(product.getIdCategory(), "Sin categoria")));
        grid.add(DialogUtils.createInfoTile("Estado", defaultText(product.getStatus())));
        grid.add(DialogUtils.createInfoTile("Precio", String.valueOf(product.getUnitPrice())));
        grid.add(DialogUtils.createInfoTile("Peso kg", String.valueOf(product.getUnitWeightKg())));
        card.add(grid, BorderLayout.NORTH);

        JPanel notesGrid = new JPanel(new GridLayout(1, 2, 12, 12));
        notesGrid.setOpaque(false);
        notesGrid.add(DialogUtils.createInfoTile("Descripcion", defaultText(product.getDescription())));
        notesGrid.add(DialogUtils.createInfoTile("Imagen", defaultText(product.getImagePath())));
        card.add(notesGrid, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        JButton btnClose = ViewStyle.createToolbarButton("Cerrar", null, new Color(96, 125, 139));
        btnClose.addActionListener(e -> dialog.dispose());
        footer.add(btnClose);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
        content.add(card, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);

        dialog.add(content, BorderLayout.CENTER);
        dialog.pack();
        DialogUtils.centerAndLock(dialog, this);
        dialog.setVisible(true);
    }

    private void deleteProduct(Product product) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Eliminar el producto seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteProduct(product.getIdProduct());
            AppMessageDialog.showInfo(this, VIEW_TITLE, "Producto eliminado correctamente.");
            currentPage = 1;
            loadProducts(txtSearch.getText());
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void openForm(Product editingProduct) {
        boolean editing = editingProduct != null;
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                editing ? "Editar Producto" : "Nuevo Producto",
                true
        );
        DialogUtils.applyDialogTheme(dialog);
        dialog.setLayout(new BorderLayout(0, 14));
        dialog.setPreferredSize(new Dimension(860, 560));

        dialog.add(DialogUtils.createHeader(
                ViewIcons.build(editing ? FontAwesome.PENCIL : FontAwesome.PLUS, 24, Color.WHITE),
                editing ? "Editar Producto" : "Nuevo Producto",
                editing ? "Actualice la informacion comercial y de almacen." : "Registre un producto con datos completos e imagen.",
                new Color(27, 94, 157)
        ), BorderLayout.NORTH);

        JTextField txtSku = DialogUtils.styleInput(new JTextField(editing ? editingProduct.getSku() : ""));
        JTextField txtName = DialogUtils.styleInput(new JTextField(editing ? editingProduct.getProductName() : ""));
        JTextArea txtDescription = DialogUtils.styleTextArea(new JTextArea(
                editing && editingProduct.getDescription() != null ? editingProduct.getDescription() : "",
                5,
                24
        ));
        JTextField txtPrice = DialogUtils.styleInput(new JTextField(editing ? String.valueOf(editingProduct.getUnitPrice()) : "0"));
        JTextField txtWeight = DialogUtils.styleInput(new JTextField(editing ? String.valueOf(editingProduct.getUnitWeightKg()) : "0"));
        JTextField txtImage = DialogUtils.styleInput(new JTextField(editing ? editingProduct.getImagePath() : ""));
        txtImage.setEditable(false);

        JComboBox<Category> cmbCategory = DialogUtils.styleInput(new JComboBox<>());
        JComboBox<String> cmbStatus = DialogUtils.styleInput(new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"}));
        final File[] selectedImage = new File[1];

        loadCategoryCombo(cmbCategory);
        selectCategory(cmbCategory, editing ? editingProduct.getIdCategory() : 0);

        if (editing) {
            cmbStatus.setSelectedItem(editingProduct.getStatus());
        }

        JButton btnImage = ViewStyle.createToolbarButton(
                "Elegir Imagen",
                ViewIcons.build(FontAwesome.PICTURE_O, 14, Color.WHITE),
                new Color(2, 136, 209)
        );
        btnImage.addActionListener(e -> {
            File image = ImageStorage.chooseImage(dialog);

            if (image != null) {
                selectedImage[0] = image;
                txtImage.setText(image.getAbsolutePath());
            }
        });

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

        JPanel topGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        topGrid.setOpaque(false);

        JPanel generalCard = DialogUtils.createCard("Datos Generales");
        JPanel generalFields = new JPanel(new GridBagLayout());
        generalFields.setOpaque(false);
        addField(generalFields, 0, "SKU", txtSku);
        addField(generalFields, 1, "Nombre", txtName);
        addField(generalFields, 2, "Categoria", cmbCategory);
        addField(generalFields, 3, "Estado", cmbStatus);
        generalCard.add(generalFields, BorderLayout.CENTER);

        JPanel commercialCard = DialogUtils.createCard("Datos Comerciales");
        JPanel commercialFields = new JPanel(new GridBagLayout());
        commercialFields.setOpaque(false);
        addField(commercialFields, 0, "Precio", txtPrice);
        addField(commercialFields, 1, "Peso kg", txtWeight);
        addField(commercialFields, 2, "Imagen", txtImage);

        GridBagConstraints imageButtonConstraints = new GridBagConstraints();
        imageButtonConstraints.gridx = 1;
        imageButtonConstraints.gridy = 3;
        imageButtonConstraints.anchor = GridBagConstraints.WEST;
        commercialFields.add(btnImage, imageButtonConstraints);

        commercialCard.add(commercialFields, BorderLayout.CENTER);

        topGrid.add(generalCard);
        topGrid.add(commercialCard);

        JPanel descriptionCard = DialogUtils.createCard("Descripcion");
        descriptionCard.add(DialogUtils.createScrollPane(txtDescription), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        JButton btnCancel = ViewStyle.createToolbarButton("Cerrar", null, new Color(96, 125, 139));
        JButton btnSave = ViewStyle.createToolbarButton(
                "Guardar",
                ViewIcons.build(FontAwesome.FLOPPY_O, 14, Color.WHITE),
                new Color(46, 125, 50)
        );

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> saveProduct(dialog, editingProduct, txtSku, txtName, txtDescription, txtPrice, txtWeight, txtImage, cmbCategory, cmbStatus, selectedImage));

        footer.add(btnCancel);
        footer.add(btnSave);

        bodyPanel.add(topGrid, BorderLayout.NORTH);
        bodyPanel.add(descriptionCard, BorderLayout.CENTER);
        bodyPanel.add(footer, BorderLayout.SOUTH);

        dialog.add(bodyPanel, BorderLayout.CENTER);
        dialog.pack();
        DialogUtils.centerAndLock(dialog, this);
        dialog.setVisible(true);
    }

    private void saveProduct(
            JDialog dialog,
            Product editingProduct,
            JTextField txtSku,
            JTextField txtName,
            JTextArea txtDescription,
            JTextField txtPrice,
            JTextField txtWeight,
            JTextField txtImage,
            JComboBox<Category> cmbCategory,
            JComboBox<String> cmbStatus,
            File[] selectedImage
    ) {
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
            } else {
                product.setImagePath(txtImage.getText());
            }

            product.setStatus(String.valueOf(cmbStatus.getSelectedItem()));

            if (editingProduct == null) {
                controller.createProduct(product);
                AppMessageDialog.showInfo(dialog, VIEW_TITLE, "Producto creado correctamente.");
            } else {
                controller.updateProduct(product);
                AppMessageDialog.showInfo(dialog, VIEW_TITLE, "Producto actualizado correctamente.");
            }

            dialog.dispose();
            currentPage = 1;
            loadProducts(txtSearch.getText());
        } catch (NumberFormatException ex) {
            AppMessageDialog.showError(dialog, VIEW_TITLE, "Precio y peso deben ser numericos.");
        } catch (Exception ex) {
            AppMessageDialog.showError(dialog, VIEW_TITLE, ex.getMessage());
        }
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

    private void addField(JPanel parent, int row, String label, Component input) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(0, 0, 12, 16);
        parent.add(DialogUtils.createFieldLabel(label), labelConstraints);

        GridBagConstraints inputConstraints = new GridBagConstraints();
        inputConstraints.gridx = 1;
        inputConstraints.gridy = row;
        inputConstraints.weightx = 1;
        inputConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputConstraints.insets = new Insets(0, 0, 12, 0);
        parent.add(input, inputConstraints);
    }

    private Window resolveWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private String defaultText(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private class ProductTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 6;
        private final String[] columns = {"SKU", "Nombre", "Categoria", "Precio", "Peso kg", "Estado", "Acciones"};
        private final List<Product> items = new ArrayList<>();

        void setItems(List<Product> products) {
            items.clear();

            if (products != null) {
                items.addAll(products);
            }

            fireTableDataChanged();
        }

        Product getProductAt(int rowIndex) {
            return items.get(rowIndex);
        }

        int getActionColumnIndex() {
            return ACTIONS_COLUMN_INDEX;
        }

        @Override
        public int getRowCount() {
            return items.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == ACTIONS_COLUMN_INDEX;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product product = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return product.getSku();
                case 1:
                    return product.getProductName();
                case 2:
                    return categoryNames.getOrDefault(product.getIdCategory(), "Sin categoria");
                case 3:
                    return product.getUnitPrice();
                case 4:
                    return product.getUnitWeightKg();
                case 5:
                    return product.getStatus();
                case ACTIONS_COLUMN_INDEX:
                    return product;
                default:
                    return "";
            }
        }
    }

    private class ProductActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnDetail;
        private final JButton btnEdit;
        private final JButton btnDelete;

        ProductActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);
            btnDetail = ViewStyle.createTableIconButton(FontAwesome.EYE, "Ver detalle", new Color(69, 90, 100));
            btnEdit = ViewStyle.createTableIconButton(FontAwesome.PENCIL, "Editar", new Color(245, 124, 0));
            btnDelete = ViewStyle.createTableIconButton(FontAwesome.TRASH, "Eliminar", new Color(198, 40, 40));
            add(btnDetail);
            add(btnEdit);
            add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Color background = isSelected ? table.getSelectionBackground() : Color.WHITE;
            setBackground(background);
            btnDetail.setBackground(background);
            btnEdit.setBackground(background);
            btnDelete.setBackground(background);
            return this;
        }
    }

    private class ProductActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private int editingRow = -1;

        ProductActionCellEditor() {
            panel.setOpaque(true);
            JButton btnDetail = ViewStyle.createTableIconButton(FontAwesome.EYE, "Ver detalle", new Color(69, 90, 100));
            JButton btnEdit = ViewStyle.createTableIconButton(FontAwesome.PENCIL, "Editar", new Color(245, 124, 0));
            JButton btnDelete = ViewStyle.createTableIconButton(FontAwesome.TRASH, "Eliminar", new Color(198, 40, 40));

            btnDetail.addActionListener(e -> handleAction("detail"));
            btnEdit.addActionListener(e -> handleAction("edit"));
            btnDelete.addActionListener(e -> handleAction("delete"));

            panel.add(btnDetail);
            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column
        ) {
            editingRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        private void handleAction(String action) {
            int modelRow = table.convertRowIndexToModel(editingRow);
            Product product = productTableModel.getProductAt(modelRow);
            fireEditingStopped();

            if ("detail".equals(action)) {
                openDetail(product);
            } else if ("edit".equals(action)) {
                openForm(product);
            } else {
                deleteProduct(product);
            }
        }
    }
}
