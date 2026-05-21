package Views;

import Controllers.ShipmentTrackingController;
import Models.ReferenceItem;
import Models.ShipmentTracking;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;

public class SeguimientoEnviosJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "seguimientoEnvios";
    private static final String VIEW_TITLE = "Seguimiento de Envios";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.MAP_MARKER, 28);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ShipmentTrackingController trackingController;
    private final TrackingTableModel trackingTableModel;

    private JTextField txtSearch;
    private JComboBox<String> cmbStatusFilter;
    private JTable tblTracking;

    public SeguimientoEnviosJPanel() {
        this.trackingController = new ShipmentTrackingController();
        this.trackingTableModel = new TrackingTableModel();
        initPanel();
        loadTracking();
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
        loadTracking();
    }

    private JPanel createHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout(12, 0));
        titlePanel.setOpaque(false);

        JLabel lblIcon = new JLabel(VIEW_ICON);

        JLabel lblTitle = new JLabel("Seguimiento de Envios");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33));

        JLabel lblSubtitle = new JLabel("Registre ubicaciones, estados y comentarios de trazabilidad.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle, BorderLayout.NORTH);
        textPanel.add(lblSubtitle, BorderLayout.CENTER);

        titlePanel.add(lblIcon, BorderLayout.WEST);
        titlePanel.add(textPanel, BorderLayout.CENTER);

        JPanel toolbarCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarCard.setBackground(Color.WHITE);
        toolbarCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 225, 232)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));

        txtSearch = new JTextField(18);
        txtSearch.setPreferredSize(new Dimension(220, 36));

        cmbStatusFilter = new JComboBox<>(new String[]{
            "TODOS", "PENDING", "PREPARING", "SHIPPED", "IN_TRANSIT", "DELIVERED", "CANCELLED"
        });
        cmbStatusFilter.setPreferredSize(new Dimension(160, 34));

        JButton btnClear = createToolbarButton("Limpiar", ViewIcons.build(FontAwesome.ERASER, 14, Color.WHITE), new Color(96, 125, 139));
        btnClear.addActionListener(e -> clearFilters());

        JButton btnNew = createToolbarButton("Nuevo Seguimiento", ViewIcons.build(FontAwesome.PLUS, 14, Color.WHITE), new Color(46, 125, 50));
        btnNew.addActionListener(e -> openTrackingForm(null));

        JButton btnRefresh = createToolbarButton("Actualizar Lista", ViewIcons.build(FontAwesome.REFRESH, 14, Color.WHITE), new Color(2, 136, 209));
        btnRefresh.addActionListener(e -> loadTracking());

        toolbarCard.add(new JLabel("Buscar:"));
        toolbarCard.add(txtSearch);
        toolbarCard.add(new JLabel("Estado:"));
        toolbarCard.add(cmbStatusFilter);
        toolbarCard.add(btnClear);
        toolbarCard.add(btnNew);
        toolbarCard.add(btnRefresh);

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(toolbarCard, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createCenterPanel() {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(214, 223, 233), 1));

        tblTracking = new JTable(trackingTableModel);
        tblTracking.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTracking.setRowHeight(38);
        tblTracking.getTableHeader().setReorderingAllowed(false);
        tblTracking.getTableHeader().setBackground(new Color(246, 248, 251));
        tblTracking.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTracking.setShowHorizontalLines(true);
        tblTracking.setGridColor(new Color(232, 236, 240));

        configureTrackingTable();

        JScrollPane scrollPane = new JScrollPane(tblTracking);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        cardPanel.add(scrollPane, BorderLayout.CENTER);
        return cardPanel;
    }

    private void configureTrackingTable() {
        int actionsColumn = trackingTableModel.getActionColumnIndex();

        tblTracking.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblTracking.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblTracking.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblTracking.getColumnModel().getColumn(3).setPreferredWidth(180);
        tblTracking.getColumnModel().getColumn(4).setPreferredWidth(130);
        tblTracking.getColumnModel().getColumn(5).setPreferredWidth(220);
        tblTracking.getColumnModel().getColumn(actionsColumn).setPreferredWidth(130);
        tblTracking.getColumnModel().getColumn(actionsColumn).setMinWidth(130);
        tblTracking.getColumnModel().getColumn(actionsColumn).setMaxWidth(130);

        tblTracking.getColumnModel().getColumn(actionsColumn).setCellRenderer(new TrackingActionCellRenderer());
        tblTracking.getColumnModel().getColumn(actionsColumn).setCellEditor(new TrackingActionCellEditor());
    }

    private JButton createToolbarButton(String text, Color color) {
        return createToolbarButton(text, null, color);
    }

    private JButton createToolbarButton(String text, Icon icon, Color color) {
        JButton button = new JButton(text, icon);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        return button;
    }

    private JButton createTableIconButton(FontAwesome icon, String tooltip, Color accentColor) {
        JButton button = new JButton(ViewIcons.build(icon, 14, accentColor));
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(217, 223, 230)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        button.setBackground(new Color(248, 250, 252));
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(36, 28));
        return button;
    }

    private void loadTracking() {
        try {
            List<ShipmentTracking> trackingList = trackingController.listTracking();
            trackingTableModel.setItems(trackingList);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Seguimiento de Envios", e.getMessage());
        }
    }

    private void clearFilters() {
        txtSearch.setText("");
        cmbStatusFilter.setSelectedItem("TODOS");
        loadTracking();
    }

    private void openTrackingForm(ShipmentTracking tracking) {
        TrackingFormDialog dialog = new TrackingFormDialog(resolveWindow(), tracking);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadTracking();
        }
    }

    private void editTracking(ShipmentTracking tracking) {
        if (tracking == null || tracking.getIdTracking() <= 0) {
            AppMessageDialog.showError(this, "Seguimiento de Envios", "Seleccione un seguimiento valido para editar.");
            return;
        }

        openTrackingForm(tracking);
    }

    private void deleteTracking(ShipmentTracking tracking) {
        if (tracking == null || tracking.getIdTracking() <= 0) {
            AppMessageDialog.showError(this, "Seguimiento de Envios", "Seleccione un seguimiento valido para eliminar.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Desea eliminar el seguimiento seleccionado?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            trackingController.deleteTracking(tracking.getIdTracking());
            AppMessageDialog.showInfo(this, "Seguimiento de Envios", "El seguimiento fue eliminado correctamente.");
            loadTracking();
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Seguimiento de Envios", e.getMessage());
        }
    }

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return "-";
        }

        return value.format(DATE_TIME_FORMATTER);
    }

    private Window resolveWindow() {
        return javax.swing.SwingUtilities.getWindowAncestor(this);
    }

    private class TrackingTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 6;

        private final String[] columns = {
            "ID", "Envio", "Fecha", "Ubicacion", "Estado", "Comentarios", "Acciones"
        };

        private final List<ShipmentTracking> items = new ArrayList<>();

        public void setItems(List<ShipmentTracking> newItems) {
            items.clear();

            if (newItems != null) {
                items.addAll(newItems);
            }

            fireTableDataChanged();
        }

        public ShipmentTracking getTrackingAt(int rowIndex) {
            return items.get(rowIndex);
        }

        public int getActionColumnIndex() {
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
            ShipmentTracking tracking = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return tracking.getIdTracking();
                case 1:
                    return tracking.getIdShipment();
                case 2:
                    return formatDateTime(tracking.getTrackingDate());
                case 3:
                    return tracking.getLocation();
                case 4:
                    return tracking.getStatus();
                case 5:
                    return tracking.getComments();
                case ACTIONS_COLUMN_INDEX:
                    return tracking;
                default:
                    return "";
            }
        }
    }

    private class TrackingActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnEdit;
        private final JButton btnDelete;

        TrackingActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);

            btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar seguimiento", new Color(245, 124, 0));
            btnDelete = createTableIconButton(FontAwesome.TRASH, "Eliminar seguimiento", new Color(198, 40, 40));

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
            btnEdit.setBackground(background);
            btnDelete.setBackground(background);
            return this;
        }
    }

    private class TrackingActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private int editingRow = -1;

        TrackingActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
            panel.setOpaque(true);

            JButton btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar seguimiento", new Color(245, 124, 0));
            JButton btnDelete = createTableIconButton(FontAwesome.TRASH, "Eliminar seguimiento", new Color(198, 40, 40));

            btnEdit.addActionListener(e -> handleAction("edit"));
            btnDelete.addActionListener(e -> handleAction("delete"));

            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public Object getCellEditorValue() {
            return null;
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

        private void handleAction(String action) {
            int modelRow = tblTracking.convertRowIndexToModel(editingRow);
            ShipmentTracking tracking = trackingTableModel.getTrackingAt(modelRow);
            fireEditingStopped();

            if ("edit".equals(action)) {
                editTracking(tracking);
                return;
            }

            deleteTracking(tracking);
        }
    }

    private class TrackingFormDialog extends JDialog {

        private final ShipmentTracking editingTracking;
        private boolean saved;

        private JComboBox<ReferenceItem> cmbShipment;
        private JComboBox<ReferenceItem> cmbUser;
        private JTextField txtLocation;
        private JComboBox<String> cmbStatus;
        private JTextArea txtComments;

        TrackingFormDialog(Window owner, ShipmentTracking tracking) {
            super(owner instanceof Frame ? (Frame) owner : null, true);
            this.editingTracking = tracking;
            buildDialog();
            fillFormIfNeeded();
        }

        boolean isSaved() {
            return saved;
        }

        private void buildDialog() {
            setTitle(editingTracking == null ? "Nuevo Seguimiento" : "Editar Seguimiento");
            getContentPane().setBackground(new Color(245, 247, 250));
            setLayout(new BorderLayout(0, 0));

            JPanel headerPanel = createDialogHeader();

            cmbShipment = createReferenceCombo();
            cmbUser = createReferenceCombo();
            txtLocation = createInput();
            loadInitialReferences();
            cmbStatus = new JComboBox<>(new String[]{
                "PENDING", "PREPARING", "SHIPPED", "IN_TRANSIT", "DELIVERED", "CANCELLED"
            });
            cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            cmbStatus.setPreferredSize(new Dimension(220, 34));

            txtComments = new JTextArea(5, 20);
            txtComments.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtComments.setLineWrap(true);
            txtComments.setWrapStyleWord(true);

            JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
            bodyPanel.setOpaque(false);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JPanel trackingCard = new JPanel(new BorderLayout());
            trackingCard.setBackground(Color.WHITE);
            trackingCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(214, 223, 233)),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            addField(formPanel, gbc, 0, "Envio", cmbShipment);
            addField(formPanel, gbc, 1, "Usuario", cmbUser);
            addField(formPanel, gbc, 2, "Ubicacion", txtLocation);
            addField(formPanel, gbc, 3, "Estado", cmbStatus);
            addField(formPanel, gbc, 4, "Comentarios", new JScrollPane(txtComments));

            trackingCard.add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setOpaque(false);

            JButton btnSave = createToolbarButton("Guardar", new Color(46, 125, 50));
            btnSave.addActionListener(e -> saveTracking());

            JButton btnCancel = createToolbarButton("Cerrar", new Color(96, 125, 139));
            btnCancel.addActionListener(e -> dispose());

            buttonPanel.add(btnSave);
            buttonPanel.add(btnCancel);

            bodyPanel.add(trackingCard, BorderLayout.CENTER);
            bodyPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(headerPanel, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);

            pack();
            setMinimumSize(new Dimension(620, 520));
            setLocationRelativeTo(SeguimientoEnviosJPanel.this);
        }

        private JPanel createDialogHeader() {
            JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
            headerPanel.setBackground(new Color(13, 71, 161));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JLabel lblHeaderIcon = new JLabel(ViewIcons.build(FontAwesome.MAP_MARKER, 24, Color.WHITE));

            JLabel lblHeaderTitle = new JLabel(editingTracking == null ? "Nuevo Seguimiento" : "Editar Seguimiento");
            lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeaderTitle.setForeground(Color.WHITE);

            JLabel lblHeaderSubtitle = new JLabel("Registre estado, ubicacion y comentarios del envio.");
            lblHeaderSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblHeaderSubtitle.setForeground(new Color(220, 235, 250));

            JPanel headerText = new JPanel(new BorderLayout(0, 4));
            headerText.setOpaque(false);
            headerText.add(lblHeaderTitle, BorderLayout.NORTH);
            headerText.add(lblHeaderSubtitle, BorderLayout.CENTER);

            headerPanel.add(lblHeaderIcon, BorderLayout.WEST);
            headerPanel.add(headerText, BorderLayout.CENTER);

            return headerPanel;
        }

        private JTextField createInput() {
            JTextField textField = new JTextField(20);
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textField.setPreferredSize(new Dimension(220, 34));
            return textField;
        }

        private JComboBox<ReferenceItem> createReferenceCombo() {
            JComboBox<ReferenceItem> comboBox = new JComboBox<>();
            comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            comboBox.setPreferredSize(new Dimension(220, 34));
            return comboBox;
        }

        private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;

            JLabel lblField = new JLabel(label);
            lblField.setFont(new Font("Segoe UI", Font.BOLD, 13));
            panel.add(lblField, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            panel.add(field, gbc);
        }

        private void fillFormIfNeeded() {
            if (editingTracking == null) {
                cmbStatus.setSelectedItem("PENDING");
                return;
            }

            selectReferenceItem(cmbShipment, editingTracking.getIdShipment());
            selectReferenceItem(cmbUser, editingTracking.getIdUser());
            txtLocation.setText(editingTracking.getLocation());
            cmbStatus.setSelectedItem(editingTracking.getStatus());
            txtComments.setText(editingTracking.getComments());
        }

        private void saveTracking() {
            try {
                ShipmentTracking tracking = editingTracking == null ? new ShipmentTracking() : editingTracking;

                tracking.setIdShipment(getSelectedReferenceId(cmbShipment, "Seleccione un envio valido."));
                tracking.setIdUser(getSelectedNullableReferenceId(cmbUser));
                tracking.setLocation(txtLocation.getText().trim());
                tracking.setStatus(cmbStatus.getSelectedItem().toString());
                tracking.setComments(txtComments.getText().trim());

                if (editingTracking == null) {
                    trackingController.createTracking(tracking);
                } else {
                    trackingController.updateTracking(tracking);
                }

                saved = true;
                AppMessageDialog.showInfo(this, "Seguimiento de Envios", "Los datos del seguimiento fueron guardados correctamente.");
                dispose();
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Seguimiento de Envios", e.getMessage());
            }
        }

        private void loadInitialReferences() {
            try {
                loadReferenceModel(cmbShipment, trackingController.listShipmentOptions(), false);
                loadReferenceModel(cmbUser, trackingController.listUserOptions(), true);
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Seguimiento de Envios", e.getMessage());
            }
        }

        private void loadReferenceModel(JComboBox<ReferenceItem> comboBox, List<ReferenceItem> items, boolean includeEmpty) {
            DefaultComboBoxModel<ReferenceItem> model = new DefaultComboBoxModel<>();

            if (includeEmpty) {
                model.addElement(new ReferenceItem(0, "Sin usuario"));
            }

            if (items != null) {
                for (ReferenceItem item : items) {
                    model.addElement(item);
                }
            }

            comboBox.setModel(model);
        }

        private void selectReferenceItem(JComboBox<ReferenceItem> comboBox, Integer id) {
            int resolvedId = id == null ? 0 : id;

            for (int index = 0; index < comboBox.getItemCount(); index++) {
                ReferenceItem item = comboBox.getItemAt(index);

                if (item.getId() == resolvedId) {
                    comboBox.setSelectedIndex(index);
                    return;
                }
            }
        }

        private int getSelectedReferenceId(JComboBox<ReferenceItem> comboBox, String message) throws Exception {
            ReferenceItem item = (ReferenceItem) comboBox.getSelectedItem();

            if (item == null || item.getId() <= 0) {
                throw new Exception(message);
            }

            return item.getId();
        }

        private Integer getSelectedNullableReferenceId(JComboBox<ReferenceItem> comboBox) {
            ReferenceItem item = (ReferenceItem) comboBox.getSelectedItem();
            return item == null || item.getId() <= 0 ? null : item.getId();
        }
    }
}
