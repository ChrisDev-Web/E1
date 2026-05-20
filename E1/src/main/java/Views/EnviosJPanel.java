package Views;

import Controllers.ShipmentController;
import Models.Client;
import Models.Shipment;
import Models.User;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Interfaces: vista funcional del modulo de envios con CRUD, tracking y detalle.
public class EnviosJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "envios";
    private static final String VIEW_TITLE = "Envios";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.TRUCK, 28);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ShipmentController shipmentController;
    private final ShipmentTableModel shipmentTableModel;
    private final Shipment.Filter shipmentFilter;

    private JTextField txtSearch;
    private JComboBox<String> cmbStatusFilter;
    private JCheckBox chkUseDate;
    private DatePicker dtpSearchDate;
    private JComboBox<Integer> cmbPageSize;
    private JLabel lblPageInfo;
    private JButton btnPrevious;
    private JButton btnNext;
    private JTable tblShipments;

    private Shipment.PaginatedResult shipmentResult;

    public EnviosJPanel() {
        this.shipmentController = new ShipmentController();
        this.shipmentTableModel = new ShipmentTableModel();
        this.shipmentFilter = new Shipment.Filter();
        initPanel();
        loadShipments();
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

        JPanel titlePanel = new JPanel(new BorderLayout(12, 0));
        titlePanel.setOpaque(false);

        JLabel lblIcon = new JLabel(VIEW_ICON);

        JLabel lblTitle = new JLabel("Gestion de Envios");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33));

        JLabel lblSubtitle = new JLabel("Consulte envios, haga seguimiento y gestione estados desde una vista mas compacta.");
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
        installSearchDebounce(txtSearch, this::searchFromFirstPage);

        cmbStatusFilter = new JComboBox<>(buildStatusFilterOptions());
        cmbStatusFilter.setPreferredSize(new Dimension(150, 34));
        cmbStatusFilter.addActionListener(e -> searchFromFirstPage());

        chkUseDate = new JCheckBox("Fecha");
        chkUseDate.setOpaque(false);

        dtpSearchDate = buildDatePicker();
        dtpSearchDate.setPreferredSize(new Dimension(160, 34));
        dtpSearchDate.setEnabled(false);

        chkUseDate.addActionListener(e -> {
            dtpSearchDate.setEnabled(chkUseDate.isSelected());

            if (!chkUseDate.isSelected()) {
                dtpSearchDate.clear();
            }

            searchFromFirstPage();
        });

        JButton btnClear = createToolbarButton("Limpiar", null, new Color(96, 125, 139));
        btnClear.addActionListener(e -> clearFilters());

        JButton btnNew = createToolbarButton("Nuevo Envio", null, new Color(46, 125, 50));
        btnNew.addActionListener(e -> openShipmentForm(null));

        JButton btnTracking = createToolbarButton("Buscar Tracking", null, new Color(2, 136, 209));
        btnTracking.addActionListener(e -> searchByTrackingCode());

        toolbarCard.add(new JLabel("Buscar:"));
        toolbarCard.add(txtSearch);
        toolbarCard.add(new JLabel("Estado:"));
        toolbarCard.add(cmbStatusFilter);
        toolbarCard.add(chkUseDate);
        toolbarCard.add(dtpSearchDate);
        toolbarCard.add(btnClear);
        toolbarCard.add(btnNew);
        toolbarCard.add(btnTracking);

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(toolbarCard, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createCenterPanel() {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(214, 223, 233), 1));

        tblShipments = new JTable(shipmentTableModel);
        tblShipments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblShipments.setRowHeight(38);
        tblShipments.getTableHeader().setReorderingAllowed(false);
        tblShipments.getTableHeader().setBackground(new Color(246, 248, 251));
        tblShipments.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblShipments.setShowHorizontalLines(true);
        tblShipments.setGridColor(new Color(232, 236, 240));

        configureShipmentTable(tblShipments, shipmentTableModel);

        JScrollPane scrollPane = new JScrollPane(tblShipments);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        cardPanel.add(scrollPane, BorderLayout.CENTER);
        return cardPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        lblPageInfo = new JLabel("Pagina 1 de 1 | Total: 0");
        lblPageInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cmbPageSize = new JComboBox<>(new Integer[]{10, 20, 50});
        cmbPageSize.setPreferredSize(new Dimension(76, 30));
        cmbPageSize.addActionListener(e -> {
            shipmentFilter.setPageSize((Integer) cmbPageSize.getSelectedItem());
            shipmentFilter.setPage(1);
            loadShipments();
        });

        leftPanel.add(lblPageInfo);
        leftPanel.add(new JLabel("Mostrar:"));
        leftPanel.add(cmbPageSize);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonsPanel.setOpaque(false);

        btnPrevious = createToolbarButton("Anterior", null, new Color(96, 125, 139));
        btnPrevious.addActionListener(e -> changePage(-1));

        btnNext = createToolbarButton("Siguiente", null, new Color(96, 125, 139));
        btnNext.addActionListener(e -> changePage(1));

        buttonsPanel.add(btnPrevious);
        buttonsPanel.add(btnNext);

        footerPanel.add(leftPanel, BorderLayout.WEST);
        footerPanel.add(buttonsPanel, BorderLayout.EAST);
        return footerPanel;
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

    private DatePicker buildDatePicker() {
        DatePickerSettings settings = new DatePickerSettings(new Locale("es", "PE"));
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");
        settings.setVisibleClearButton(false);
        settings.setGapBeforeButtonPixels(4);

        DatePicker datePicker = new DatePicker(settings);
        configureDatePickerButton(datePicker);
        return datePicker;
    }

    private DateTimePicker buildDateTimePicker() {
        DatePickerSettings settings = new DatePickerSettings(new Locale("es", "PE"));
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");
        settings.setVisibleClearButton(false);
        settings.setGapBeforeButtonPixels(4);

        DateTimePicker dateTimePicker = new DateTimePicker(settings, null);
        configureDatePickerButton(dateTimePicker.getDatePicker());
        return dateTimePicker;
    }

    private void configureDatePickerButton(DatePicker datePicker) {
        JButton calendarButton = datePicker.getComponentToggleCalendarButton();
        calendarButton.setText("");
        calendarButton.setIcon(ViewIcons.build(FontAwesome.CALENDAR, 14, new Color(27, 94, 157)));
        calendarButton.setPreferredSize(new Dimension(36, 34));
        calendarButton.setMinimumSize(new Dimension(36, 34));
        calendarButton.setFocusPainted(false);
        calendarButton.setToolTipText("Abrir calendario");
    }

    private void installSearchDebounce(JTextField textField, Runnable action) {
        Timer timer = new Timer(280, e -> action.run());
        timer.setRepeats(false);

        textField.getDocument().addDocumentListener(new DocumentListener() {
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

        textField.addActionListener(e -> {
            timer.stop();
            action.run();
        });
    }

    private void configureShipmentTable(JTable table, ShipmentTableModel tableModel) {
        int actionsColumn = tableModel.getActionColumnIndex();
        table.getColumnModel().getColumn(0).setPreferredWidth(220);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(170);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(actionsColumn).setPreferredWidth(170);
        table.getColumnModel().getColumn(actionsColumn).setMinWidth(170);
        table.getColumnModel().getColumn(actionsColumn).setMaxWidth(170);
        table.getColumnModel().getColumn(actionsColumn).setCellRenderer(new ShipmentActionCellRenderer());
        table.getColumnModel().getColumn(actionsColumn).setCellEditor(new ShipmentActionCellEditor(table, tableModel));
    }

    private String[] buildStatusFilterOptions() {
        List<String> options = new ArrayList<>();
        options.add("TODOS");

        for (Shipment.Status status : Shipment.Status.values()) {
            options.add(status.name());
        }

        return options.toArray(new String[0]);
    }

    private void searchFromFirstPage() {
        shipmentFilter.setPage(1);
        loadShipments();
    }

    private void clearFilters() {
        if (txtSearch != null) {
            txtSearch.setText("");
        }

        if (cmbStatusFilter != null) {
            cmbStatusFilter.setSelectedItem("TODOS");
        }

        if (chkUseDate != null) {
            chkUseDate.setSelected(false);
        }

        if (dtpSearchDate != null) {
            dtpSearchDate.clear();
            dtpSearchDate.setEnabled(false);
        }

        shipmentFilter.setPage(1);
        loadShipments();
    }

    private void changePage(int delta) {
        if (shipmentResult == null) {
            return;
        }

        int nextPage = shipmentFilter.getPage() + delta;

        if (nextPage <= 0 || nextPage > shipmentResult.getTotalPages()) {
            return;
        }

        shipmentFilter.setPage(nextPage);
        loadShipments();
    }

    private void loadShipments() {
        shipmentFilter.setSearchText(txtSearch == null ? null : txtSearch.getText());
        shipmentFilter.setStatus(resolveStatusFilter());
        shipmentFilter.setPageSize(cmbPageSize == null ? shipmentFilter.getPageSize() : (Integer) cmbPageSize.getSelectedItem());
        shipmentFilter.setShipmentDate(chkUseDate != null && chkUseDate.isSelected() ? dtpSearchDate.getDate() : null);

        try {
            shipmentResult = shipmentController.searchShipments(shipmentFilter);
            shipmentTableModel.setItems(shipmentResult.getItems());
            lblPageInfo.setText(
                    "Pagina " + shipmentResult.getPage()
                    + " de " + shipmentResult.getTotalPages()
                    + " | Total: " + shipmentResult.getTotalRecords()
            );
            btnPrevious.setEnabled(shipmentResult.getPage() > 1);
            btnNext.setEnabled(shipmentResult.getPage() < shipmentResult.getTotalPages());
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Envios", e.getMessage());
        }
    }

    private Shipment.Status resolveStatusFilter() {
        String selected = cmbStatusFilter == null ? "TODOS" : (String) cmbStatusFilter.getSelectedItem();

        if (selected == null || "TODOS".equalsIgnoreCase(selected)) {
            return null;
        }

        return Shipment.Status.valueOf(selected);
    }

    private void openShipmentForm(Shipment shipment) {
        ShipmentFormDialog dialog = new ShipmentFormDialog(resolveWindow(), shipment);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadShipments();
        }
    }

    private void editShipment(int idShipment) {
        if (idShipment <= 0) {
            AppMessageDialog.showError(this, "Envios", "Seleccione un envio para editar.");
            return;
        }

        try {
            Shipment shipment = shipmentController.findShipmentById(idShipment);
            openShipmentForm(shipment);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Envios", e.getMessage());
        }
    }

    private void openShipmentDetail(int idShipment) {
        if (idShipment <= 0) {
            AppMessageDialog.showError(this, "Envios", "Seleccione un envio para ver el detalle.");
            return;
        }

        try {
            Shipment.Detail detail = shipmentController.getShipmentDetail(idShipment);
            ShipmentDetailDialog dialog = new ShipmentDetailDialog(resolveWindow(), detail);
            dialog.setVisible(true);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Envios", e.getMessage());
        }
    }

    private void cancelShipmentFromList(int idShipment) {
        if (idShipment <= 0) {
            AppMessageDialog.showError(this, "Envios", "Seleccione un envio para cancelarlo.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "El envio sera marcado como cancelado. Desea continuar?",
                "Confirmar cancelacion",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            shipmentController.cancelShipment(
                    idShipment,
                    resolveCurrentUserId(),
                    "Sistema",
                    "Envio cancelado desde el listado."
            );
            AppMessageDialog.showInfo(this, "Envios", "El envio fue cancelado correctamente.");
            loadShipments();
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Envios", e.getMessage());
        }
    }

    private void searchByTrackingCode() {
        String trackingCode = JOptionPane.showInputDialog(this, "Ingrese el codigo de tracking:");

        if (trackingCode == null || trackingCode.trim().isEmpty()) {
            return;
        }

        try {
            Shipment shipment = shipmentController.findShipmentByTrackingCode(trackingCode.trim());

            if (shipment == null) {
                AppMessageDialog.showError(this, "Envios", "No se encontro un envio con ese codigo de tracking.");
                return;
            }

            ShipmentDetailDialog dialog = new ShipmentDetailDialog(resolveWindow(), shipmentController.getShipmentDetail(shipment.getIdShipment()));
            dialog.setVisible(true);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Envios", e.getMessage());
        }
    }

    private void registerTrackingForShipment(int idShipment) {
        if (idShipment <= 0) {
            AppMessageDialog.showError(this, "Envios", "Seleccione un envio para registrar seguimiento.");
            return;
        }

        Shipment shipment;

        try {
            shipment = shipmentController.findShipmentById(idShipment);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Envios", e.getMessage());
            return;
        }

        Shipment.Status status = (Shipment.Status) JOptionPane.showInputDialog(
                this,
                "Seleccione el estado a registrar:",
                "Registrar seguimiento",
                JOptionPane.PLAIN_MESSAGE,
                null,
                Shipment.Status.values(),
                shipment == null ? Shipment.Status.PENDING : shipment.getStatus()
        );

        if (status == null) {
            return;
        }

        String location = JOptionPane.showInputDialog(this, "Ubicacion del seguimiento:", "Ubicacion");
        String description = JOptionPane.showInputDialog(this, "Comentarios del seguimiento:", "Seguimiento manual");

        try {
            shipmentController.registerTracking(
                    idShipment,
                    status,
                    location,
                    description,
                    resolveCurrentUserId()
            );
            AppMessageDialog.showInfo(this, "Envios", "El seguimiento fue registrado correctamente.");
            loadShipments();
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Envios", e.getMessage());
        }
    }

    private Integer resolveCurrentUserId() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        return currentUser == null ? null : currentUser.getIdUser();
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

    private static class ShipmentTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 4;

        private final String[] columns = {
            "Tracking", "Cliente", "Fecha Estimada", "Estado", "Acciones"
        };

        private final List<Shipment> items = new ArrayList<>();

        public void setItems(List<Shipment> newItems) {
            items.clear();

            if (newItems != null) {
                items.addAll(newItems);
            }

            fireTableDataChanged();
        }

        public Shipment getShipmentAt(int rowIndex) {
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
            Shipment shipment = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return safeValue(shipment.getTrackingCode());
                case 1:
                    return safeValue(shipment.getClientName());
                case 2:
                    return shipment.getEstimatedDeliveryDate() == null ? "-" : shipment.getEstimatedDeliveryDate().format(DATE_TIME_FORMATTER);
                case 3:
                    return shipment.getStatus() == null ? "-" : shipment.getStatus().name();
                case ACTIONS_COLUMN_INDEX:
                    return shipment;
                default:
                    return "";
            }
        }

        private String safeValue(String value) {
            return value == null || value.isBlank() ? "-" : value;
        }
    }

    private class ShipmentActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnDetail;
        private final JButton btnEdit;
        private final JButton btnDelete;

        ShipmentActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);

            btnDetail = createTableIconButton(FontAwesome.EYE, "Ver detalle", new Color(69, 90, 100));
            btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar", new Color(245, 124, 0));
            btnDelete = createTableIconButton(FontAwesome.TRASH, "Cancelar envio", new Color(198, 40, 40));

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

    private class ShipmentActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JTable table;
        private final ShipmentTableModel tableModel;
        private final JPanel panel;
        private int editingRow = -1;

        ShipmentActionCellEditor(JTable table, ShipmentTableModel tableModel) {
            this.table = table;
            this.tableModel = tableModel;
            this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
            this.panel.setOpaque(true);

            JButton btnDetail = createTableIconButton(FontAwesome.EYE, "Ver detalle", new Color(69, 90, 100));
            JButton btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar", new Color(245, 124, 0));
            JButton btnDelete = createTableIconButton(FontAwesome.TRASH, "Cancelar envio", new Color(198, 40, 40));

            btnDetail.addActionListener(e -> handleAction("detail"));
            btnEdit.addActionListener(e -> handleAction("edit"));
            btnDelete.addActionListener(e -> handleAction("delete"));

            panel.add(btnDetail);
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
            int modelRow = table.convertRowIndexToModel(editingRow);
            Shipment shipment = tableModel.getShipmentAt(modelRow);
            fireEditingStopped();

            if ("detail".equals(action)) {
                openShipmentDetail(shipment.getIdShipment());
                return;
            }

            if ("edit".equals(action)) {
                editShipment(shipment.getIdShipment());
                return;
            }

            cancelShipmentFromList(shipment.getIdShipment());
        }
    }

    private class ShipmentFormDialog extends JDialog {

        private final Shipment editingShipment;
        private boolean saved;

        private JTextField txtTrackingCode;
        private JComboBox<Client.ReferenceItem> cmbClient;
        private JComboBox<Shipment.ReferenceItem> cmbWarehouse;
        private JComboBox<Shipment.ReferenceItem> cmbResponsibleUser;
        private DateTimePicker dtpEstimatedDate;
        private JComboBox<Shipment.Status> cmbStatus;
        private JTextArea txtNotes;

        ShipmentFormDialog(Window owner, Shipment shipment) {
            super(owner instanceof Frame ? (Frame) owner : null, true);
            this.editingShipment = shipment;
            buildDialog();
            loadOptions();
            fillFormIfNeeded();
        }

        boolean isSaved() {
            return saved;
        }

        private void buildDialog() {
            setTitle(editingShipment == null ? "Nuevo Envio" : "Editar Envio");
            DialogUtils.applyDialogTheme(this);
            setLayout(new BorderLayout(0, 0));

            JPanel headerPanel = DialogUtils.createHeader(
                    ViewIcons.build(FontAwesome.TRUCK, 24, Color.WHITE),
                    editingShipment == null ? "Nuevo Envio" : "Editar Envio",
                    editingShipment == null
                            ? "Configure los datos esenciales del envio y su fecha estimada."
                            : "Actualice el envio y gestione sus acciones operativas desde este panel.",
                    new Color(13, 71, 161)
            );

            txtTrackingCode = DialogUtils.styleInput(new JTextField(20));
            txtTrackingCode.setEditable(false);
            txtTrackingCode.setText(editingShipment == null ? "Se genera automaticamente por trigger" : "");

            cmbClient = DialogUtils.styleInput(new JComboBox<>());
            cmbWarehouse = DialogUtils.styleInput(new JComboBox<>());
            cmbResponsibleUser = DialogUtils.styleInput(new JComboBox<>());
            dtpEstimatedDate = buildDateTimePicker();
            dtpEstimatedDate.setPreferredSize(new Dimension(220, 34));
            cmbStatus = DialogUtils.styleInput(new JComboBox<>(Shipment.Status.values()));
            txtNotes = DialogUtils.styleTextArea(new JTextArea(8, 24));

            JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
            bodyPanel.setOpaque(false);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JPanel shipmentCard = DialogUtils.createCard("Datos del Envio");
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            addField(formPanel, gbc, 0, "Tracking", txtTrackingCode);
            addField(formPanel, gbc, 1, "Cliente", cmbClient);
            addField(formPanel, gbc, 2, "Almacen de origen", cmbWarehouse);
            addField(formPanel, gbc, 3, "Usuario responsable", cmbResponsibleUser);
            addField(formPanel, gbc, 4, "Fecha estimada", dtpEstimatedDate);
            addField(formPanel, gbc, 5, "Estado", cmbStatus);
            shipmentCard.add(formPanel, BorderLayout.CENTER);

            JPanel notesCard = DialogUtils.createCard("Notas y Observaciones");
            notesCard.add(DialogUtils.createScrollPane(txtNotes), BorderLayout.CENTER);

            if (editingShipment == null) {
                cmbStatus.setSelectedItem(Shipment.Status.PENDING);
                cmbStatus.setEnabled(false);
                dtpEstimatedDate.setDateTimeStrict(LocalDateTime.now().plusDays(1));
            } else {
                cmbResponsibleUser.setEnabled(false);
            }

            JPanel actionPanel = DialogUtils.createCard("Acciones Rapidas");

            if (editingShipment != null) {
                JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                quickActions.setOpaque(false);

                JButton btnRegisterTracking = createToolbarButton("Registrar Seguimiento", null, new Color(121, 85, 72));
                btnRegisterTracking.addActionListener(e -> registerTrackingFromDialog());

                JButton btnDelivered = createToolbarButton("Marcar Entregado", null, new Color(0, 121, 107));
                btnDelivered.addActionListener(e -> markAsDeliveredFromDialog());

                JButton btnCancelShipment = createToolbarButton("Cancelar Envio", null, new Color(198, 40, 40));
                btnCancelShipment.addActionListener(e -> cancelFromDialog());

                quickActions.add(btnRegisterTracking);
                quickActions.add(btnDelivered);
                quickActions.add(btnCancelShipment);
                actionPanel.add(quickActions, BorderLayout.CENTER);
            } else {
                JPanel helperPanel = new JPanel(new BorderLayout());
                helperPanel.setOpaque(false);
                helperPanel.add(new JLabel("El estado inicial se genera automaticamente como PENDING."), BorderLayout.CENTER);
                actionPanel.add(helperPanel, BorderLayout.CENTER);
            }

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            buttonPanel.setOpaque(false);

            JButton btnSave = createToolbarButton("Guardar", ViewIcons.build(FontAwesome.FLOPPY_O, 14, Color.WHITE), new Color(46, 125, 50));
            btnSave.addActionListener(e -> saveShipment());

            JButton btnCancel = createToolbarButton("Cerrar", null, new Color(96, 125, 139));
            btnCancel.addActionListener(e -> dispose());

            buttonPanel.add(btnSave);
            buttonPanel.add(btnCancel);

            JPanel contentGrid = new JPanel(new GridLayout(1, 2, 16, 0));
            contentGrid.setOpaque(false);
            contentGrid.add(shipmentCard);

            JPanel rightColumn = new JPanel(new BorderLayout(0, 16));
            rightColumn.setOpaque(false);
            rightColumn.add(actionPanel, BorderLayout.NORTH);
            rightColumn.add(notesCard, BorderLayout.CENTER);
            contentGrid.add(rightColumn);

            bodyPanel.add(contentGrid, BorderLayout.CENTER);
            bodyPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(headerPanel, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);
            pack();
            setMinimumSize(new Dimension(980, 620));
            DialogUtils.centerAndLock(this, EnviosJPanel.this);
        }

        private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            panel.add(DialogUtils.createFieldLabel(label), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            panel.add(field, gbc);
        }

        private void loadOptions() {
            try {
                loadReferenceModel(cmbClient, shipmentController.listClientOptions());
                loadReferenceModel(cmbWarehouse, shipmentController.listWarehouseOptions());
                loadReferenceModel(cmbResponsibleUser, shipmentController.listUserOptions());
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Envios", e.getMessage());
            }
        }

        private <T> void loadReferenceModel(JComboBox<T> comboBox, List<T> items) {
            DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();

            for (T item : items) {
                model.addElement(item);
            }

            comboBox.setModel(model);
        }

        private void fillFormIfNeeded() {
            if (editingShipment == null) {
                return;
            }

            txtTrackingCode.setText(editingShipment.getTrackingCode());
            selectReferenceItem(cmbClient, editingShipment.getIdClient());
            selectReferenceItem(cmbWarehouse, editingShipment.getIdWarehouseOrigin());
            selectReferenceItem(cmbResponsibleUser, editingShipment.getIdUser());
            dtpEstimatedDate.setDateTimeStrict(editingShipment.getEstimatedDeliveryDate());
            cmbStatus.setSelectedItem(editingShipment.getStatus());
            txtNotes.setText(editingShipment.getNotes());
        }

        private <T> void selectReferenceItem(JComboBox<T> comboBox, int id) {
            for (int index = 0; index < comboBox.getItemCount(); index++) {
                Object item = comboBox.getItemAt(index);

                if (item instanceof Client.ReferenceItem && ((Client.ReferenceItem) item).getId() == id) {
                    comboBox.setSelectedIndex(index);
                    break;
                }

                if (item instanceof Shipment.ReferenceItem && ((Shipment.ReferenceItem) item).getId() == id) {
                    comboBox.setSelectedIndex(index);
                    break;
                }
            }
        }

        private void saveShipment() {
            try {
                Client.ReferenceItem client = (Client.ReferenceItem) cmbClient.getSelectedItem();
                Shipment.ReferenceItem warehouse = (Shipment.ReferenceItem) cmbWarehouse.getSelectedItem();
                Shipment.ReferenceItem responsibleUser = (Shipment.ReferenceItem) cmbResponsibleUser.getSelectedItem();

                Shipment shipment = editingShipment == null ? new Shipment() : editingShipment;
                shipment.setTrackingCode(editingShipment == null ? null : txtTrackingCode.getText());
                shipment.setIdClient(client == null ? 0 : client.getId());
                shipment.setIdWarehouseOrigin(warehouse == null ? 0 : warehouse.getId());
                shipment.setIdUser(responsibleUser == null ? 0 : responsibleUser.getId());
                shipment.setChangedByUserId(resolveCurrentUserId());
                shipment.setEstimatedDeliveryDate(dtpEstimatedDate.getDateTimeStrict());
                shipment.setStatus((Shipment.Status) cmbStatus.getSelectedItem());
                shipment.setNotes(txtNotes.getText());

                if (editingShipment == null) {
                    shipmentController.createShipment(shipment);
                } else {
                    shipmentController.updateShipment(shipment);
                }

                saved = true;
                AppMessageDialog.showInfo(this, "Envios", "Los datos del envio fueron guardados correctamente.");
                dispose();
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Envios", e.getMessage());
            }
        }

        private void registerTrackingFromDialog() {
            if (editingShipment == null) {
                return;
            }

            Shipment.Status status = (Shipment.Status) JOptionPane.showInputDialog(
                    this,
                    "Seleccione el estado a registrar:",
                    "Registrar seguimiento",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    Shipment.Status.values(),
                    cmbStatus.getSelectedItem()
            );

            if (status == null) {
                return;
            }

            String location = JOptionPane.showInputDialog(this, "Ubicacion del seguimiento:", "Ubicacion");
            String description = JOptionPane.showInputDialog(this, "Comentarios del seguimiento:", "Seguimiento manual");

            try {
                shipmentController.registerTracking(
                        editingShipment.getIdShipment(),
                        status,
                        location,
                        description,
                        resolveCurrentUserId()
                );
                saved = true;
                AppMessageDialog.showInfo(this, "Envios", "El seguimiento fue registrado correctamente.");
                dispose();
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Envios", e.getMessage());
            }
        }

        private void markAsDeliveredFromDialog() {
            if (editingShipment == null) {
                return;
            }

            String location = JOptionPane.showInputDialog(this, "Ubicacion de entrega:", "Ubicacion");
            String note = JOptionPane.showInputDialog(this, "Comentarios de entrega:", "Entrega completada");

            try {
                shipmentController.markShipmentAsDelivered(
                        editingShipment.getIdShipment(),
                        resolveCurrentUserId(),
                        location,
                        note
                );
                saved = true;
                AppMessageDialog.showInfo(this, "Envios", "El envio fue marcado como entregado.");
                dispose();
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Envios", e.getMessage());
            }
        }

        private void cancelFromDialog() {
            if (editingShipment == null) {
                return;
            }

            String location = JOptionPane.showInputDialog(this, "Ubicacion de cancelacion:", "Ubicacion");
            String note = JOptionPane.showInputDialog(this, "Comentarios de cancelacion:", "Cancelacion de envio");

            try {
                shipmentController.cancelShipment(
                        editingShipment.getIdShipment(),
                        resolveCurrentUserId(),
                        location,
                        note
                );
                saved = true;
                AppMessageDialog.showInfo(this, "Envios", "El envio fue cancelado correctamente.");
                dispose();
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Envios", e.getMessage());
            }
        }
    }

    private class ShipmentDetailDialog extends JDialog {

        ShipmentDetailDialog(Window owner, Shipment.Detail detail) {
            super(owner instanceof Frame ? (Frame) owner : null, "Detalle de Envio", true);
            buildDialog(detail);
        }

        private void buildDialog(Shipment.Detail detail) {
            DialogUtils.applyDialogTheme(this);
            setLayout(new BorderLayout(0, 0));
            setPreferredSize(new Dimension(1180, 860));

            Shipment shipment = detail.getShipment();

            JPanel headerPanel = DialogUtils.createHeader(
                    ViewIcons.build(FontAwesome.TRUCK, 24, Color.WHITE),
                    "Detalle de Envio",
                    "Visualice el estado actual, historial y contenido del envio.",
                    new Color(13, 71, 161)
            );

            JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
            bodyPanel.setOpaque(false);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 12, 12));
            summaryPanel.setOpaque(false);
            summaryPanel.add(DialogUtils.createInfoTile("Tracking", shipment == null ? "-" : shipment.getTrackingCode()));
            summaryPanel.add(DialogUtils.createInfoTile("Cliente", shipment == null ? "-" : shipment.getClientName()));
            summaryPanel.add(DialogUtils.createInfoTile("Estado", shipment == null || shipment.getStatus() == null ? "-" : shipment.getStatus().name()));
            summaryPanel.add(DialogUtils.createInfoTile("Fecha estimada", shipment == null ? "-" : formatDateTime(shipment.getEstimatedDeliveryDate())));

            JPanel infoCard = DialogUtils.createCard("Datos Generales");
            JPanel infoGrid = new JPanel(new GridLayout(0, 2, 12, 12));
            infoGrid.setOpaque(false);
            infoGrid.add(DialogUtils.createInfoTile("Id envio", shipment == null ? "-" : String.valueOf(shipment.getIdShipment())));
            infoGrid.add(DialogUtils.createInfoTile("Almacen", shipment == null ? "-" : shipment.getWarehouseName()));
            infoGrid.add(DialogUtils.createInfoTile("Usuario responsable", shipment == null ? "-" : shipment.getUserName()));
            infoGrid.add(DialogUtils.createInfoTile("Fecha envio", shipment == null ? "-" : formatDateTime(shipment.getShipmentDate())));
            infoGrid.add(DialogUtils.createInfoTile("Entregado en", shipment == null ? "-" : formatDateTime(shipment.getDeliveredAt())));
            infoGrid.add(DialogUtils.createInfoTile("Notas", shipment == null ? "-" : shipment.getNotes()));
            infoCard.add(infoGrid, BorderLayout.CENTER);

            JTable trackingTable = new JTable(new TrackingTableModel(detail.getTrackingHistory()));
            trackingTable.setRowHeight(28);
            trackingTable.getTableHeader().setReorderingAllowed(false);
            trackingTable.getTableHeader().setBackground(new Color(246, 248, 251));
            trackingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

            JList<String> boxList = createDetailList(detail.getBoxDetails(), "Sin cajas registradas.");
            JList<String> productList = createDetailList(detail.getProductDetails(), "Sin productos registrados.");

            JPanel lowerPanel = new JPanel(new GridLayout(1, 3, 12, 12));
            lowerPanel.setOpaque(false);
            lowerPanel.add(createSectionPanel("Historial de Seguimiento", DialogUtils.createScrollPane(trackingTable)));
            lowerPanel.add(createSectionPanel("Cajas del Envio", DialogUtils.createScrollPane(boxList)));
            lowerPanel.add(createSectionPanel("Productos Enviados", DialogUtils.createScrollPane(productList)));

            JButton btnClose = createToolbarButton("Cerrar", null, new Color(96, 125, 139));
            btnClose.addActionListener(e -> dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setOpaque(false);
            buttonPanel.add(btnClose);

            bodyPanel.add(summaryPanel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
            contentPanel.setOpaque(false);
            contentPanel.add(infoCard, BorderLayout.NORTH);
            contentPanel.add(lowerPanel, BorderLayout.CENTER);

            bodyPanel.add(contentPanel, BorderLayout.CENTER);

            add(headerPanel, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            pack();
            setMinimumSize(new Dimension(1120, 820));
            DialogUtils.centerAndLock(this, EnviosJPanel.this);
        }

        private JPanel createSectionPanel(String title, Component component) {
            JPanel panel = DialogUtils.createCard(title);
            panel.add(component, BorderLayout.CENTER);
            return panel;
        }

        private JList<String> createDetailList(List<String> items, String fallbackText) {
            DefaultListModel<String> model = new DefaultListModel<>();

            if (items == null || items.isEmpty()) {
                model.addElement(fallbackText);
            } else {
                for (String item : items) {
                    model.addElement(item);
                }
            }

            JList<String> list = new JList<>(model);
            list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            list.setFixedCellHeight(26);
            list.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            return list;
        }
    }

    private class TrackingTableModel extends AbstractTableModel {

        private final String[] columns = {"Fecha", "Estado", "Ubicacion", "Comentarios", "Usuario"};
        private final List<Shipment.Tracking> items;

        TrackingTableModel(List<Shipment.Tracking> items) {
            this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
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
            Shipment.Tracking tracking = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return formatDateTime(tracking.getTrackingDate());
                case 1:
                    return tracking.getStatus() == null ? "-" : tracking.getStatus().name();
                case 2:
                    return tracking.getLocation();
                case 3:
                    return tracking.getComments();
                case 4:
                    return tracking.getUserName();
                default:
                    return "";
            }
        }
    }
}
