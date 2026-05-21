package Views;

import Controllers.AlmacenesController;
import Models.Warehouses;
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

    private JPanel createHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        wrapper.add(
                ViewStyle.createTitlePanel(
                        VIEW_ICON,
                        "Gestion de Almacenes",
                        "Administre sedes logisticas activas e inactivas con acciones por fila."
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
        table.getColumnModel().getColumn(0).setPreferredWidth(220);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(actionsColumn).setPreferredWidth(100);
        table.getColumnModel().getColumn(actionsColumn).setMinWidth(100);
        table.getColumnModel().getColumn(actionsColumn).setMaxWidth(100);
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
            currentWarehouses.addAll(showingInactive ? controller.listarInactivos() : controller.listarActivos());
            warehouseTableModel.setItems(currentWarehouses);
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void searchWarehouses() {
        try {
            currentWarehouses.clear();
            currentWarehouses.addAll(controller.buscarAlmacenes(txtSearch.getText(), showingInactive));
            warehouseTableModel.setItems(currentWarehouses);
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void clearFilters() {
        txtSearch.setText("");
        loadWarehouses();
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

        private static final int ACTIONS_COLUMN_INDEX = 5;
        private final String[] columns = {"Nombre", "Direccion", "Ciudad", "Pais", "Telefono", "Acciones"};
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
                    return warehouse.getAddress();
                case 2:
                    return warehouse.getCity();
                case 3:
                    return warehouse.getCountry();
                case 4:
                    return warehouse.getPhone();
                case ACTIONS_COLUMN_INDEX:
                    return warehouse;
                default:
                    return "";
            }
        }
    }

    private class WarehouseActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnPrimary;
        private final JButton btnDanger;

        WarehouseActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);

            btnPrimary = ViewStyle.createTableIconButton(
                    showingInactive ? FontAwesome.UNDO : FontAwesome.PENCIL,
                    showingInactive ? "Restaurar" : "Editar",
                    showingInactive ? new Color(46, 125, 50) : new Color(245, 124, 0)
            );
            btnDanger = ViewStyle.createTableIconButton(FontAwesome.TRASH, "Eliminar", new Color(198, 40, 40));
            add(btnPrimary);
            add(btnDanger);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            Color background = isSelected ? table.getSelectionBackground() : Color.WHITE;
            setBackground(background);

            JButton primary = ViewStyle.createTableIconButton(
                    showingInactive ? FontAwesome.UNDO : FontAwesome.PENCIL,
                    showingInactive ? "Restaurar" : "Editar",
                    showingInactive ? new Color(46, 125, 50) : new Color(245, 124, 0)
            );
            JButton danger = ViewStyle.createTableIconButton(FontAwesome.TRASH, showingInactive ? "Eliminar fisico" : "Inactivar", new Color(198, 40, 40));
            primary.setBackground(background);
            danger.setBackground(background);
            add(primary);
            add(danger);
            return this;
        }
    }

    private class WarehouseActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private int editingRow = -1;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editingRow = row;
            panel.removeAll();
            panel.setOpaque(true);
            panel.setBackground(table.getSelectionBackground());

            JButton btnPrimary = ViewStyle.createTableIconButton(
                    showingInactive ? FontAwesome.UNDO : FontAwesome.PENCIL,
                    showingInactive ? "Restaurar" : "Editar",
                    showingInactive ? new Color(46, 125, 50) : new Color(245, 124, 0)
            );
            JButton btnDanger = ViewStyle.createTableIconButton(
                    FontAwesome.TRASH,
                    showingInactive ? "Eliminar fisico" : "Inactivar",
                    new Color(198, 40, 40)
            );

            btnPrimary.addActionListener(e -> handleAction("primary"));
            btnDanger.addActionListener(e -> handleAction("danger"));

            panel.add(btnPrimary);
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

            if ("primary".equals(action)) {
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
}
