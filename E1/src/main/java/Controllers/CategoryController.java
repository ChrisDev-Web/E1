package Controllers;

import DAO.CategoryDAO;
import Models.Category;
import Views.CategoriasJPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CategoryController implements ActionListener {
    
    private final CategoriasJPanel vista;
    private final CategoryDAO dao;
    private DefaultTableModel modeloTabla;

    public CategoryController(CategoriasJPanel vista) {
        this.vista = vista;
        this.dao = new CategoryDAO();
        
        // Inicializar los escuchadores de los botones que creamos en el JPanel
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnModificar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        
        // Listar las categorías en la tabla apenas se cargue la sección
        listarCategorias();
    }

    // Método para rellenar la JTable con los datos de MySQL
    public void listarCategorias() {
        List<Category> lista = dao.listar();
        modeloTabla = (DefaultTableModel) vista.tblCategorias.getModel();
        modeloTabla.setRowCount(0); // Limpiar filas antiguas antes de rellenar
        
        Object[] objeto = new Object[3];
        for (Category cat : lista) {
            objeto[0] = cat.getIdCategory();
            objeto[1] = cat.getCategoryName();
            objeto[2] = cat.getDescription();
            modeloTabla.addRow(objeto);
        }
        vista.tblCategorias.setModel(modeloTabla);
    }

    // Capturar las acciones de los clics de la interfaz
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardar) {
            registrar();
        }
        if (e.getSource() == vista.btnModificar) {
            modificar();
        }
        if (e.getSource() == vista.btnEliminar) {
            eliminar();
        }
    }

    private void registrar() {
        String nombre = vista.txtNombre.getText().trim();
        String descripcion = vista.txtDescripcion.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El nombre de la categoría es obligatorio.");
            return;
        }
        
        Category cat = new Category();
        cat.setCategoryName(nombre);
        cat.setDescription(descripcion);
        cat.setImagePath(""); // Se puede dejar vacío por defecto
        
        if (dao.registrar(cat)) {
            JOptionPane.showMessageDialog(vista, "Categoría registrada con éxito.");
            limpiarCampos();
            listarCategorias();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al registrar la categoría.");
        }
    }

    private void modificar() {
        int fila = vista.tblCategorias.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar una fila de la tabla para modificar.");
            return;
        }
        
        int id = Integer.parseInt(vista.tblCategorias.getValueAt(fila, 0).toString());
        String nombre = vista.txtNombre.getText().trim();
        String descripcion = vista.txtDescripcion.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El nombre de la categoría no puede estar vacío.");
            return;
        }
        
        Category cat = new Category(id, nombre, descripcion, "");
        
        if (dao.modificar(cat)) {
            JOptionPane.showMessageDialog(vista, "Categoría modificada con éxito.");
            limpiarCampos();
            listarCategorias();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al modificar la categoría.");
        }
    }

    private void eliminar() {
        int fila = vista.tblCategorias.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar una fila de la tabla para eliminar.");
            return;
        }
        
        int id = Integer.parseInt(vista.tblCategorias.getValueAt(fila, 0).toString());
        int confirmar = JOptionPane.showConfirmDialog(vista, "¿Está seguro de eliminar esta categoría?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirmar == JOptionPane.YES_OPTION) {
            if (dao.eliminar(id)) {
                JOptionPane.showMessageDialog(vista, "Categoría eliminada.");
                limpiarCampos();
                listarCategorias();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo eliminar (puede que esté asignada a un producto).");
            }
        }
    }

    private void limpiarCampos() {
        vista.txtNombre.setText("");
        vista.txtDescripcion.setText("");
        vista.tblCategorias.clearSelection();
    }
}