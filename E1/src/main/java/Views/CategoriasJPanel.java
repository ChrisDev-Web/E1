package Views;

import Controllers.CategoryController;
import Models.Category;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import jiconfont.icons.font_awesome.FontAwesome;

public class CategoriasJPanel extends JPanel implements IViewPanel {

    private static final String VIEW_KEY = "categorias";
    private static final String VIEW_TITLE = "Categorias";
    private static final Icon VIEW_ICON = ViewIcons.build(FontAwesome.TAGS, 28);

    private final CategoryController controller = new CategoryController();
    private DefaultTableModel model;
    private JTable table;

    public CategoriasJPanel() {
        initPanel();
        loadCategories();
    }

    @Override
    public void initPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(Color.WHITE);

        JButton btnNew = new JButton("Nueva categoria");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");

        toolbar.add(btnNew);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);
        add(toolbar, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripcion"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnNew.addActionListener(e -> openForm(null));
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadCategories());
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

    private void loadCategories() {
        try {
            model.setRowCount(0);
            List<Category> categories = controller.listCategories();

            for (Category category : categories) {
                model.addRow(new Object[]{
                    category.getIdCategory(),
                    category.getCategoryName(),
                    category.getDescription()
                });
            }
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();

        if (row == -1) {
            AppMessageDialog.showError(this, VIEW_TITLE, "Seleccione una categoria.");
            return;
        }

        Category category = new Category();
        category.setIdCategory((int) table.getValueAt(row, 0));
        category.setCategoryName(String.valueOf(table.getValueAt(row, 1)));
        category.setDescription(String.valueOf(table.getValueAt(row, 2)));
        openForm(category);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();

        if (row == -1) {
            AppMessageDialog.showError(this, VIEW_TITLE, "Seleccione una categoria.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Eliminar la categoria seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteCategory((int) table.getValueAt(row, 0));
            loadCategories();
        } catch (Exception e) {
            AppMessageDialog.showError(this, VIEW_TITLE, e.getMessage());
        }
    }

    private void openForm(Category editingCategory) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editingCategory == null ? "Nueva Categoria" : "Editar Categoria", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(420, 220);
        dialog.setLocationRelativeTo(this);

        JTextField txtName = new JTextField(editingCategory == null ? "" : editingCategory.getCategoryName());
        JTextField txtDescription = new JTextField(editingCategory == null ? "" : editingCategory.getDescription());

        dialog.add(new JLabel(" Nombre:"));
        dialog.add(txtName);
        dialog.add(new JLabel(" Descripcion:"));
        dialog.add(txtDescription);

        JButton btnSave = new JButton("Guardar");
        btnSave.addActionListener(e -> {
            try {
                Category category = editingCategory == null ? new Category() : editingCategory;
                category.setCategoryName(txtName.getText());
                category.setDescription(txtDescription.getText());
                category.setImagePath("");

                if (editingCategory == null) {
                    controller.createCategory(category);
                } else {
                    controller.updateCategory(category);
                }

                dialog.dispose();
                loadCategories();
            } catch (Exception ex) {
                AppMessageDialog.showError(dialog, VIEW_TITLE, ex.getMessage());
            }
        });

        JButton btnCancel = new JButton("Cerrar");
        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnCancel);
        dialog.add(btnSave);
        dialog.setVisible(true);
    }
}
