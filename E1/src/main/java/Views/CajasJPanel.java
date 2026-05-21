package Views;

import Config.ImageStorage;
import Controllers.BoxController;
import Models.Box;
import Models.ReferenceItem;
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
import java.io.File;
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

public class CajasJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "cajas";
    private static final String VIEW_TITLE = "Cajas";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.ARCHIVE, 28);

    private final BoxController boxController;
    private final BoxTableModel boxTableModel;

    private JTextField txtSearch;
    private JComboBox<String> cmbStatusFilter;
    private JTable tblBoxes;

    public CajasJPanel() {
        this.boxController = new BoxController();
        this.boxTableModel = new BoxTableModel();
        initPanel();
        loadBoxes();
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

        JLabel lblTitle = new JLabel("Gestion de Cajas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33));

        JLabel lblSubtitle = new JLabel("Controle cajas, medidas, peso, valor declarado y estado de embalaje.");
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
            "TODOS", "PACKED", "SHIPPED", "IN_TRANSIT", "DELIVERED", "DAMAGED"
        });
        cmbStatusFilter.setPreferredSize(new Dimension(160, 34));

        JButton btnClear = createToolbarButton("Limpiar", new Color(96, 125, 139));
        btnClear.addActionListener(e -> clearFilters());

        JButton btnNew = createToolbarButton("Nueva Caja", new Color(46, 125, 50));
        btnNew.addActionListener(e -> openBoxForm(null));

        JButton btnRefresh = createToolbarButton("Actualizar Lista", new Color(2, 136, 209));
        btnRefresh.addActionListener(e -> loadBoxes());

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

        tblBoxes = new JTable(boxTableModel);
        tblBoxes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBoxes.setRowHeight(38);
        tblBoxes.getTableHeader().setReorderingAllowed(false);
        tblBoxes.getTableHeader().setBackground(new Color(246, 248, 251));
        tblBoxes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblBoxes.setShowHorizontalLines(true);
        tblBoxes.setGridColor(new Color(232, 236, 240));

        configureBoxTable();

        JScrollPane scrollPane = new JScrollPane(tblBoxes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        cardPanel.add(scrollPane, BorderLayout.CENTER);
        return cardPanel;
    }

    private void configureBoxTable() {
        int actionsColumn = boxTableModel.getActionColumnIndex();

        tblBoxes.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblBoxes.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblBoxes.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblBoxes.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblBoxes.getColumnModel().getColumn(actionsColumn).setPreferredWidth(130);
        tblBoxes.getColumnModel().getColumn(actionsColumn).setMinWidth(130);
        tblBoxes.getColumnModel().getColumn(actionsColumn).setMaxWidth(130);

        tblBoxes.getColumnModel().getColumn(actionsColumn).setCellRenderer(new BoxActionCellRenderer());
        tblBoxes.getColumnModel().getColumn(actionsColumn).setCellEditor(new BoxActionCellEditor());
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

    private void loadBoxes() {
        try {
            List<Box> boxes = boxController.listBoxes();
            boxTableModel.setItems(boxes);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Cajas", e.getMessage());
        }
    }

    private void clearFilters() {
        txtSearch.setText("");
        cmbStatusFilter.setSelectedItem("TODOS");
        loadBoxes();
    }

    private void openBoxForm(Box box) {
        BoxFormDialog dialog = new BoxFormDialog(resolveWindow(), box);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadBoxes();
        }
    }

    private void editBox(Box box) {
        if (box == null || box.getIdBox() <= 0) {
            AppMessageDialog.showError(this, "Cajas", "Seleccione una caja valida para editar.");
            return;
        }

        openBoxForm(box);
    }

    private void deleteBox(Box box) {
        if (box == null || box.getIdBox() <= 0) {
            AppMessageDialog.showError(this, "Cajas", "Seleccione una caja valida para eliminar.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Desea eliminar la caja " + box.getBoxCode() + "?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boxController.deleteBox(box.getIdBox());
            AppMessageDialog.showInfo(this, "Cajas", "La caja fue eliminada correctamente.");
            loadBoxes();
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Cajas", e.getMessage());
        }
    }

    private Window resolveWindow() {
        return javax.swing.SwingUtilities.getWindowAncestor(this);
    }

    private static class BoxTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 7;

        private final String[] columns = {
            "ID", "Envio", "Codigo", "Medidas", "Peso", "Valor", "Estado", "Acciones"
        };

        private final List<Box> items = new ArrayList<>();

        public void setItems(List<Box> newItems) {
            items.clear();

            if (newItems != null) {
                items.addAll(newItems);
            }

            fireTableDataChanged();
        }

        public Box getBoxAt(int rowIndex) {
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
            Box box = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return box.getIdBox();
                case 1:
                    return box.getIdShipment();
                case 2:
                    return box.getBoxCode();
                case 3:
                    return box.getLengthCm() + " x " + box.getWidthCm() + " x " + box.getHeightCm();
                case 4:
                    return box.getWeightKg();
                case 5:
                    return box.getDeclaredValue();
                case 6:
                    return box.getStatus();
                case ACTIONS_COLUMN_INDEX:
                    return box;
                default:
                    return "";
            }
        }
    }

    private class BoxActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnEdit;
        private final JButton btnDelete;

        BoxActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);

            btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar caja", new Color(245, 124, 0));
            btnDelete = createTableIconButton(FontAwesome.TRASH, "Eliminar caja", new Color(198, 40, 40));

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

    private class BoxActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private int editingRow = -1;

        BoxActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
            panel.setOpaque(true);

            JButton btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar caja", new Color(245, 124, 0));
            JButton btnDelete = createTableIconButton(FontAwesome.TRASH, "Eliminar caja", new Color(198, 40, 40));

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
            int modelRow = tblBoxes.convertRowIndexToModel(editingRow);
            Box box = boxTableModel.getBoxAt(modelRow);
            fireEditingStopped();

            if ("edit".equals(action)) {
                editBox(box);
                return;
            }

            deleteBox(box);
        }
    }

    private class BoxFormDialog extends JDialog {

        private final Box editingBox;
        private boolean saved;

        private JComboBox<ReferenceItem> cmbShipment;
        private JTextField txtBoxCode;
        private JTextField txtImagePath;
        private JTextField txtLengthCm;
        private JTextField txtWidthCm;
        private JTextField txtHeightCm;
        private JTextField txtWeightKg;
        private JTextField txtDeclaredValue;
        private JComboBox<String> cmbStatus;
        private File selectedImage;

        BoxFormDialog(Window owner, Box box) {
            super(owner instanceof Frame ? (Frame) owner : null, true);
            this.editingBox = box;
            buildDialog();
            fillFormIfNeeded();
        }

        boolean isSaved() {
            return saved;
        }

        private void buildDialog() {
            setTitle(editingBox == null ? "Nueva Caja" : "Editar Caja");
            getContentPane().setBackground(new Color(245, 247, 250));
            setLayout(new BorderLayout(0, 0));

            JPanel headerPanel = createDialogHeader();

            cmbShipment = new JComboBox<>();
            cmbShipment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            cmbShipment.setPreferredSize(new Dimension(220, 34));
            loadShipmentOptions();
            txtBoxCode = createInput();
            txtImagePath = createInput();
            txtImagePath.setEditable(false);
            txtLengthCm = createInput();
            txtWidthCm = createInput();
            txtHeightCm = createInput();
            txtWeightKg = createInput();
            txtDeclaredValue = createInput();

            cmbStatus = new JComboBox<>(new String[]{
                "PACKED", "SHIPPED", "IN_TRANSIT", "DELIVERED", "DAMAGED"
            });
            cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            cmbStatus.setPreferredSize(new Dimension(220, 34));

            JButton btnImage = createToolbarButton("Elegir imagen", new Color(21, 101, 192));
            btnImage.addActionListener(e -> {
                File image = ImageStorage.chooseImage(this);

                if (image != null) {
                    selectedImage = image;
                    txtImagePath.setText(image.getAbsolutePath());
                }
            });

            JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
            bodyPanel.setOpaque(false);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JPanel boxCard = new JPanel(new BorderLayout());
            boxCard.setBackground(Color.WHITE);
            boxCard.setBorder(BorderFactory.createCompoundBorder(
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
            addField(formPanel, gbc, 1, "Codigo", txtBoxCode);
            addField(formPanel, gbc, 2, "Imagen", txtImagePath);
            addField(formPanel, gbc, 3, "", btnImage);
            addField(formPanel, gbc, 4, "Largo cm", txtLengthCm);
            addField(formPanel, gbc, 5, "Ancho cm", txtWidthCm);
            addField(formPanel, gbc, 6, "Alto cm", txtHeightCm);
            addField(formPanel, gbc, 7, "Peso kg", txtWeightKg);
            addField(formPanel, gbc, 8, "Valor declarado", txtDeclaredValue);
            addField(formPanel, gbc, 9, "Estado", cmbStatus);

            boxCard.add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setOpaque(false);

            JButton btnSave = createToolbarButton("Guardar", new Color(46, 125, 50));
            btnSave.addActionListener(e -> saveBox());

            JButton btnCancel = createToolbarButton("Cerrar", new Color(96, 125, 139));
            btnCancel.addActionListener(e -> dispose());

            buttonPanel.add(btnSave);
            buttonPanel.add(btnCancel);

            bodyPanel.add(boxCard, BorderLayout.CENTER);
            bodyPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(headerPanel, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);

            pack();
            setMinimumSize(new Dimension(620, 620));
            setLocationRelativeTo(CajasJPanel.this);
        }

        private JPanel createDialogHeader() {
            JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
            headerPanel.setBackground(new Color(13, 71, 161));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JLabel lblHeaderIcon = new JLabel(ViewIcons.build(FontAwesome.ARCHIVE, 24, Color.WHITE));

            JLabel lblHeaderTitle = new JLabel(editingBox == null ? "Nueva Caja" : "Editar Caja");
            lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeaderTitle.setForeground(Color.WHITE);

            JLabel lblHeaderSubtitle = new JLabel("Registre medidas, peso, valor y estado de la caja.");
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
            if (editingBox == null) {
                cmbStatus.setSelectedItem("PACKED");
                return;
            }

            selectReferenceItem(cmbShipment, editingBox.getIdShipment());
            txtBoxCode.setText(editingBox.getBoxCode());
            txtImagePath.setText(editingBox.getImagePath());
            txtLengthCm.setText(String.valueOf(editingBox.getLengthCm()));
            txtWidthCm.setText(String.valueOf(editingBox.getWidthCm()));
            txtHeightCm.setText(String.valueOf(editingBox.getHeightCm()));
            txtWeightKg.setText(String.valueOf(editingBox.getWeightKg()));
            txtDeclaredValue.setText(String.valueOf(editingBox.getDeclaredValue()));
            cmbStatus.setSelectedItem(editingBox.getStatus());
        }

        private void saveBox() {
            try {
                Box box = editingBox == null ? new Box() : editingBox;

                box.setIdShipment(getSelectedReferenceId(cmbShipment, "Seleccione un envio valido."));
                box.setBoxCode(txtBoxCode.getText().trim());
                if (selectedImage != null) {
                    box.setImagePath(ImageStorage.saveImage(selectedImage, "boxes", txtBoxCode.getText()));
                } else if (editingBox == null) {
                    box.setImagePath("");
                }
                box.setLengthCm(parseDecimal(txtLengthCm.getText(), "Ingrese un largo valido."));
                box.setWidthCm(parseDecimal(txtWidthCm.getText(), "Ingrese un ancho valido."));
                box.setHeightCm(parseDecimal(txtHeightCm.getText(), "Ingrese un alto valido."));
                box.setWeightKg(parseDecimal(txtWeightKg.getText(), "Ingrese un peso valido."));
                box.setDeclaredValue(parseDecimal(txtDeclaredValue.getText(), "Ingrese un valor declarado valido."));
                box.setStatus(cmbStatus.getSelectedItem().toString());

                if (editingBox == null) {
                    boxController.createBox(box);
                } else {
                    boxController.updateBox(box);
                }

                saved = true;
                AppMessageDialog.showInfo(this, "Cajas", "Los datos de la caja fueron guardados correctamente.");
                dispose();
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Cajas", e.getMessage());
            }
        }

        private BigDecimal parseDecimal(String value, String message) throws Exception {
            try {
                return new BigDecimal(value.trim());
            } catch (NumberFormatException ex) {
                throw new Exception(message);
            }
        }

        private void loadShipmentOptions() {
            DefaultComboBoxModel<ReferenceItem> model = new DefaultComboBoxModel<>();

            try {
                for (ReferenceItem item : boxController.listShipmentOptions()) {
                    model.addElement(item);
                }
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Cajas", e.getMessage());
            }

            cmbShipment.setModel(model);
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
