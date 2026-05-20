package Views;

import Controllers.ShipmentDetailController;
import Models.ReferenceItem;
import Models.ShipmentDetail;
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
import java.math.BigDecimal;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;

public class DetallesEnviosJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "detallesEnvios";
    private static final String VIEW_TITLE = "Detalles de Envios";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.FILE_TEXT_O, 28);

    private final ShipmentDetailController detailController;
    private final DetailTableModel detailTableModel;

    private JTextField txtSearch;
    private JTable tblDetails;

    public DetallesEnviosJPanel() {
        this.detailController = new ShipmentDetailController();
        this.detailTableModel = new DetailTableModel();
        initPanel();
        loadDetails();
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

        JPanel titlePanel = new JPanel(new BorderLayout(12, 0));
        titlePanel.setOpaque(false);

        JLabel lblIcon = new JLabel(VIEW_ICON);

        JLabel lblTitle = new JLabel("Detalles de Envios");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33));

        JLabel lblSubtitle = new JLabel("Gestione productos, cantidades y pesos dentro de cada caja.");
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

        JButton btnClear = createToolbarButton("Limpiar", new Color(96, 125, 139));
        btnClear.addActionListener(e -> clearFilters());

        JButton btnNew = createToolbarButton("Nuevo Detalle", new Color(46, 125, 50));
        btnNew.addActionListener(e -> openDetailForm(null));

        JButton btnRefresh = createToolbarButton("Actualizar Lista", new Color(2, 136, 209));
        btnRefresh.addActionListener(e -> loadDetails());

        toolbarCard.add(new JLabel("Buscar:"));
        toolbarCard.add(txtSearch);
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

        tblDetails = new JTable(detailTableModel);
        tblDetails.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDetails.setRowHeight(38);
        tblDetails.getTableHeader().setReorderingAllowed(false);
        tblDetails.getTableHeader().setBackground(new Color(246, 248, 251));
        tblDetails.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblDetails.setShowHorizontalLines(true);
        tblDetails.setGridColor(new Color(232, 236, 240));

        configureDetailTable();

        JScrollPane scrollPane = new JScrollPane(tblDetails);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        cardPanel.add(scrollPane, BorderLayout.CENTER);
        return cardPanel;
    }

    private void configureDetailTable() {
        int actionsColumn = detailTableModel.getActionColumnIndex();

        tblDetails.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblDetails.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblDetails.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblDetails.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblDetails.getColumnModel().getColumn(actionsColumn).setPreferredWidth(130);
        tblDetails.getColumnModel().getColumn(actionsColumn).setMinWidth(130);
        tblDetails.getColumnModel().getColumn(actionsColumn).setMaxWidth(130);

        tblDetails.getColumnModel().getColumn(actionsColumn).setCellRenderer(new DetailActionCellRenderer());
        tblDetails.getColumnModel().getColumn(actionsColumn).setCellEditor(new DetailActionCellEditor());
    }

    private JButton createToolbarButton(String text, Color color) {
        JButton button = new JButton(text);
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

    private void loadDetails() {
        try {
            List<ShipmentDetail> details = detailController.listDetails();
            detailTableModel.setItems(details);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Detalles de Envios", e.getMessage());
        }
    }

    private void clearFilters() {
        txtSearch.setText("");
        loadDetails();
    }

    private void openDetailForm(ShipmentDetail detail) {
        DetailFormDialog dialog = new DetailFormDialog(resolveWindow(), detail);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadDetails();
        }
    }

    private void editDetail(ShipmentDetail detail) {
        if (detail == null || detail.getIdShipmentDetail() <= 0) {
            AppMessageDialog.showError(this, "Detalles de Envios", "Seleccione un detalle valido para editar.");
            return;
        }

        openDetailForm(detail);
    }

    private void deleteDetail(ShipmentDetail detail) {
        if (detail == null || detail.getIdShipmentDetail() <= 0) {
            AppMessageDialog.showError(this, "Detalles de Envios", "Seleccione un detalle valido para eliminar.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Desea eliminar el detalle seleccionado?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            detailController.deleteDetail(detail.getIdShipmentDetail());
            AppMessageDialog.showInfo(this, "Detalles de Envios", "El detalle fue eliminado correctamente.");
            loadDetails();
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Detalles de Envios", e.getMessage());
        }
    }

    private Window resolveWindow() {
        return javax.swing.SwingUtilities.getWindowAncestor(this);
    }

    private static class DetailTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 6;

        private final String[] columns = {
            "ID", "Envio", "Caja", "Producto", "Cantidad", "Peso Unit.", "Acciones"
        };

        private final List<ShipmentDetail> items = new ArrayList<>();

        public void setItems(List<ShipmentDetail> newItems) {
            items.clear();

            if (newItems != null) {
                items.addAll(newItems);
            }

            fireTableDataChanged();
        }

        public ShipmentDetail getDetailAt(int rowIndex) {
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
            ShipmentDetail detail = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return detail.getIdShipmentDetail();
                case 1:
                    return detail.getIdShipment();
                case 2:
                    return detail.getIdBox();
                case 3:
                    return detail.getIdProduct();
                case 4:
                    return detail.getQuantity();
                case 5:
                    return detail.getUnitWeightKg();
                case ACTIONS_COLUMN_INDEX:
                    return detail;
                default:
                    return "";
            }
        }
    }

    private class DetailActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnEdit;
        private final JButton btnDelete;

        DetailActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);

            btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar detalle", new Color(245, 124, 0));
            btnDelete = createTableIconButton(FontAwesome.TRASH, "Eliminar detalle", new Color(198, 40, 40));

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

    private class DetailActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private int editingRow = -1;

        DetailActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
            panel.setOpaque(true);

            JButton btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar detalle", new Color(245, 124, 0));
            JButton btnDelete = createTableIconButton(FontAwesome.TRASH, "Eliminar detalle", new Color(198, 40, 40));

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
            int modelRow = tblDetails.convertRowIndexToModel(editingRow);
            ShipmentDetail detail = detailTableModel.getDetailAt(modelRow);
            fireEditingStopped();

            if ("edit".equals(action)) {
                editDetail(detail);
                return;
            }

            deleteDetail(detail);
        }
    }

    private class DetailFormDialog extends JDialog {

        private final ShipmentDetail editingDetail;
        private boolean saved;

        private JComboBox<ReferenceItem> cmbShipment;
        private JComboBox<ReferenceItem> cmbBox;
        private JComboBox<ReferenceItem> cmbProduct;
        private JTextField txtQuantity;
        private JTextField txtUnitWeightKg;

        DetailFormDialog(Window owner, ShipmentDetail detail) {
            super(owner instanceof Frame ? (Frame) owner : null, true);
            this.editingDetail = detail;
            buildDialog();
            fillFormIfNeeded();
        }

        boolean isSaved() {
            return saved;
        }

        private void buildDialog() {
            setTitle(editingDetail == null ? "Nuevo Detalle" : "Editar Detalle");
            getContentPane().setBackground(new Color(245, 247, 250));
            setLayout(new BorderLayout(0, 0));

            JPanel headerPanel = createDialogHeader();

            cmbShipment = createReferenceCombo();
            cmbBox = createReferenceCombo();
            cmbProduct = createReferenceCombo();
            txtQuantity = createInput();
            txtUnitWeightKg = createInput();

            loadInitialReferences();
            loadBoxesForSelectedShipment();
            cmbShipment.addActionListener(e -> loadBoxesForSelectedShipment());

            JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
            bodyPanel.setOpaque(false);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JPanel detailCard = new JPanel(new BorderLayout());
            detailCard.setBackground(Color.WHITE);
            detailCard.setBorder(BorderFactory.createCompoundBorder(
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
            addField(formPanel, gbc, 1, "Caja", cmbBox);
            addField(formPanel, gbc, 2, "Producto", cmbProduct);
            addField(formPanel, gbc, 3, "Cantidad", txtQuantity);
            addField(formPanel, gbc, 4, "Peso unitario kg", txtUnitWeightKg);

            detailCard.add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setOpaque(false);

            JButton btnSave = createToolbarButton("Guardar", new Color(46, 125, 50));
            btnSave.addActionListener(e -> saveDetail());

            JButton btnCancel = createToolbarButton("Cerrar", new Color(96, 125, 139));
            btnCancel.addActionListener(e -> dispose());

            buttonPanel.add(btnSave);
            buttonPanel.add(btnCancel);

            bodyPanel.add(detailCard, BorderLayout.CENTER);
            bodyPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(headerPanel, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);

            pack();
            setMinimumSize(new Dimension(560, 460));
            setLocationRelativeTo(DetallesEnviosJPanel.this);
        }

        private JPanel createDialogHeader() {
            JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
            headerPanel.setBackground(new Color(13, 71, 161));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JLabel lblHeaderIcon = new JLabel(ViewIcons.build(FontAwesome.FILE_TEXT_O, 24, Color.WHITE));

            JLabel lblHeaderTitle = new JLabel(editingDetail == null ? "Nuevo Detalle" : "Editar Detalle");
            lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeaderTitle.setForeground(Color.WHITE);

            JLabel lblHeaderSubtitle = new JLabel("Registre productos, cantidades y peso unitario del envio.");
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

        private void loadInitialReferences() {
            try {
                loadReferenceModel(cmbShipment, detailController.listShipmentOptions());
                loadReferenceModel(cmbProduct, detailController.listProductOptions());
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Detalles de Envios", e.getMessage());
            }
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
            if (editingDetail == null) {
                return;
            }

            selectReferenceItem(cmbShipment, editingDetail.getIdShipment());
            loadBoxesForSelectedShipment();
            selectReferenceItem(cmbBox, editingDetail.getIdBox());
            selectReferenceItem(cmbProduct, editingDetail.getIdProduct());
            txtQuantity.setText(String.valueOf(editingDetail.getQuantity()));
            txtUnitWeightKg.setText(String.valueOf(editingDetail.getUnitWeightKg()));
        }

        private void saveDetail() {
            try {
                ShipmentDetail detail = editingDetail == null ? new ShipmentDetail() : editingDetail;

                detail.setIdShipment(getSelectedReferenceId(cmbShipment, "Seleccione un envio valido."));
                detail.setIdBox(getSelectedReferenceId(cmbBox, "Seleccione una caja valida."));
                detail.setIdProduct(getSelectedReferenceId(cmbProduct, "Seleccione un producto valido."));
                detail.setQuantity(parseInt(txtQuantity.getText(), "Ingrese una cantidad valida."));
                detail.setUnitWeightKg(parseDecimal(txtUnitWeightKg.getText(), "Ingrese un peso unitario valido."));

                if (editingDetail == null) {
                    detailController.createDetail(detail);
                } else {
                    detailController.updateDetail(detail);
                }

                saved = true;
                AppMessageDialog.showInfo(this, "Detalles de Envios", "Los datos del detalle fueron guardados correctamente.");
                dispose();
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Detalles de Envios", e.getMessage());
            }
        }

        private int parseInt(String value, String message) throws Exception {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException ex) {
                throw new Exception(message);
            }
        }

        private BigDecimal parseDecimal(String value, String message) throws Exception {
            try {
                return new BigDecimal(value.trim());
            } catch (NumberFormatException ex) {
                throw new Exception(message);
            }
        }

        private void loadReferenceModel(JComboBox<ReferenceItem> comboBox, List<ReferenceItem> items) {
            DefaultComboBoxModel<ReferenceItem> model = new DefaultComboBoxModel<>();

            if (items != null) {
                for (ReferenceItem item : items) {
                    model.addElement(item);
                }
            }

            comboBox.setModel(model);
        }

        private void loadBoxesForSelectedShipment() {
            ReferenceItem shipment = (ReferenceItem) cmbShipment.getSelectedItem();

            try {
                if (shipment == null || shipment.getId() <= 0) {
                    loadReferenceModel(cmbBox, detailController.listBoxOptions());
                } else {
                    loadReferenceModel(cmbBox, detailController.listBoxOptionsByShipment(shipment.getId()));
                }
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Detalles de Envios", e.getMessage());
            }
        }

        private void selectReferenceItem(JComboBox<ReferenceItem> comboBox, int id) {
            for (int index = 0; index < comboBox.getItemCount(); index++) {
                ReferenceItem item = comboBox.getItemAt(index);

                if (item.getId() == id) {
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
    }
}
