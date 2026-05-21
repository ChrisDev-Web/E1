package Views;

import Controllers.ClientController;
import Models.Client;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;

// Swing + Interfaces: vista funcional del modulo de clientes con CRUD y paginacion.
public class ClientesJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "clientes";
    private static final String VIEW_TITLE = "Clientes";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.USERS, 28);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ClientController clientController;
    private final ClientTableModel clientTableModel;
    private final Client.Filter activeFilter;

    private JTextField txtSearch;
    private JComboBox<Integer> cmbPageSize;
    private JLabel lblPageInfo;
    private JButton btnPrevious;
    private JButton btnNext;
    private JTable tblClients;

    private Client.PaginatedResult activeResult;

    public ClientesJPanel() {
        this.clientController = new ClientController();
        this.clientTableModel = new ClientTableModel();
        this.activeFilter = new Client.Filter();
        initPanel();
        loadActiveClients();
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

    @Override
    public void onViewShown() {
        loadActiveClients();
    }

    private JPanel createHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout(12, 0));
        titlePanel.setOpaque(false);

        JLabel lblIcon = new JLabel(VIEW_ICON);

        JLabel lblTitle = new JLabel("Gestion de Clientes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33));

        JLabel lblSubtitle = new JLabel("Consulte clientes activos y administre los registros inactivos desde una sola vista.");
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

        txtSearch = new JTextField(26);
        txtSearch.setPreferredSize(new Dimension(280, 36));
        installSearchDebounce(txtSearch, this::searchFromFirstPage);

        JButton btnClear = createToolbarButton(
                "Limpiar",
                ViewIcons.build(FontAwesome.ERASER, 14, Color.WHITE),
                new Color(96, 125, 139)
        );
        btnClear.addActionListener(e -> clearActiveSearch());

        JButton btnNew = createToolbarButton(
                "Crear",
                ViewIcons.build(FontAwesome.PLUS, 14, Color.WHITE),
                new Color(46, 125, 50)
        );
        btnNew.addActionListener(e -> openClientForm(null));

        JButton btnInactive = createToolbarButton(
                "Ver Inactivos",
                ViewIcons.build(FontAwesome.ARCHIVE, 14, Color.WHITE),
                new Color(55, 71, 79)
        );
        btnInactive.addActionListener(e -> openInactiveClientsDialog());

        toolbarCard.add(new JLabel("Buscar:"));
        toolbarCard.add(txtSearch);
        toolbarCard.add(btnClear);
        toolbarCard.add(btnNew);
        toolbarCard.add(btnInactive);

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(toolbarCard, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createCenterPanel() {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(214, 223, 233), 1));

        tblClients = new JTable(clientTableModel);
        tblClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblClients.setRowHeight(38);
        tblClients.getTableHeader().setReorderingAllowed(false);
        tblClients.getTableHeader().setBackground(new Color(246, 248, 251));
        tblClients.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblClients.setShowHorizontalLines(true);
        tblClients.setGridColor(new Color(232, 236, 240));

        configureClientTable(tblClients, clientTableModel, false);

        JScrollPane scrollPane = new JScrollPane(tblClients);
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
            activeFilter.setPageSize((Integer) cmbPageSize.getSelectedItem());
            activeFilter.setPage(1);
            loadActiveClients();
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

    private void configureClientTable(JTable table, ClientTableModel tableModel, boolean inactiveTable) {
        int actionsColumn = tableModel.getActionColumnIndex();
        table.getColumnModel().getColumn(0).setPreferredWidth(240);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(actionsColumn).setPreferredWidth(170);
        table.getColumnModel().getColumn(actionsColumn).setMinWidth(170);
        table.getColumnModel().getColumn(actionsColumn).setMaxWidth(170);
        table.getColumnModel().getColumn(actionsColumn).setCellRenderer(new ClientActionCellRenderer(inactiveTable));
        table.getColumnModel().getColumn(actionsColumn).setCellEditor(new ClientActionCellEditor(table, tableModel, inactiveTable));
    }

    private void searchFromFirstPage() {
        activeFilter.setSearchText(txtSearch.getText());
        activeFilter.setPage(1);
        loadActiveClients();
    }

    private void clearActiveSearch() {
        boolean changed = txtSearch != null && !txtSearch.getText().trim().isEmpty();
        activeFilter.setPage(1);

        if (txtSearch != null) {
            txtSearch.setText("");
        }

        if (!changed) {
            loadActiveClients();
        }
    }

    private void changePage(int delta) {
        if (activeResult == null) {
            return;
        }

        int nextPage = activeFilter.getPage() + delta;

        if (nextPage <= 0 || nextPage > activeResult.getTotalPages()) {
            return;
        }

        activeFilter.setPage(nextPage);
        loadActiveClients();
    }

    private void loadActiveClients() {
        activeFilter.setSearchText(txtSearch == null ? null : txtSearch.getText());
        activeFilter.setPageSize(cmbPageSize == null ? activeFilter.getPageSize() : (Integer) cmbPageSize.getSelectedItem());

        try {
            activeResult = clientController.searchActiveClients(activeFilter);
            clientTableModel.setItems(activeResult.getItems());
            updatePaginationState(activeResult);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Clientes", e.getMessage());
        }
    }

    private void updatePaginationState(Client.PaginatedResult result) {
        lblPageInfo.setText(
                "Pagina " + result.getPage()
                + " de " + result.getTotalPages()
                + " | Total: " + result.getTotalRecords()
        );

        btnPrevious.setEnabled(result.getPage() > 1);
        btnNext.setEnabled(result.getPage() < result.getTotalPages());
    }

    private void openClientForm(Client client) {
        ClientFormDialog dialog = new ClientFormDialog(resolveWindow(), client);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadActiveClients();
        }
    }

    private void editClient(int idClient) {
        if (idClient <= 0) {
            AppMessageDialog.showError(this, "Clientes", "Seleccione un cliente para editar.");
            return;
        }

        try {
            Client client = clientController.findClientById(idClient);
            openClientForm(client);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Clientes", e.getMessage());
        }
    }

    private void openClientDetail(int idClient) {
        if (idClient <= 0) {
            AppMessageDialog.showError(this, "Clientes", "Seleccione un cliente valido.");
            return;
        }

        try {
            Client client = clientController.findClientById(idClient);
            ClientDetailDialog dialog = new ClientDetailDialog(resolveWindow(), client);
            dialog.setVisible(true);
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Clientes", e.getMessage());
        }
    }

    private void softDeleteClient(int idClient) {
        if (idClient <= 0) {
            AppMessageDialog.showError(this, "Clientes", "Seleccione un cliente para desactivar.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "El cliente seleccionado pasara a inactivos. Desea continuar?",
                "Confirmar desactivacion",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            clientController.softDeleteClient(idClient);
            AppMessageDialog.showInfo(this, "Clientes", "El cliente fue enviado a inactivos.");
            loadActiveClients();
        } catch (Exception e) {
            AppMessageDialog.showError(this, "Clientes", e.getMessage());
        }
    }

    private void openInactiveClientsDialog() {
        InactiveClientsDialog dialog = new InactiveClientsDialog(resolveWindow());
        dialog.setVisible(true);

        if (dialog.isDataChanged()) {
            loadActiveClients();
        }
    }

    private Window resolveWindow() {
        return javax.swing.SwingUtilities.getWindowAncestor(this);
    }

    private String formatDocument(Client client) {
        String documentType = valueOrDash(client.getDocumentTypeName());
        String documentNumber = client.getDocumentNumber() == null ? "-" : String.valueOf(client.getDocumentNumber());
        return documentType + " - " + documentNumber;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "-" : DATE_TIME_FORMATTER.format(dateTime);
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private static class ClientTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 4;

        private final String[] columns = {
            "Cliente", "Documento", "Telefono", "Ciudad", "Acciones"
        };

        private final List<Client> items = new ArrayList<>();

        public void setItems(List<Client> newItems) {
            items.clear();

            if (newItems != null) {
                items.addAll(newItems);
            }

            fireTableDataChanged();
        }

        public Client getClientAt(int rowIndex) {
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
            Client client = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return client.getFullName();
                case 1:
                    return buildDocumentValue(client);
                case 2:
                    return buildSimpleValue(client.getPhone());
                case 3:
                    return buildSimpleValue(client.getCity());
                case ACTIONS_COLUMN_INDEX:
                    return client;
                default:
                    return "";
            }
        }

        private String buildDocumentValue(Client client) {
            String documentType = buildSimpleValue(client.getDocumentTypeName());
            String documentNumber = client.getDocumentNumber() == null ? "-" : String.valueOf(client.getDocumentNumber());
            return documentType + " - " + documentNumber;
        }

        private String buildSimpleValue(String value) {
            return value == null || value.trim().isEmpty() ? "-" : value.trim();
        }
    }

    private class ClientActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnPrimary;
        private final JButton btnSecondary;
        private final JButton btnDanger;

        ClientActionCellRenderer(boolean inactiveTable) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);

            btnPrimary = createTableIconButton(FontAwesome.EYE, "Ver detalle", new Color(69, 90, 100));
            btnSecondary = createTableIconButton(
                    inactiveTable ? FontAwesome.UNDO : FontAwesome.PENCIL,
                    inactiveTable ? "Restaurar" : "Editar",
                    inactiveTable ? new Color(46, 125, 50) : new Color(245, 124, 0)
            );
            btnDanger = createTableIconButton(FontAwesome.TRASH, inactiveTable ? "Eliminar fisico" : "Eliminar", new Color(198, 40, 40));

            add(btnPrimary);
            add(btnSecondary);
            add(btnDanger);
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
            btnPrimary.setBackground(background);
            btnSecondary.setBackground(background);
            btnDanger.setBackground(background);
            return this;
        }
    }

    private class ClientActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JTable table;
        private final ClientTableModel tableModel;
        private final boolean inactiveTable;
        private final JPanel panel;
        private int editingRow = -1;

        ClientActionCellEditor(JTable table, ClientTableModel tableModel, boolean inactiveTable) {
            this.table = table;
            this.tableModel = tableModel;
            this.inactiveTable = inactiveTable;
            this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
            this.panel.setOpaque(true);

            JButton btnDetail = createTableIconButton(FontAwesome.EYE, "Ver detalle", new Color(69, 90, 100));
            JButton btnSecondary = createTableIconButton(
                    inactiveTable ? FontAwesome.UNDO : FontAwesome.PENCIL,
                    inactiveTable ? "Restaurar" : "Editar",
                    inactiveTable ? new Color(46, 125, 50) : new Color(245, 124, 0)
            );
            JButton btnDelete = createTableIconButton(FontAwesome.TRASH, inactiveTable ? "Eliminar fisico" : "Eliminar", new Color(198, 40, 40));

            btnDetail.addActionListener(e -> handleAction("detail"));
            btnSecondary.addActionListener(e -> handleAction(inactiveTable ? "restore" : "edit"));
            btnDelete.addActionListener(e -> handleAction("delete"));

            panel.add(btnDetail);
            panel.add(btnSecondary);
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
            Client client = tableModel.getClientAt(modelRow);
            fireEditingStopped();

            if ("detail".equals(action)) {
                openClientDetail(client.getIdClient());
                return;
            }

            if ("edit".equals(action)) {
                editClient(client.getIdClient());
                return;
            }

            if ("restore".equals(action) && inactiveTable) {
                Window window = javax.swing.SwingUtilities.getWindowAncestor(table);
                if (window instanceof InactiveClientsDialog) {
                    ((InactiveClientsDialog) window).restoreClient(client.getIdClient());
                }
                return;
            }

            if ("delete".equals(action) && inactiveTable) {
                Window window = javax.swing.SwingUtilities.getWindowAncestor(table);
                if (window instanceof InactiveClientsDialog) {
                    ((InactiveClientsDialog) window).deleteInactiveClient(client.getIdClient());
                }
                return;
            }

            if ("delete".equals(action)) {
                softDeleteClient(client.getIdClient());
            }
        }
    }

    private class ClientDetailDialog extends JDialog {

        ClientDetailDialog(Window owner, Client client) {
            super(owner instanceof Frame ? (Frame) owner : null, "Detalle del Cliente", true);
            buildDialog(client);
        }

        private void buildDialog(Client client) {
            DialogUtils.applyDialogTheme(this);
            setLayout(new BorderLayout(0, 0));

            JPanel headerPanel = DialogUtils.createHeader(
                    ViewIcons.build(FontAwesome.USER, 26, Color.WHITE),
                    "Detalle del Cliente",
                    "Revise la informacion general y el historial del registro.",
                    new Color(27, 94, 157)
            );

            JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
            bodyPanel.setOpaque(false);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 12, 12));
            summaryPanel.setOpaque(false);
            summaryPanel.add(DialogUtils.createInfoTile("Nombre completo", client.getFullName()));
            summaryPanel.add(DialogUtils.createInfoTile("Documento", formatDocument(client)));
            summaryPanel.add(DialogUtils.createInfoTile("Empresa", valueOrDash(client.getCompanyName())));

            JPanel detailsCard = DialogUtils.createCard("Datos del Cliente");
            JPanel detailsGrid = new JPanel(new GridLayout(0, 2, 12, 12));
            detailsGrid.setOpaque(false);
            detailsGrid.add(DialogUtils.createInfoTile("Telefono", valueOrDash(client.getPhone())));
            detailsGrid.add(DialogUtils.createInfoTile("Correo", valueOrDash(client.getEmail())));
            detailsGrid.add(DialogUtils.createInfoTile("Direccion", valueOrDash(client.getAddress())));
            detailsGrid.add(DialogUtils.createInfoTile("Ciudad", valueOrDash(client.getCity())));
            detailsGrid.add(DialogUtils.createInfoTile("Pais", valueOrDash(client.getCountry())));
            detailsGrid.add(DialogUtils.createInfoTile("Creado", formatDate(client.getCreatedAt())));
            detailsGrid.add(DialogUtils.createInfoTile("Actualizado", formatDate(client.getUpdatedAt())));
            detailsGrid.add(DialogUtils.createInfoTile("Inactivado", formatDate(client.getDeletedAt())));
            detailsCard.add(detailsGrid, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            buttonPanel.setOpaque(false);

            JButton btnClose = createToolbarButton("Cerrar", null, new Color(96, 125, 139));
            btnClose.addActionListener(e -> dispose());

            buttonPanel.add(btnClose);

            bodyPanel.add(summaryPanel, BorderLayout.NORTH);
            bodyPanel.add(detailsCard, BorderLayout.CENTER);
            bodyPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(headerPanel, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);
            pack();
            setMinimumSize(new Dimension(760, 560));
            DialogUtils.centerAndLock(this, ClientesJPanel.this);
        }
    }

    private class ClientFormDialog extends JDialog {

        private final Client editingClient;
        private boolean saved;

        private JTextField txtName;
        private JTextField txtLastNamePaternal;
        private JTextField txtLastNameMaternal;
        private JComboBox<Client.ReferenceItem> cmbDocumentType;
        private JTextField txtDocumentNumber;
        private JTextField txtCompanyName;
        private JTextField txtPhone;
        private JTextField txtEmail;
        private JTextField txtAddress;
        private JTextField txtCity;
        private JTextField txtCountry;

        ClientFormDialog(Window owner, Client client) {
            super(owner instanceof Frame ? (Frame) owner : null, true);
            this.editingClient = client;
            buildDialog();
            loadDocumentTypes();
            fillFormIfNeeded();
        }

        boolean isSaved() {
            return saved;
        }

        private void buildDialog() {
            setTitle(editingClient == null ? "Nuevo Cliente" : "Editar Cliente");
            DialogUtils.applyDialogTheme(this);
            setLayout(new BorderLayout(0, 0));

            JPanel headerPanel = DialogUtils.createHeader(
                    ViewIcons.build(editingClient == null ? FontAwesome.PLUS : FontAwesome.PENCIL, 24, Color.WHITE),
                    editingClient == null ? "Nuevo Cliente" : "Editar Cliente",
                    editingClient == null
                            ? "Complete la informacion principal para registrar un nuevo cliente."
                            : "Actualice los datos del cliente seleccionado.",
                    new Color(21, 101, 192)
            );

            txtName = DialogUtils.styleInput(new JTextField(18));
            txtLastNamePaternal = DialogUtils.styleInput(new JTextField(18));
            txtLastNameMaternal = DialogUtils.styleInput(new JTextField(18));
            cmbDocumentType = DialogUtils.styleInput(new JComboBox<>());
            txtDocumentNumber = DialogUtils.styleInput(new JTextField(18));
            txtCompanyName = DialogUtils.styleInput(new JTextField(18));
            txtPhone = DialogUtils.styleInput(new JTextField(18));
            txtEmail = DialogUtils.styleInput(new JTextField(18));
            txtAddress = DialogUtils.styleInput(new JTextField(18));
            txtCity = DialogUtils.styleInput(new JTextField(18));
            txtCountry = DialogUtils.styleInput(new JTextField(18));

            JPanel bodyPanel = new JPanel(new BorderLayout(0, 16));
            bodyPanel.setOpaque(false);
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JPanel identityCard = DialogUtils.createCard("Identificacion");
            JPanel identityGrid = new JPanel(new GridBagLayout());
            identityGrid.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            addField(identityGrid, gbc, 0, "Nombre", txtName);
            addField(identityGrid, gbc, 1, "Apellido paterno", txtLastNamePaternal);
            addField(identityGrid, gbc, 2, "Apellido materno", txtLastNameMaternal);
            addField(identityGrid, gbc, 3, "Tipo de documento", cmbDocumentType);
            addField(identityGrid, gbc, 4, "Numero de documento", txtDocumentNumber);
            addField(identityGrid, gbc, 5, "Empresa", txtCompanyName);
            identityCard.add(identityGrid, BorderLayout.CENTER);

            JPanel contactCard = DialogUtils.createCard("Contacto y Ubicacion");
            JPanel contactGrid = new JPanel(new GridBagLayout());
            contactGrid.setOpaque(false);
            GridBagConstraints contactGbc = new GridBagConstraints();
            contactGbc.insets = new Insets(6, 6, 6, 6);
            contactGbc.fill = GridBagConstraints.HORIZONTAL;
            contactGbc.anchor = GridBagConstraints.WEST;

            addField(contactGrid, contactGbc, 0, "Telefono", txtPhone);
            addField(contactGrid, contactGbc, 1, "Correo", txtEmail);
            addField(contactGrid, contactGbc, 2, "Direccion", txtAddress);
            addField(contactGrid, contactGbc, 3, "Ciudad", txtCity);
            addField(contactGrid, contactGbc, 4, "Pais", txtCountry);
            contactCard.add(contactGrid, BorderLayout.CENTER);

            JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 16, 0));
            cardsPanel.setOpaque(false);
            cardsPanel.add(identityCard);
            cardsPanel.add(contactCard);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            buttonPanel.setOpaque(false);

            JButton btnSave = createToolbarButton("Guardar", ViewIcons.build(FontAwesome.FLOPPY_O, 14, Color.WHITE), new Color(46, 125, 50));
            btnSave.addActionListener(e -> saveClient());

            JButton btnCancel = createToolbarButton("Cancelar", null, new Color(96, 125, 139));
            btnCancel.addActionListener(e -> dispose());

            buttonPanel.add(btnSave);
            buttonPanel.add(btnCancel);

            bodyPanel.add(cardsPanel, BorderLayout.CENTER);
            bodyPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(headerPanel, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);
            pack();
            setMinimumSize(new Dimension(920, 560));
            DialogUtils.centerAndLock(this, ClientesJPanel.this);
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

        private void loadDocumentTypes() {
            try {
                List<Client.ReferenceItem> items = clientController.listDocumentTypes();
                DefaultComboBoxModel<Client.ReferenceItem> model = new DefaultComboBoxModel<>();

                for (Client.ReferenceItem item : items) {
                    model.addElement(item);
                }

                cmbDocumentType.setModel(model);
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Clientes", e.getMessage());
            }
        }

        private void fillFormIfNeeded() {
            if (editingClient == null) {
                return;
            }

            txtName.setText(editingClient.getName());
            txtLastNamePaternal.setText(editingClient.getLastNamePaternal());
            txtLastNameMaternal.setText(editingClient.getLastNameMaternal());
            txtDocumentNumber.setText(editingClient.getDocumentNumber() == null ? "" : String.valueOf(editingClient.getDocumentNumber()));
            txtCompanyName.setText(editingClient.getCompanyName());
            txtPhone.setText(editingClient.getPhone());
            txtEmail.setText(editingClient.getEmail());
            txtAddress.setText(editingClient.getAddress());
            txtCity.setText(editingClient.getCity());
            txtCountry.setText(editingClient.getCountry());

            for (int index = 0; index < cmbDocumentType.getItemCount(); index++) {
                Client.ReferenceItem item = cmbDocumentType.getItemAt(index);

                if (item.getId() == editingClient.getIdDocumentType()) {
                    cmbDocumentType.setSelectedIndex(index);
                    break;
                }
            }
        }

        private void saveClient() {
            try {
                Client.ReferenceItem documentType = (Client.ReferenceItem) cmbDocumentType.getSelectedItem();

                Client client = editingClient == null ? new Client() : editingClient;
                client.setName(txtName.getText());
                client.setLastNamePaternal(txtLastNamePaternal.getText());
                client.setLastNameMaternal(txtLastNameMaternal.getText());
                client.setIdDocumentType(documentType == null ? 0 : documentType.getId());
                client.setDocumentNumber(Integer.valueOf(txtDocumentNumber.getText().trim()));
                client.setCompanyName(txtCompanyName.getText());
                client.setPhone(txtPhone.getText());
                client.setEmail(txtEmail.getText());
                client.setAddress(txtAddress.getText());
                client.setCity(txtCity.getText());
                client.setCountry(txtCountry.getText());

                if (editingClient == null) {
                    clientController.createClient(client);
                } else {
                    clientController.updateClient(client);
                }

                saved = true;
                AppMessageDialog.showInfo(this, "Clientes", "Los datos del cliente fueron guardados correctamente.");
                dispose();
            } catch (NumberFormatException e) {
                AppMessageDialog.showError(this, "Clientes", "Escriba un numero de documento valido.");
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Clientes", e.getMessage());
            }
        }
    }

    private class InactiveClientsDialog extends JDialog {

        private final ClientTableModel inactiveTableModel = new ClientTableModel();
        private final Client.Filter inactiveFilter = new Client.Filter();
        private Client.PaginatedResult inactiveResult;
        private boolean dataChanged;

        private JTextField txtInactiveSearch;
        private JComboBox<Integer> cmbInactivePageSize;
        private JLabel lblInactivePageInfo;
        private JButton btnInactivePrevious;
        private JButton btnInactiveNext;
        private JTable tblInactiveClients;

        InactiveClientsDialog(Window owner) {
            super(owner instanceof Frame ? (Frame) owner : null, "Clientes Inactivos", true);
            buildDialog();
            loadInactiveClients();
        }

        boolean isDataChanged() {
            return dataChanged;
        }

        private void buildDialog() {
            setLayout(new BorderLayout(0, 14));
            setPreferredSize(new Dimension(980, 540));

            JPanel topCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            topCard.setBackground(Color.WHITE);
            topCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(218, 225, 232)),
                    BorderFactory.createEmptyBorder(4, 6, 4, 6)
            ));

            txtInactiveSearch = new JTextField(24);
            txtInactiveSearch.setPreferredSize(new Dimension(280, 36));
            installSearchDebounce(txtInactiveSearch, this::searchInactiveFromFirstPage);

            JButton btnClear = createToolbarButton(
                    "Limpiar",
                    ViewIcons.build(FontAwesome.ERASER, 14, Color.WHITE),
                    new Color(96, 125, 139)
            );
            btnClear.addActionListener(e -> clearInactiveSearch());

            topCard.add(new JLabel("Buscar:"));
            topCard.add(txtInactiveSearch);
            topCard.add(btnClear);

            JPanel tableCard = new JPanel(new BorderLayout());
            tableCard.setBackground(Color.WHITE);
            tableCard.setBorder(BorderFactory.createLineBorder(new Color(214, 223, 233), 1));

            tblInactiveClients = new JTable(inactiveTableModel);
            tblInactiveClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblInactiveClients.setRowHeight(38);
            tblInactiveClients.getTableHeader().setReorderingAllowed(false);
            tblInactiveClients.getTableHeader().setBackground(new Color(246, 248, 251));
            tblInactiveClients.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            tblInactiveClients.setShowHorizontalLines(true);
            tblInactiveClients.setGridColor(new Color(232, 236, 240));

            configureClientTable(tblInactiveClients, inactiveTableModel, true);

            JScrollPane scrollPane = new JScrollPane(tblInactiveClients);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            tableCard.add(scrollPane, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setOpaque(false);

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftPanel.setOpaque(false);

            lblInactivePageInfo = new JLabel("Pagina 1 de 1 | Total: 0");
            cmbInactivePageSize = new JComboBox<>(new Integer[]{10, 20, 50});
            cmbInactivePageSize.setPreferredSize(new Dimension(76, 30));
            cmbInactivePageSize.addActionListener(e -> {
                inactiveFilter.setPageSize((Integer) cmbInactivePageSize.getSelectedItem());
                inactiveFilter.setPage(1);
                loadInactiveClients();
            });

            leftPanel.add(lblInactivePageInfo);
            leftPanel.add(new JLabel("Mostrar:"));
            leftPanel.add(cmbInactivePageSize);

            JPanel pagingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            pagingPanel.setOpaque(false);

            btnInactivePrevious = createToolbarButton("Anterior", null, new Color(96, 125, 139));
            btnInactivePrevious.addActionListener(e -> changeInactivePage(-1));

            btnInactiveNext = createToolbarButton("Siguiente", null, new Color(96, 125, 139));
            btnInactiveNext.addActionListener(e -> changeInactivePage(1));

            pagingPanel.add(btnInactivePrevious);
            pagingPanel.add(btnInactiveNext);

            bottomPanel.add(leftPanel, BorderLayout.WEST);
            bottomPanel.add(pagingPanel, BorderLayout.EAST);

            add(topCard, BorderLayout.NORTH);
            add(tableCard, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            pack();
            DialogUtils.centerAndLock(this, ClientesJPanel.this);
        }

        private void searchInactiveFromFirstPage() {
            inactiveFilter.setSearchText(txtInactiveSearch.getText());
            inactiveFilter.setPage(1);
            loadInactiveClients();
        }

        private void clearInactiveSearch() {
            boolean changed = txtInactiveSearch != null && !txtInactiveSearch.getText().trim().isEmpty();
            inactiveFilter.setPage(1);
            txtInactiveSearch.setText("");

            if (!changed) {
                loadInactiveClients();
            }
        }

        private void changeInactivePage(int delta) {
            if (inactiveResult == null) {
                return;
            }

            int nextPage = inactiveFilter.getPage() + delta;

            if (nextPage <= 0 || nextPage > inactiveResult.getTotalPages()) {
                return;
            }

            inactiveFilter.setPage(nextPage);
            loadInactiveClients();
        }

        private void loadInactiveClients() {
            inactiveFilter.setSearchText(txtInactiveSearch == null ? null : txtInactiveSearch.getText());
            inactiveFilter.setPageSize(cmbInactivePageSize == null ? inactiveFilter.getPageSize() : (Integer) cmbInactivePageSize.getSelectedItem());

            try {
                inactiveResult = clientController.searchInactiveClients(inactiveFilter);
                inactiveTableModel.setItems(inactiveResult.getItems());
                lblInactivePageInfo.setText(
                        "Pagina " + inactiveResult.getPage()
                        + " de " + inactiveResult.getTotalPages()
                        + " | Total: " + inactiveResult.getTotalRecords()
                );

                btnInactivePrevious.setEnabled(inactiveResult.getPage() > 1);
                btnInactiveNext.setEnabled(inactiveResult.getPage() < inactiveResult.getTotalPages());
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Clientes", e.getMessage());
            }
        }

        private void restoreClient(int idClient) {
            if (idClient <= 0) {
                AppMessageDialog.showError(this, "Clientes", "Seleccione un cliente inactivo para restaurar.");
                return;
            }

            try {
                clientController.restoreClient(idClient);
                dataChanged = true;
                AppMessageDialog.showInfo(this, "Clientes", "El cliente fue restaurado correctamente.");
                loadInactiveClients();
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Clientes", e.getMessage());
            }
        }

        private void deleteInactiveClient(int idClient) {
            if (idClient <= 0) {
                AppMessageDialog.showError(this, "Clientes", "Seleccione un cliente inactivo para eliminar.");
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Se eliminara fisicamente el cliente inactivo seleccionado. Esta accion no se puede deshacer. Desea continuar?",
                    "Confirmar eliminacion fisica",
                    JOptionPane.YES_NO_OPTION
            );

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                clientController.deleteClient(idClient);
                dataChanged = true;
                AppMessageDialog.showInfo(this, "Clientes", "El cliente inactivo fue eliminado fisicamente.");
                loadInactiveClients();
            } catch (Exception e) {
                AppMessageDialog.showError(this, "Clientes", e.getMessage());
            }
        }
    }
}
