package Views;

import Controllers.AlmacenesController;
import Models.Inventory;
import Models.Warehouses;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
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

public class AlmacenesJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "almacenes";
    private static final String VIEW_TITLE = "Almacenes";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.BUILDING_O, 28);

    private final AlmacenesController controller = new AlmacenesController();
    private final WarehouseTableModel warehouseTableModel = new WarehouseTableModel();
    private final List<Warehouses> currentWarehouses = new ArrayList<>();

    private JTextField txtSearch;
    private JTable table;
    private JLabel lblMode;
    private JButton btnToggleView;
    private boolean showingInactive;

    public AlmacenesJPanel() {
        initPanel();
        loadWarehouses();
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

    @Override
    public void onViewShown() {
        refreshData();
    }

    private JPanel createHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        wrapper.add(
                ViewStyle.createTitlePanel(
                        VIEW_ICON,
                        "Gestion de Almacenes",
                        "Consulte sedes, cantidad de productos y stock total sin salir del modulo."
                ),
                BorderLayout.NORTH
        );

        JPanel toolbarCard = ViewStyle.createToolbarCard();

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(240, 36));
        installSearchDebounce();

        lblMode = new JLabel("Vista: Activos");
        lblMode.setForeground(new Color(69, 90, 100));

        JButton btnClear = ViewStyle.createToolbarButton(
                "Limpiar",
                ViewIcons.build(FontAwesome.ERASER, 14, Color.WHITE),
                new Color(96, 125, 139)
        );
        btnClear.addActionListener(e -> clearFilters());

        JButton btnNew = ViewStyle.createToolbarButton(
                "Nuevo Almacen",
                ViewIcons.build(FontAwesome.PLUS, 14, Color.WHITE),
                new Color(46, 125, 50)
        );
        btnNew.addActionListener(e -> openWarehouseForm(null));

        btnToggleView = ViewStyle.createToolbarButton(
                "Ver Inactivos",
                ViewIcons.build(FontAwesome.ARCHIVE, 14, Color.WHITE),
                new Color(121, 85, 72)
        );
        btnToggleView.addActionListener(e -> toggleView());

        toolbarCard.add(new JLabel("Buscar:"));
        toolbarCard.add(txtSearch);
        toolbarCard.add(btnClear);
        toolbarCard.add(btnNew);
        toolbarCard.add(btnToggleView);
        toolbarCard.add(lblMode);

        wrapper.add(toolbarCard, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createCenterPanel() {
        table = new JTable(warehouseTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configureTable();
        return ViewStyle.createTableCard(table);
    }

    private void configureTable() {
        int actionsColumn = warehouseTableModel.getActionColumnIndex();
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(actionsColumn).setPreferredWidth(130);
        table.getColumnModel().getColumn(actionsColumn).setMinWidth(130);
        table.getColumnModel().getColumn(actionsColumn).setMaxWidth(130);
        table.getColumnModel().getColumn(actionsColumn).setCellRenderer(new WarehouseActionCellRenderer());
        table.getColumnModel().getColumn(actionsColumn).setCellEditor(new WarehouseActionCellEditor());
    }

    private void installSearchDebounce() {
        Timer timer = new Timer(280, e -> searchWarehouses());
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
            searchWarehouses();
        });
    }

    private void loadWarehouses() {
        try {
            currentWarehouses.clear();
            currentWarehouses.addAll(showingInactive
                    ? controller.listarInactivosConResumen()
                    : controller.listarActivosConResumen());
            warehouseTableModel.setItems(currentWarehouses);
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void searchWarehouses() {
        try {
            currentWarehouses.clear();
            currentWarehouses.addAll(controller.buscarAlmacenesConResumen(txtSearch.getText(), showingInactive));
            warehouseTableModel.setItems(currentWarehouses);
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void clearFilters() {
        txtSearch.setText("");
        loadWarehouses();
    }

    private void refreshData() {
        if (txtSearch == null || txtSearch.getText().trim().isEmpty()) {
            loadWarehouses();
        } else {
            searchWarehouses();
        }
    }

    private void toggleView() {
        showingInactive = !showingInactive;
        lblMode.setText(showingInactive ? "Vista: Inactivos" : "Vista: Activos");
        btnToggleView.setText(showingInactive ? "Ver Activos" : "Ver Inactivos");
        txtSearch.setText("");
        loadWarehouses();
    }

    private void openWarehouseForm(Warehouses warehouse) {
        WarehouseFormDialog dialog = new WarehouseFormDialog(resolveWindow(), warehouse);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadWarehouses();
        }
    }

    private void openWarehouseDetail(Warehouses warehouse) {
        try {
            List<Inventory> inventoryItems = controller.listarInventarioDeAlmacen(warehouse.getIdWarehouse());
            WarehouseDetailDialog dialog = new WarehouseDetailDialog(resolveWindow(), warehouse, inventoryItems);
            dialog.setVisible(true);
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void restoreWarehouse(Warehouses warehouse) {
        try {
            controller.restaurarAlmacen(warehouse.getIdWarehouse());
            AppMessageDialog.showInfo(this, VIEW_TITLE, "Almacen restaurado correctamente.");
            loadWarehouses();
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void deleteWarehouse(Warehouses warehouse) {
        int option = JOptionPane.showConfirmDialog(
                this,
                showingInactive
                        ? "Eliminar permanentemente el almacen seleccionado?"
                        : "Enviar el almacen al listado de inactivos?",
                showingInactive ? "Borrado fisico" : "Desactivacion",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (showingInactive) {
                controller.eliminarDefinitivo(warehouse.getIdWarehouse());
            } else {
                controller.eliminarLogico(warehouse.getIdWarehouse());
            }

            AppMessageDialog.showInfo(this, VIEW_TITLE, showingInactive
                    ? "Almacen eliminado permanentemente."
                    : "Almacen enviado a inactivos.");
            loadWarehouses();
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private Window resolveWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private class WarehouseTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 7;
        private final String[] columns = {
            "Nombre", "Ciudad", "Productos", "Stock", "Reservado", "Disponible", "Telefono", "Acciones"
        };
        private final List<Warehouses> items = new ArrayList<>();

        void setItems(List<Warehouses> warehouses) {
            items.clear();

            if (warehouses != null) {
                items.addAll(warehouses);
            }

            fireTableDataChanged();
        }

        Warehouses getWarehouseAt(int rowIndex) {
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
            Warehouses warehouse = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return warehouse.getWarehouseName();
                case 1:
                    return warehouse.getCity();
                case 2:
                    return warehouse.getProductCount();
                case 3:
                    return warehouse.getTotalStock();
                case 4:
                    return warehouse.getTotalReservedStock();
                case 5:
                    return warehouse.getAvailableStock();
                case 6:
                    return warehouse.getPhone();
                case ACTIONS_COLUMN_INDEX:
                    return warehouse;
                default:
                    return "";
            }
        }
    }

    private class WarehouseActionCellRenderer extends JPanel implements TableCellRenderer {

        WarehouseActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            Color background = isSelected ? table.getSelectionBackground() : Color.WHITE;
            setBackground(background);

            JButton btnDetail = ViewStyle.createTableIconButton(FontAwesome.EYE, "Ver inventario", new Color(69, 90, 100));
            JButton btnSecondary = ViewStyle.createTableIconButton(
                    showingInactive ? FontAwesome.UNDO : FontAwesome.PENCIL,
                    showingInactive ? "Restaurar" : "Editar",
                    showingInactive ? new Color(46, 125, 50) : new Color(245, 124, 0)
            );
            JButton btnDanger = ViewStyle.createTableIconButton(
                    FontAwesome.TRASH,
                    showingInactive ? "Eliminar fisico" : "Inactivar",
                    new Color(198, 40, 40)
            );

            btnDetail.setBackground(background);
            btnSecondary.setBackground(background);
            btnDanger.setBackground(background);

            add(btnDetail);
            add(btnSecondary);
            add(btnDanger);
            return this;
        }
    }

    private class WarehouseActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private int editingRow = -1;

        WarehouseActionCellEditor() {
            panel.setOpaque(true);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editingRow = row;
            panel.removeAll();
            panel.setBackground(table.getSelectionBackground());

            JButton btnDetail = ViewStyle.createTableIconButton(FontAwesome.EYE, "Ver inventario", new Color(69, 90, 100));
            JButton btnSecondary = ViewStyle.createTableIconButton(
                    showingInactive ? FontAwesome.UNDO : FontAwesome.PENCIL,
                    showingInactive ? "Restaurar" : "Editar",
                    showingInactive ? new Color(46, 125, 50) : new Color(245, 124, 0)
            );
            JButton btnDanger = ViewStyle.createTableIconButton(
                    FontAwesome.TRASH,
                    showingInactive ? "Eliminar fisico" : "Inactivar",
                    new Color(198, 40, 40)
            );

            btnDetail.addActionListener(e -> handleAction("detail"));
            btnSecondary.addActionListener(e -> handleAction("secondary"));
            btnDanger.addActionListener(e -> handleAction("danger"));

            panel.add(btnDetail);
            panel.add(btnSecondary);
            panel.add(btnDanger);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        private void handleAction(String action) {
            int modelRow = table.convertRowIndexToModel(editingRow);
            Warehouses warehouse = warehouseTableModel.getWarehouseAt(modelRow);
            fireEditingStopped();

            if ("detail".equals(action)) {
                openWarehouseDetail(warehouse);
            } else if ("secondary".equals(action)) {
                if (showingInactive) {
                    restoreWarehouse(warehouse);
                } else {
                    openWarehouseForm(warehouse);
                }
            } else {
                deleteWarehouse(warehouse);
            }
        }
    }

    private class WarehouseFormDialog extends JDialog {

        private final Warehouses editingWarehouse;
        private boolean saved;

        private JTextField txtName;
        private JTextField txtAddress;
        private JTextField txtCity;
        private JTextField txtCountry;
        private JTextField txtPhone;

        WarehouseFormDialog(Window owner, Warehouses warehouse) {
            super(owner instanceof Frame ? (Frame) owner : null, true);
            this.editingWarehouse = warehouse;
            buildDialog();
        }

        boolean isSaved() {
            return saved;
        }

        private void buildDialog() {
            setTitle(editingWarehouse == null ? "Nuevo Almacen" : "Editar Almacen");
            DialogUtils.applyDialogTheme(this);
            setLayout(new BorderLayout(0, 14));
            setPreferredSize(new Dimension(700, 460));

            add(DialogUtils.createHeader(
                    ViewIcons.build(editingWarehouse == null ? FontAwesome.PLUS : FontAwesome.PENCIL, 24, Color.WHITE),
                    editingWarehouse == null ? "Nuevo Almacen" : "Editar Almacen",
                    "Complete los datos de ubicacion y contacto del almacen.",
                    new Color(27, 94, 157)
            ), BorderLayout.NORTH);

            txtName = DialogUtils.styleInput(new JTextField(editingWarehouse == null ? "" : editingWarehouse.getWarehouseName()));
            txtAddress = DialogUtils.styleInput(new JTextField(editingWarehouse == null ? "" : editingWarehouse.getAddress()));
            txtCity = DialogUtils.styleInput(new JTextField(editingWarehouse == null ? "" : editingWarehouse.getCity()));
            txtCountry = DialogUtils.styleInput(new JTextField(editingWarehouse == null ? "" : editingWarehouse.getCountry()));
            txtPhone = DialogUtils.styleInput(new JTextField(editingWarehouse == null ? "" : editingWarehouse.getPhone()));

            JPanel formCard = DialogUtils.createCard("Datos del almacen");
            JPanel fieldsPanel = new JPanel(new GridBagLayout());
            fieldsPanel.setOpaque(false);

            addField(fieldsPanel, 0, "Nombre", txtName);
            addField(fieldsPanel, 1, "Direccion", txtAddress);
            addField(fieldsPanel, 2, "Ciudad", txtCity);
            addField(fieldsPanel, 3, "Pais", txtCountry);
            addField(fieldsPanel, 4, "Telefono", txtPhone);

            formCard.add(fieldsPanel, BorderLayout.CENTER);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            footer.setOpaque(false);

            JButton btnCancel = ViewStyle.createToolbarButton("Cerrar", null, new Color(96, 125, 139));
            JButton btnSave = ViewStyle.createToolbarButton(
                    "Guardar",
                    ViewIcons.build(FontAwesome.FLOPPY_O, 14, Color.WHITE),
                    new Color(46, 125, 50)
            );

            btnCancel.addActionListener(e -> dispose());
            btnSave.addActionListener(e -> saveWarehouse());

            footer.add(btnCancel);
            footer.add(btnSave);

            JPanel content = new JPanel(new BorderLayout(0, 14));
            content.setOpaque(false);
            content.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
            content.add(formCard, BorderLayout.CENTER);
            content.add(footer, BorderLayout.SOUTH);

            add(content, BorderLayout.CENTER);
            pack();
            DialogUtils.centerAndLock(this, AlmacenesJPanel.this);
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

        private void saveWarehouse() {
            try {
                if (editingWarehouse == null) {
                    controller.registrarAlmacen(
                            txtName.getText(),
                            txtAddress.getText(),
                            txtCity.getText(),
                            txtCountry.getText(),
                            txtPhone.getText()
                    );
                } else {
                    controller.modificarAlmacen(
                            editingWarehouse.getIdWarehouse(),
                            txtName.getText(),
                            txtAddress.getText(),
                            txtCity.getText(),
                            txtCountry.getText(),
                            txtPhone.getText()
                    );
                }

                saved = true;
                AppMessageDialog.showInfo(this, VIEW_TITLE, editingWarehouse == null
                        ? "Almacen creado correctamente."
                        : "Almacen actualizado correctamente.");
                dispose();
            } catch (Exception ex) {
                AppMessageDialog.showError(this, VIEW_TITLE, ex.getMessage());
            }
        }
    }

    private class WarehouseDetailDialog extends JDialog {

        WarehouseDetailDialog(Window owner, Warehouses warehouse, List<Inventory> inventoryItems) {
            super(owner instanceof Frame ? (Frame) owner : null, "Detalle del Almacen", true);
            buildDialog(warehouse, inventoryItems);
        }

        private void buildDialog(Warehouses warehouse, List<Inventory> inventoryItems) {
            DialogUtils.applyDialogTheme(this);
            setLayout(new BorderLayout(0, 0));
            setPreferredSize(new Dimension(1080, 760));

            JPanel headerPanel = DialogUtils.createHeader(
                    ViewIcons.build(FontAwesome.BUILDING_O, 24, Color.WHITE),
                    "Detalle del Almacen",
                    "Revise productos asignados, stock total y disponibilidad.",
                    new Color(13, 71, 161)
            );

            JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
            bodyPanel.setOpaque(false);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 12, 12));
            summaryPanel.setOpaque(false);
            summaryPanel.add(DialogUtils.createInfoTile("Almacen", warehouse.getWarehouseName()));
            summaryPanel.add(DialogUtils.createInfoTile("Productos", String.valueOf(warehouse.getProductCount())));
            summaryPanel.add(DialogUtils.createInfoTile("Stock total", String.valueOf(warehouse.getTotalStock())));
            summaryPanel.add(DialogUtils.createInfoTile("Disponible", String.valueOf(warehouse.getAvailableStock())));

            JPanel infoCard = DialogUtils.createCard("Datos Generales");
            JPanel infoGrid = new JPanel(new GridLayout(1, 3, 12, 12));
            infoGrid.setOpaque(false);
            infoGrid.add(DialogUtils.createInfoTile("Ciudad", warehouse.getCity()));
            infoGrid.add(DialogUtils.createInfoTile("Pais", warehouse.getCountry()));
            infoGrid.add(DialogUtils.createInfoTile("Telefono", warehouse.getPhone()));
            infoCard.add(infoGrid, BorderLayout.CENTER);

            JTable inventoryTable = new JTable(new WarehouseInventoryTableModel(inventoryItems));
            ViewStyle.styleTable(inventoryTable);
            inventoryTable.setRowHeight(32);
            inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(140);
            inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(260);
            inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(100);

            JPanel inventoryCard = DialogUtils.createCard("Inventario del Almacen");
            inventoryCard.add(DialogUtils.createScrollPane(inventoryTable), BorderLayout.CENTER);

            JButton btnClose = ViewStyle.createToolbarButton("Cerrar", null, new Color(96, 125, 139));
            btnClose.addActionListener(e -> dispose());

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            footer.setOpaque(false);
            footer.add(btnClose);

            bodyPanel.add(summaryPanel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
            contentPanel.setOpaque(false);
            contentPanel.add(infoCard, BorderLayout.NORTH);
            contentPanel.add(inventoryCard, BorderLayout.CENTER);

            bodyPanel.add(contentPanel, BorderLayout.CENTER);

            add(headerPanel, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);
            add(footer, BorderLayout.SOUTH);

            pack();
            setMinimumSize(new Dimension(1000, 720));
            DialogUtils.centerAndLock(this, AlmacenesJPanel.this);
        }
    }

    private static class WarehouseInventoryTableModel extends AbstractTableModel {

        private final String[] columns = {"SKU", "Producto", "Stock", "Reservado", "Minimo"};
        private final List<Inventory> items;

        WarehouseInventoryTableModel(List<Inventory> inventoryItems) {
            this.items = inventoryItems == null ? new ArrayList<>() : new ArrayList<>(inventoryItems);
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
        public Object getValueAt(int rowIndex, int columnIndex) {
            Inventory inventory = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return inventory.getProductSku();
                case 1:
                    return inventory.getProductName();
                case 2:
                    return inventory.getStock();
                case 3:
                    return inventory.getReservedStock();
                case 4:
                    return inventory.getMinStock();
                default:
                    return "";
            }
        }
    }
}
