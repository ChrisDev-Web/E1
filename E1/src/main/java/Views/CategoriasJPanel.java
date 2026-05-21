package Views;

import Config.ImageStorage;
import Controllers.CategoryController;
import Models.Category;
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
import java.io.File;
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

public class CategoriasJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "categorias";
    private static final String VIEW_TITLE = "Categorias";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.TAGS, 28);

    private final CategoryController controller = new CategoryController();
    private final CategoryTableModel categoryTableModel = new CategoryTableModel();
    private final List<Category> currentCategories = new ArrayList<>();

    private JTextField txtSearch;
    private JTable table;
    private javax.swing.JComboBox<Integer> cmbPageSize;
    private JLabel lblPageInfo;
    private JButton btnPrevious;
    private JButton btnNext;
    private int currentPage = 1;

    public CategoriasJPanel() {
        initPanel();
        loadCategories("");
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
        loadCategories(txtSearch == null ? "" : txtSearch.getText());
    }

    private JPanel createHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        wrapper.add(
                ViewStyle.createTitlePanel(
                        VIEW_ICON,
                        "Gestion de Categorias",
                        "Organice familias de productos con descripciones e imagen de apoyo."
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
        btnRefresh.addActionListener(e -> loadCategories(txtSearch.getText()));

        JButton btnNew = ViewStyle.createToolbarButton(
                "Nueva Categoria",
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
        table = new JTable(categoryTableModel);
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
        lblPageInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cmbPageSize = new javax.swing.JComboBox<>(new Integer[]{10, 20, 50});
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
        int actionsColumn = categoryTableModel.getActionColumnIndex();
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(0).setMaxWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(360);
        table.getColumnModel().getColumn(actionsColumn).setPreferredWidth(140);
        table.getColumnModel().getColumn(actionsColumn).setMinWidth(140);
        table.getColumnModel().getColumn(actionsColumn).setMaxWidth(140);
        table.getColumnModel().getColumn(actionsColumn).setCellRenderer(new CategoryActionCellRenderer());
        table.getColumnModel().getColumn(actionsColumn).setCellEditor(new CategoryActionCellEditor());
    }

    private void installSearchDebounce() {
        Timer timer = new Timer(280, e -> {
            currentPage = 1;
            loadCategories(txtSearch.getText());
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
            loadCategories(txtSearch.getText());
        });
    }

    private void loadCategories(String query) {
        try {
            currentCategories.clear();
            currentCategories.addAll(controller.searchCategories(query == null ? "" : query.trim()));
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
        int toIndex = Math.min(fromIndex + pageSize, currentCategories.size());

        if (fromIndex > toIndex) {
            fromIndex = 0;
            toIndex = Math.min(pageSize, currentCategories.size());
        }

        categoryTableModel.setItems(currentCategories.subList(fromIndex, toIndex));
        lblPageInfo.setText("Pagina " + currentPage + " de " + totalPages + " | Total: " + currentCategories.size());
        btnPrevious.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private int getPageSize() {
        Integer selected = (Integer) cmbPageSize.getSelectedItem();
        return selected == null || selected <= 0 ? 10 : selected;
    }

    private int getTotalPages() {
        if (currentCategories.isEmpty()) {
            return 1;
        }

        return (int) Math.ceil((double) currentCategories.size() / getPageSize());
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
        loadCategories("");
    }

    private void openDetail(Category category) {
        JDialog dialog = new JDialog(resolveWindow(), "Detalle de Categoria", Dialog.ModalityType.APPLICATION_MODAL);
        DialogUtils.applyDialogTheme(dialog);
        dialog.setLayout(new BorderLayout(0, 14));
        dialog.setPreferredSize(new Dimension(620, 340));

        dialog.add(DialogUtils.createHeader(
                ViewIcons.build(FontAwesome.TAGS, 24, Color.WHITE),
                "Detalle de Categoria",
                "Informacion general de la categoria seleccionada.",
                new Color(27, 94, 157)
        ), BorderLayout.NORTH);

        JPanel card = DialogUtils.createCard("Datos de la categoria");
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setOpaque(false);
        grid.add(DialogUtils.createInfoTile("ID", String.valueOf(category.getIdCategory())));
        grid.add(DialogUtils.createInfoTile("Nombre", category.getCategoryName()));
        grid.add(DialogUtils.createInfoTile("Descripcion", defaultText(category.getDescription())));
        grid.add(DialogUtils.createInfoTile("Imagen", defaultText(category.getImagePath())));
        card.add(grid, BorderLayout.CENTER);

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

    private void openForm(Category editingCategory) {
        boolean editing = editingCategory != null;
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                editing ? "Editar Categoria" : "Nueva Categoria",
                true
        );
        DialogUtils.applyDialogTheme(dialog);
        dialog.setLayout(new BorderLayout(0, 14));
        dialog.setPreferredSize(new Dimension(680, 420));

        dialog.add(DialogUtils.createHeader(
                ViewIcons.build(editing ? FontAwesome.PENCIL : FontAwesome.PLUS, 24, Color.WHITE),
                editing ? "Editar Categoria" : "Nueva Categoria",
                editing ? "Actualice nombre, descripcion e imagen." : "Cree una nueva categoria para sus productos.",
                new Color(27, 94, 157)
        ), BorderLayout.NORTH);

        JTextField txtName = DialogUtils.styleInput(new JTextField(editing ? editingCategory.getCategoryName() : ""));
        JTextField txtDescription = DialogUtils.styleInput(new JTextField(editing ? editingCategory.getDescription() : ""));
        JTextField txtImage = DialogUtils.styleInput(new JTextField(editing ? editingCategory.getImagePath() : ""));
        txtImage.setEditable(false);

        final File[] selectedImage = new File[1];
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

        JPanel formCard = DialogUtils.createCard("Datos de la categoria");
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);

        addField(fieldsPanel, 0, "Nombre", txtName);
        addField(fieldsPanel, 1, "Descripcion", txtDescription);
        addField(fieldsPanel, 2, "Imagen", txtImage);

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 1;
        buttonConstraints.gridy = 3;
        buttonConstraints.anchor = GridBagConstraints.WEST;
        buttonConstraints.insets = new Insets(0, 0, 0, 0);
        fieldsPanel.add(btnImage, buttonConstraints);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        JButton btnCancel = ViewStyle.createToolbarButton("Cerrar", null, new Color(96, 125, 139));
        JButton btnSave = ViewStyle.createToolbarButton(
                "Guardar",
                ViewIcons.build(FontAwesome.FLOPPY_O, 14, Color.WHITE),
                new Color(46, 125, 50)
        );

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> saveCategory(dialog, editingCategory, txtName, txtDescription, txtImage, selectedImage));

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

    private void saveCategory(
            JDialog dialog,
            Category editingCategory,
            JTextField txtName,
            JTextField txtDescription,
            JTextField txtImage,
            File[] selectedImage
    ) {
        try {
            Category category = editingCategory == null ? new Category() : editingCategory;
            category.setCategoryName(txtName.getText());
            category.setDescription(txtDescription.getText());

            if (selectedImage[0] != null) {
                category.setImagePath(ImageStorage.saveImage(selectedImage[0], "categories", txtName.getText()));
            } else if (editingCategory == null) {
                category.setImagePath("");
            } else {
                category.setImagePath(txtImage.getText());
            }

            if (editingCategory == null) {
                controller.createCategory(category);
                AppMessageDialog.showInfo(dialog, VIEW_TITLE, "Categoria creada correctamente.");
            } else {
                controller.updateCategory(category);
                AppMessageDialog.showInfo(dialog, VIEW_TITLE, "Categoria actualizada correctamente.");
            }

            dialog.dispose();
            currentPage = 1;
            loadCategories(txtSearch.getText());
        } catch (Exception ex) {
            AppMessageDialog.showError(dialog, VIEW_TITLE, ex.getMessage());
        }
    }

    private void deleteCategory(Category category) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Eliminar la categoria seleccionada?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteCategory(category.getIdCategory());
            AppMessageDialog.showInfo(this, VIEW_TITLE, "Categoria eliminada correctamente.");
            currentPage = 1;
            loadCategories(txtSearch.getText());
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
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

    private class CategoryTableModel extends AbstractTableModel {

        private static final int ACTIONS_COLUMN_INDEX = 3;
        private final String[] columns = {"ID", "Nombre", "Descripcion", "Acciones"};
        private final List<Category> items = new ArrayList<>();

        void setItems(List<Category> categories) {
            items.clear();

            if (categories != null) {
                items.addAll(categories);
            }

            fireTableDataChanged();
        }

        Category getCategoryAt(int rowIndex) {
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
            Category category = items.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return category.getIdCategory();
                case 1:
                    return category.getCategoryName();
                case 2:
                    return defaultText(category.getDescription());
                case ACTIONS_COLUMN_INDEX:
                    return category;
                default:
                    return "";
            }
        }
    }

    private class CategoryActionCellRenderer extends JPanel implements TableCellRenderer {

        private final JButton btnDetail;
        private final JButton btnEdit;
        private final JButton btnDelete;

        CategoryActionCellRenderer() {
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

    private class CategoryActionCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private int editingRow = -1;

        CategoryActionCellEditor() {
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
            Category category = categoryTableModel.getCategoryAt(modelRow);
            fireEditingStopped();

            if ("detail".equals(action)) {
                openDetail(category);
            } else if ("edit".equals(action)) {
                openForm(category);
            } else {
                deleteCategory(category);
            }
        }
    }
}
