package Views;

import Controllers.UserController;
import Models.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jiconfont.icons.font_awesome.FontAwesome;

// Swing + MVC: vista CRUD para administrar usuarios del sistema.
public class UsuariosJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "usuarios";
    private static final String VIEW_TITLE = "Usuarios";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.USER, 28);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final UserController userController = new UserController();
    private final UserTableModel userTableModel = new UserTableModel();
    private final List<User> allUsers = new ArrayList<>();

    private JTextField txtSearch;
    private JComboBox<String> cmbStatusFilter;
    private JComboBox<Integer> cmbPageSize;
    private JLabel lblPageInfo;
    private JButton btnPrevious;
    private JButton btnNext;
    private JTable tblUsers;
    private int currentPage = 1;

    public UsuariosJPanel() {
        initPanel();
        loadUsers();
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
        loadUsers();
    }

    private JPanel createHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout(12, 0));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Gestion de Usuarios");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33));

        JLabel lblSubtitle = new JLabel("Cree, edite, filtre y desactive cuentas de acceso sin tocar IDs manualmente.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(90, 90, 90));

        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle, BorderLayout.NORTH);
        textPanel.add(lblSubtitle, BorderLayout.CENTER);

        titlePanel.add(new JLabel(VIEW_ICON), BorderLayout.WEST);
        titlePanel.add(textPanel, BorderLayout.CENTER);

        JPanel toolbarCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarCard.setBackground(Color.WHITE);
        toolbarCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 225, 232)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));

        txtSearch = new JTextField(26);
        txtSearch.setPreferredSize(new Dimension(280, 36));
        installSearchDebounce();

        cmbStatusFilter = new JComboBox<>(new String[]{"Todos", "Activos", "Inactivos"});
        cmbStatusFilter.setPreferredSize(new Dimension(130, 36));
        cmbStatusFilter.addActionListener(e -> {
            currentPage = 1;
            loadUsers();
        });

        JButton btnClear = createToolbarButton(
                "Limpiar",
                ViewIcons.build(FontAwesome.ERASER, 14, Color.WHITE),
                new Color(96, 125, 139)
        );
        btnClear.addActionListener(e -> clearFilters());

        JButton btnNew = createToolbarButton(
                "Crear",
                ViewIcons.build(FontAwesome.PLUS, 14, Color.WHITE),
                new Color(46, 125, 50)
        );
        btnNew.addActionListener(e -> openUserForm(null));

        toolbarCard.add(new JLabel("Buscar:"));
        toolbarCard.add(txtSearch);
        toolbarCard.add(new JLabel("Estado:"));
        toolbarCard.add(cmbStatusFilter);
        toolbarCard.add(btnClear);
        toolbarCard.add(btnNew);

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(toolbarCard, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createCenterPanel() {
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(new Color(214, 223, 233), 1));

        tblUsers = new JTable(userTableModel);
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUsers.setRowHeight(40);
        tblUsers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblUsers.getTableHeader().setReorderingAllowed(false);
        tblUsers.getTableHeader().setBackground(new Color(246, 248, 251));
        tblUsers.getTableHeader().setForeground(new Color(33, 33, 33));
        tblUsers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblUsers.setShowHorizontalLines(true);
        tblUsers.setShowVerticalLines(false);
        tblUsers.setGridColor(new Color(232, 236, 240));
        tblUsers.setSelectionBackground(new Color(229, 241, 255));
        tblUsers.setSelectionForeground(new Color(33, 33, 33));

        configureTable();

        JScrollPane scrollPane = new JScrollPane(tblUsers);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);
        return tableCard;
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
            currentPage = 1;
            renderCurrentPage();
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

    private void configureTable() {
        int actionsColumn = userTableModel.getActionColumnIndex();
        tblUsers.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblUsers.getColumnModel().getColumn(0).setMaxWidth(90);
        tblUsers.getColumnModel().getColumn(1).setPreferredWidth(230);
        tblUsers.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblUsers.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblUsers.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblUsers.getColumnModel().getColumn(actionsColumn).setPreferredWidth(170);
        tblUsers.getColumnModel().getColumn(actionsColumn).setMinWidth(170);
        tblUsers.getColumnModel().getColumn(actionsColumn).setMaxWidth(170);
        tblUsers.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());
        tblUsers.getColumnModel().getColumn(actionsColumn).setCellRenderer(new UserActionCellRenderer());
        tblUsers.getColumnModel().getColumn(actionsColumn).setCellEditor(new UserActionCellEditor());
    }

    private JButton createToolbarButton(String text, Icon icon, Color color) {
        JButton button = new JButton(text, icon);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

    private void installSearchDebounce() {
        Timer timer = new Timer(280, e -> {
            currentPage = 1;
            loadUsers();
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
            loadUsers();
        });
    }

    private void loadUsers() {
        try {
            allUsers.clear();
            allUsers.addAll(userController.searchUsers(txtSearch.getText(), getSelectedStatusFilter()));
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
        int toIndex = Math.min(fromIndex + pageSize, allUsers.size());

        if (fromIndex > toIndex) {
            fromIndex = 0;
            toIndex = Math.min(pageSize, allUsers.size());
        }

        userTableModel.setItems(allUsers.subList(fromIndex, toIndex));
        lblPageInfo.setText("Pagina " + currentPage + " de " + totalPages + " | Total: " + allUsers.size());
        btnPrevious.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private int getPageSize() {
        Integer selected = (Integer) cmbPageSize.getSelectedItem();
        return selected == null || selected <= 0 ? 10 : selected;
    }

    private int getTotalPages() {
        if (allUsers.isEmpty()) {
            return 1;
        }

        return (int) Math.ceil((double) allUsers.size() / getPageSize());
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
        cmbStatusFilter.setSelectedIndex(0);
        currentPage = 1;
        loadUsers();
    }

    private String getSelectedStatusFilter() {
        String selected = String.valueOf(cmbStatusFilter.getSelectedItem());

        if ("Activos".equals(selected)) {
            return "ACTIVE";
        }

        if ("Inactivos".equals(selected)) {
            return "INACTIVE";
        }

        return "ALL";
    }

    private void openUserDetail(User user) {
        JDialog dialog = new JDialog(resolveWindow(), "Detalle de Usuario", Dialog.ModalityType.APPLICATION_MODAL);
        DialogUtils.applyDialogTheme(dialog);
        dialog.setLayout(new BorderLayout(0, 14));
        dialog.setPreferredSize(new Dimension(620, 360));

        dialog.add(DialogUtils.createHeader(
                ViewIcons.build(FontAwesome.USER, 24, Color.WHITE),
                "Detalle de Usuario",
                "Informacion de la cuenta seleccionada.",
                new Color(27, 94, 157)
        ), BorderLayout.NORTH);

        JPanel card = DialogUtils.createCard("Datos del usuario");
        JPanel grid = new JPanel(new GridLayout(3, 2, 12, 12));
        grid.setOpaque(false);
        grid.add(DialogUtils.createInfoTile("ID", String.valueOf(user.getIdUser())));
        grid.add(DialogUtils.createInfoTile("Usuario", user.getUserName()));
        grid.add(DialogUtils.createInfoTile("Estado", formatStatus(user.getStatus())));
        grid.add(DialogUtils.createInfoTile("Creado", formatDate(user.getCreatedAt())));
        grid.add(DialogUtils.createInfoTile("Actualizado", formatDate(user.getUpdatedAt())));
        grid.add(DialogUtils.createInfoTile("Clave", "Protegida con hash"));
        card.add(grid, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        JButton btnClose = createToolbarButton("Cerrar", null, new Color(96, 125, 139));
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

    private void openUserForm(User editingUser) {
        boolean editing = editingUser != null;
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                editing ? "Editar Usuario" : "Nuevo Usuario",
                true
        );
        DialogUtils.applyDialogTheme(dialog);
        dialog.setLayout(new BorderLayout(0, 14));
        dialog.setPreferredSize(new Dimension(620, 430));

        dialog.add(DialogUtils.createHeader(
                ViewIcons.build(editing ? FontAwesome.PENCIL : FontAwesome.PLUS, 24, Color.WHITE),
                editing ? "Editar Usuario" : "Nuevo Usuario",
                editing ? "Actualice nombre, estado o cambie la contrasena." : "Cree una cuenta de acceso para el sistema.",
                new Color(27, 94, 157)
        ), BorderLayout.NORTH);

        JPanel formCard = DialogUtils.createCard("Credenciales");
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);

        JTextField txtUserName = DialogUtils.styleInput(new JTextField(editing ? editingUser.getUserName() : ""));
        JPasswordField txtPassword = DialogUtils.styleInput(new JPasswordField());
        JPasswordField txtConfirmPassword = DialogUtils.styleInput(new JPasswordField());
        JComboBox<String> cmbStatus = DialogUtils.styleInput(new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"}));

        if (editing) {
            cmbStatus.setSelectedItem(editingUser.getStatus() == null ? "ACTIVE" : editingUser.getStatus());
        }

        addField(fieldsPanel, 0, "Usuario", txtUserName);
        addField(fieldsPanel, 1, editing ? "Nueva contrasena" : "Contrasena", txtPassword);
        addField(fieldsPanel, 2, "Confirmar contrasena", txtConfirmPassword);
        addField(fieldsPanel, 3, "Estado", cmbStatus);

        if (editing) {
            JLabel hint = new JLabel("Deje las contrasenas vacias si no desea cambiarlas.");
            hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            hint.setForeground(new Color(98, 110, 123));
            GridBagConstraints hintConstraints = new GridBagConstraints();
            hintConstraints.gridx = 1;
            hintConstraints.gridy = 4;
            hintConstraints.weightx = 1;
            hintConstraints.fill = GridBagConstraints.HORIZONTAL;
            hintConstraints.insets = new Insets(0, 0, 0, 0);
            fieldsPanel.add(hint, hintConstraints);
        }

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        JButton btnCancel = createToolbarButton("Cancelar", null, new Color(96, 125, 139));
        JButton btnSave = createToolbarButton(
                "Guardar",
                ViewIcons.build(FontAwesome.FLOPPY_O, 14, Color.WHITE),
                new Color(46, 125, 50)
        );

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> saveUser(dialog, editingUser, txtUserName, txtPassword, txtConfirmPassword, cmbStatus));

        footer.add(btnCancel);
        footer.add(btnSave);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
        content.add(formCard, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);

        dialog.add(content, BorderLayout.CENTER);
        dialog.pack();
        DialogUtils.centerAndLock(dialog, this);
        dialog.setVisible(true);
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

    private void saveUser(
            JDialog dialog,
            User editingUser,
            JTextField txtUserName,
            JPasswordField txtPassword,
            JPasswordField txtConfirmPassword,
            JComboBox<String> cmbStatus
    ) {
        try {
            if (editingUser == null) {
                userController.createUser(
                        txtUserName.getText(),
                        txtPassword.getPassword(),
                        txtConfirmPassword.getPassword(),
                        String.valueOf(cmbStatus.getSelectedItem())
                );
                AppMessageDialog.showInfo(dialog, VIEW_TITLE, "Usuario creado correctamente.");
            } else {
                userController.updateUser(
                        editingUser.getIdUser(),
                        txtUserName.getText(),
                        txtPassword.getPassword(),
                        txtConfirmPassword.getPassword(),
                        String.valueOf(cmbStatus.getSelectedItem())
                );
                AppMessageDialog.showInfo(dialog, VIEW_TITLE, "Usuario actualizado correctamente.");
            }

            dialog.dispose();
            currentPage = 1;
            loadUsers();
        } catch (Exception e) {
            AppMessageDialog.showError(dialog, VIEW_TITLE, e.getMessage());
        } finally {
            txtPassword.setText("");
            txtConfirmPassword.setText("");
        }
    }

    private void deactivateUser(User user) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "El usuario seleccionado pasara a estado INACTIVE. Desea continuar?",
                "Confirmar desactivacion",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userController.deleteUser(user.getIdUser());
            AppMessageDialog.showInfo(this, VIEW_TITLE, "Usuario desactivado correctamente.");
            currentPage = 1;
            loadUsers();
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private Window resolveWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "-" : DATE_TIME_FORMATTER.format(dateTime);
    }

    private String formatStatus(String status) {
        return "INACTIVE".equalsIgnoreCase(status) ? "Inactivo" : "Activo";
    }

    private class UserTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 5;
        private final String[] columns = {"ID", "Usuario", "Estado", "Creado", "Actualizado", "Acciones"};
        private final List<User> items = new ArrayList<>();

        void setItems(List<User> users) {
            items.clear();

            if (users != null) {
                items.addAll(users);
            }

            fireTableDataChanged();
        }

        User getUserAt(int rowIndex) {
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
            User user = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return user.getIdUser();
                case 1:
                    return user.getUserName();
                case 2:
                    return formatStatus(user.getStatus());
                case 3:
                    return formatDate(user.getCreatedAt());
                case 4:
                    return formatDate(user.getUpdatedAt());
                case ACTIONS_COLUMN_INDEX:
                    return user;
                default:
                    return "";
            }
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground("Inactivo".equals(value) ? new Color(198, 40, 40) : new Color(46, 125, 50));
            return label;
        }
    }

    private class UserActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnDetail;
        private final JButton btnEdit;
        private final JButton btnDelete;

        UserActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            setOpaque(true);
            btnDetail = createTableIconButton(FontAwesome.EYE, "Ver detalle", new Color(69, 90, 100));
            btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar", new Color(245, 124, 0));
            btnDelete = createTableIconButton(FontAwesome.TRASH, "Desactivar", new Color(198, 40, 40));
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

    private class UserActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private int editingRow = -1;

        UserActionCellEditor() {
            panel.setOpaque(true);
            JButton btnDetail = createTableIconButton(FontAwesome.EYE, "Ver detalle", new Color(69, 90, 100));
            JButton btnEdit = createTableIconButton(FontAwesome.PENCIL, "Editar", new Color(245, 124, 0));
            JButton btnDelete = createTableIconButton(FontAwesome.TRASH, "Desactivar", new Color(198, 40, 40));

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
            int modelRow = tblUsers.convertRowIndexToModel(editingRow);
            User user = userTableModel.getUserAt(modelRow);
            fireEditingStopped();

            if ("detail".equals(action)) {
                openUserDetail(user);
            } else if ("edit".equals(action)) {
                openUserForm(user);
            } else {
                deactivateUser(user);
            }
        }
    }
}
