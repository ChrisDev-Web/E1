package Views;

import Controllers.InventarioController;
import Models.Inventory;
import Models.ReferenceItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
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

public class InventarioJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "inventario";
    private static final String VIEW_TITLE = "Inventario";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.CLIPBOARD, 28);

    private final InventarioController controller = new InventarioController();
    private final InventoryTableModel inventoryTableModel = new InventoryTableModel();
    private final List<Inventory> currentInventory = new ArrayList<>();

    private JTextField txtSearch;
    private JTable table;

    public InventarioJPanel() {
        initPanel();
        loadInventory();
    }

    @Override
    public void initPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
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
                        "Gestion de Inventario",
                        "Controle stock real, reservado, minimo y transferencias entre almacenes."
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

        JButton btnNew = ViewStyle.createToolbarButton(
                "Asignar Stock",
                ViewIcons.build(FontAwesome.PLUS, 14, Color.WHITE),
                new Color(46, 125, 50)
        );
        btnNew.addActionListener(e -> openInventoryForm(null));

        JButton btnRefresh = ViewStyle.createToolbarButton(
                "Actualizar",
                ViewIcons.build(FontAwesome.REFRESH, 14, Color.WHITE),
                new Color(2, 136, 209)
        );
        btnRefresh.addActionListener(e -> loadInventory());

        toolbarCard.add(new JLabel("Buscar:"));
        toolbarCard.add(txtSearch);
        toolbarCard.add(btnClear);
        toolbarCard.add(btnNew);
        toolbarCard.add(btnRefresh);

        wrapper.add(toolbarCard, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createCenterPanel() {
        table = new JTable(inventoryTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configureTable();
        return ViewStyle.createTableCard(table);
    }

    private void configureTable() {
        int actionsColumn = inventoryTableModel.getActionColumnIndex();
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(actionsColumn).setPreferredWidth(100);
        table.getColumnModel().getColumn(actionsColumn).setMinWidth(100);
        table.getColumnModel().getColumn(actionsColumn).setMaxWidth(100);
        table.getColumnModel().getColumn(actionsColumn).setCellRenderer(new InventoryActionCellRenderer());
        table.getColumnModel().getColumn(actionsColumn).setCellEditor(new InventoryActionCellEditor());
    }

    private void installSearchDebounce() {
        Timer timer = new Timer(280, e -> searchInventory());
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
            searchInventory();
        });
    }

    private void loadInventory() {
        try {
            currentInventory.clear();
            currentInventory.addAll(controller.listarActivos());
            inventoryTableModel.setItems(currentInventory);
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void searchInventory() {
        try {
            currentInventory.clear();
            currentInventory.addAll(controller.buscarInventario(txtSearch.getText()));
            inventoryTableModel.setItems(currentInventory);
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void clearFilters() {
        txtSearch.setText("");
        loadInventory();
    }

    private void openInventoryForm(Inventory inventory) {
        InventoryFormDialog dialog = new InventoryFormDialog(resolveWindow(), inventory);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadInventory();
        }
    }

    private void openTransferDialog(Inventory inventory) {
        TransferFormDialog dialog = new TransferFormDialog(resolveWindow(), inventory);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadInventory();
        }
    }

    private Window resolveWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private void loadReferenceModel(JComboBox<ReferenceItem> comboBox, ReferenceLoader loader) throws Exception {
        DefaultComboBoxModel<ReferenceItem> comboModel = new DefaultComboBoxModel<>();

        for (ReferenceItem item : loader.load()) {
            comboModel.addElement(item);
        }

        comboBox.setModel(comboModel);
    }

    private ReferenceItem getSelectedReference(JComboBox<ReferenceItem> comboBox, String message) throws Exception {
        ReferenceItem item = (ReferenceItem) comboBox.getSelectedItem();

        if (item == null || item.getId() <= 0) {
            throw new Exception(message);
        }

        return item;
    }

    private interface ReferenceLoader {
        List<ReferenceItem> load() throws Exception;
    }

    private class InventoryTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 6;
        private final String[] columns = {"Almacen", "SKU", "Producto", "Stock", "Reservado", "Minimo", "Acciones"};
        private final List<Inventory> items = new ArrayList<>();

        void setItems(List<Inventory> inventory) {
            items.clear();

            if (inventory != null) {
                items.addAll(inventory);
            }

            fireTableDataChanged();
        }

        Inventory getInventoryAt(int rowIndex) {
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
            Inventory inventory = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return inventory.getWarehouseName();
                case 1:
                    return inventory.getProductSku();
                case 2:
                    return inventory.getProductName();
                case 3:
                    return inventory.getStock();
                case 4:
                    return inventory.getReservedStock();
                case 5:
                    return inventory.getMinStock();
                case ACTIONS_COLUMN_INDEX:
                    return inventory;
                default:
                    return "";
            }
        }
    }

    private class InventoryActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnEdit;
        private final JButton btnTransfer;

        InventoryActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);
            btnEdit = ViewStyle.createTableIconButton(FontAwesome.PENCIL, "Ajustar stock", new Color(245, 124, 0));
            btnTransfer = ViewStyle.createTableIconButton(FontAwesome.EXCHANGE, "Transferir", new Color(2, 136, 209));
            add(btnEdit);
            add(btnTransfer);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Color background = isSelected ? table.getSelectionBackground() : Color.WHITE;
            setBackground(background);
            btnEdit.setBackground(background);
            btnTransfer.setBackground(background);
            return this;
        }
    }

    private class InventoryActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private int editingRow = -1;

        InventoryActionCellEditor() {
            panel.setOpaque(true);
            JButton btnEdit = ViewStyle.createTableIconButton(FontAwesome.PENCIL, "Ajustar stock", new Color(245, 124, 0));
            JButton btnTransfer = ViewStyle.createTableIconButton(FontAwesome.EXCHANGE, "Transferir", new Color(2, 136, 209));

            btnEdit.addActionListener(e -> handleAction("edit"));
            btnTransfer.addActionListener(e -> handleAction("transfer"));

            panel.add(btnEdit);
            panel.add(btnTransfer);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
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
            Inventory inventory = inventoryTableModel.getInventoryAt(modelRow);
            fireEditingStopped();

            if ("edit".equals(action)) {
                openInventoryForm(inventory);
            } else {
                openTransferDialog(inventory);
            }
        }
    }

    private class InventoryFormDialog extends JDialog {

        private final Inventory editingInventory;
        private boolean saved;

        private JComboBox<ReferenceItem> cmbWarehouse;
        private JComboBox<ReferenceItem> cmbProduct;
        private JTextField txtStock;
        private JTextField txtReserved;
        private JTextField txtMin;

        InventoryFormDialog(Window owner, Inventory inventory) {
            super(owner instanceof Frame ? (Frame) owner : null, true);
            this.editingInventory = inventory;
            buildDialog();
        }

        boolean isSaved() {
            return saved;
        }

        private void buildDialog() {
            setTitle(editingInventory == null ? "Asignar Stock" : "Ajustar Stock");
            DialogUtils.applyDialogTheme(this);
            setLayout(new BorderLayout(0, 14));
            setPreferredSize(new Dimension(760, 460));

            add(DialogUtils.createHeader(
                    ViewIcons.build(editingInventory == null ? FontAwesome.PLUS : FontAwesome.PENCIL, 24, Color.WHITE),
                    editingInventory == null ? "Asignar Stock" : "Ajustar Stock",
                    "Registre existencias fisicas, reservadas y stock minimo del producto.",
                    new Color(27, 94, 157)
            ), BorderLayout.NORTH);

            cmbWarehouse = DialogUtils.styleInput(new JComboBox<>());
            cmbProduct = DialogUtils.styleInput(new JComboBox<>());
            txtStock = DialogUtils.styleInput(new JTextField(editingInventory == null ? "0" : String.valueOf(editingInventory.getStock())));
            txtReserved = DialogUtils.styleInput(new JTextField(editingInventory == null ? "0" : String.valueOf(editingInventory.getReservedStock())));
            txtMin = DialogUtils.styleInput(new JTextField(editingInventory == null ? "0" : String.valueOf(editingInventory.getMinStock())));

            JPanel formCard = DialogUtils.createCard("Datos del inventario");
            JPanel fieldsPanel = new JPanel(new GridBagLayout());
            fieldsPanel.setOpaque(false);

            try {
                loadReferenceModel(cmbWarehouse, () -> controller.listarAlmacenes());
                loadReferenceModel(cmbProduct, () -> controller.listarProductos());
            } catch (Exception e) {
                AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
            }

            Component warehouseComponent = cmbWarehouse;
            Component productComponent = cmbProduct;

            if (editingInventory != null) {
                JTextField txtWarehouse = DialogUtils.styleInput(new JTextField(editingInventory.getWarehouseName()));
                txtWarehouse.setEditable(false);
                JTextField txtProduct = DialogUtils.styleInput(new JTextField(editingInventory.getProductSku() + " - " + editingInventory.getProductName()));
                txtProduct.setEditable(false);
                warehouseComponent = txtWarehouse;
                productComponent = txtProduct;
            }

            addField(fieldsPanel, 0, "Almacen", warehouseComponent);
            addField(fieldsPanel, 1, "Producto", productComponent);
            addField(fieldsPanel, 2, "Stock real", txtStock);
            addField(fieldsPanel, 3, "Reservado", txtReserved);
            addField(fieldsPanel, 4, "Stock minimo", txtMin);

            formCard.add(fieldsPanel, BorderLayout.CENTER);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            footer.setOpaque(false);

            JButton btnCancel = ViewStyle.createToolbarButton("Cerrar", null, new Color(96, 125, 139));
            JButton btnSave = ViewStyle.createToolbarButton(
                    editingInventory == null ? "Procesar" : "Guardar",
                    ViewIcons.build(FontAwesome.FLOPPY_O, 14, Color.WHITE),
                    new Color(46, 125, 50)
            );

            btnCancel.addActionListener(e -> dispose());
            btnSave.addActionListener(e -> saveInventory());

            footer.add(btnCancel);
            footer.add(btnSave);

            JPanel content = new JPanel(new BorderLayout(0, 14));
            content.setOpaque(false);
            content.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
            content.add(formCard, BorderLayout.CENTER);
            content.add(footer, BorderLayout.SOUTH);

            add(content, BorderLayout.CENTER);
            pack();
            DialogUtils.centerAndLock(this, InventarioJPanel.this);
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

        private void saveInventory() {
            try {
                int stock = Integer.parseInt(txtStock.getText().trim());
                int reserved = Integer.parseInt(txtReserved.getText().trim());
                int min = Integer.parseInt(txtMin.getText().trim());

                if (editingInventory == null) {
                    ReferenceItem warehouse = getSelectedReference(cmbWarehouse, "Seleccione un almacen.");
                    ReferenceItem product = getSelectedReference(cmbProduct, "Seleccione un producto.");
                    controller.registrarInventario(warehouse.getId(), product.getId(), stock, reserved, min);
                } else {
                    controller.modificarInventario(editingInventory.getIdInventory(), stock, reserved, min);
                }

                saved = true;
                AppMessageDialog.showInfo(this, VIEW_TITLE, editingInventory == null
                        ? "Inventario registrado correctamente."
                        : "Inventario actualizado correctamente.");
                dispose();
            } catch (NumberFormatException ex) {
                AppMessageDialog.showError(this, VIEW_TITLE, "Los campos numericos son invalidos.");
            } catch (Exception ex) {
                AppMessageDialog.showError(this, VIEW_TITLE, ex.getMessage());
            }
        }
    }

    private class TransferFormDialog extends JDialog {

        private final Inventory sourceInventory;
        private boolean saved;

        private JComboBox<ReferenceItem> cmbTargetWarehouse;
        private JTextField txtOrigin;
        private JTextField txtQuantity;

        TransferFormDialog(Window owner, Inventory inventory) {
            super(owner instanceof Frame ? (Frame) owner : null, true);
            this.sourceInventory = inventory;
            buildDialog();
        }

        boolean isSaved() {
            return saved;
        }

        private void buildDialog() {
            setTitle("Transferir Stock");
            DialogUtils.applyDialogTheme(this);
            setLayout(new BorderLayout(0, 14));
            setPreferredSize(new Dimension(700, 360));

            add(DialogUtils.createHeader(
                    ViewIcons.build(FontAwesome.EXCHANGE, 24, Color.WHITE),
                    "Transferir Stock",
                    "Mueva existencias fisicas a otro almacen de destino.",
                    new Color(13, 71, 161)
            ), BorderLayout.NORTH);

            txtOrigin = DialogUtils.styleInput(new JTextField(
                    sourceInventory.getWarehouseName() + " | " + sourceInventory.getProductSku() + " - " + sourceInventory.getProductName()
            ));
            txtOrigin.setEditable(false);
            txtQuantity = DialogUtils.styleInput(new JTextField("0"));
            cmbTargetWarehouse = DialogUtils.styleInput(new JComboBox<>());

            try {
                loadReferenceModel(cmbTargetWarehouse, () -> controller.listarAlmacenes());
            } catch (Exception e) {
                AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
            }

            JPanel formCard = DialogUtils.createCard("Datos de la transferencia");
            JPanel fieldsPanel = new JPanel(new GridBagLayout());
            fieldsPanel.setOpaque(false);
            addField(fieldsPanel, 0, "Origen", txtOrigin);
            addField(fieldsPanel, 1, "Almacen destino", cmbTargetWarehouse);
            addField(fieldsPanel, 2, "Cantidad a mover", txtQuantity);
            addField(fieldsPanel, 3, "Disponible", DialogUtils.createValueLabel(String.valueOf(sourceInventory.getStock())));
            formCard.add(fieldsPanel, BorderLayout.CENTER);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            footer.setOpaque(false);

            JButton btnCancel = ViewStyle.createToolbarButton("Cerrar", null, new Color(96, 125, 139));
            JButton btnSave = ViewStyle.createToolbarButton(
                    "Confirmar",
                    ViewIcons.build(FontAwesome.EXCHANGE, 14, Color.WHITE),
                    new Color(2, 136, 209)
            );

            btnCancel.addActionListener(e -> dispose());
            btnSave.addActionListener(e -> saveTransfer());

            footer.add(btnCancel);
            footer.add(btnSave);

            JPanel content = new JPanel(new BorderLayout(0, 14));
            content.setOpaque(false);
            content.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
            content.add(formCard, BorderLayout.CENTER);
            content.add(footer, BorderLayout.SOUTH);

            add(content, BorderLayout.CENTER);
            pack();
            DialogUtils.centerAndLock(this, InventarioJPanel.this);
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

        private void saveTransfer() {
            try {
                ReferenceItem targetWarehouse = getSelectedReference(cmbTargetWarehouse, "Seleccione un almacen destino.");
                int quantity = Integer.parseInt(txtQuantity.getText().trim());

                if (quantity > sourceInventory.getStock()) {
                    throw new Exception("No puede transferir mas del stock fisico disponible.");
                }

                controller.transferirExistencias(sourceInventory.getIdInventory(), targetWarehouse.getId(), quantity);
                saved = true;
                AppMessageDialog.showInfo(this, VIEW_TITLE, "Transferencia realizada correctamente.");
                dispose();
            } catch (NumberFormatException ex) {
                AppMessageDialog.showError(this, VIEW_TITLE, "La cantidad debe ser numerica.");
            } catch (Exception ex) {
                AppMessageDialog.showError(this, VIEW_TITLE, ex.getMessage());
            }
        }
    }
}
