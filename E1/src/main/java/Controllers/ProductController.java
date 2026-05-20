package Controllers;

import DAO.CategoryDAO;
import DAO.ProductDAO;
import Models.Category;
import Models.Product;
import Views.ProductosJPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ProductController implements ActionListener {

    private final ProductosJPanel vista;
    private final ProductDAO productDao;
    private final CategoryDAO categoryDao;
    private DefaultTableModel modeloTabla;
    
    // Mapa útil para asociar el nombre de la categoría en el ComboBox con su ID de BD
    private final HashMap<String, Integer> mapCategorias = new HashMap<>();

    public ProductController(ProductosJPanel vista) {
        this.vista = vista;
        this.productDao = new ProductDAO();
        this.categoryDao = new CategoryDAO();

        // Escuchar botones
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnModificar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);

        // Cargar datos iniciales
        cargarComboCategorias();
        listarProductos();
    }

    private void cargarComboCategorias() {
        vista.cbCategorias.removeAllItems();
        List<Category> lista = categoryDao.listar();
        for (Category cat : lista) {
            vista.cbCategorias.addItem(cat.getCategoryName());
            mapCategorias.put(cat.getCategoryName(), cat.getIdCategory());
        }
    }

    public void listarProductos() {
        List<Product> lista = productDao.listar();
        List<Category> categorias = categoryDao.listar();
        
        // Mapeo rápido para mostrar el nombre de la categoría en la JTable en vez de ver solo el ID numérico
        HashMap<Integer, String> nombresCat = new HashMap<>();
        for (Category c : categorias) {
            nombresCat.put(c.getIdCategory(), c.getCategoryName());
        }

        modeloTabla = (DefaultTableModel) vista.tblProductos.getModel();
        modeloTabla.setRowCount(0);

        Object[] fila = new Object[7];
        for (Product p : lista) {
            fila[0] = p.getIdProduct();
            fila[1] = p.getSku();
            fila[2] = p.getProductName();
            fila[3] = nombresCat.getOrDefault(p.getIdCategory(), "Sin Categoría");
            fila[4] = "S/. " + p.getUnitPrice();
            fila[5] = p.getUnitWeightKg() + " Kg";
            fila[6] = p.getStatus();
            modeloTabla.addRow(fila);
        }
        vista.tblProductos.setModel(modeloTabla);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardar) registrar();
        if (e.getSource() == vista.btnModificar) modificar();
        if (e.getSource() == vista.btnEliminar) eliminar();
    }

    private void registrar() {
        String sku = vista.txtSku.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String descripcion = vista.txtDescripcion.getText().trim();
        String seleccionCat = (String) vista.cbCategorias.getSelectedItem();

        if (sku.isEmpty() || nombre.isEmpty() || seleccionCat == null) {
            JOptionPane.showMessageDialog(vista, "Campos SKU, Nombre y Categoría son obligatorios.");
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(vista.txtPrecio.getText().trim());
            BigDecimal peso = new BigDecimal(vista.txtPeso.getText().trim());
            int idCategory = mapCategorias.get(seleccionCat);

            Product p = new Product(0, idCategory, sku, nombre, descripcion, "", peso, precio, vista.cbStatus.getSelectedItem().toString());

            if (productDao.registrar(p)) {
                JOptionPane.showMessageDialog(vista, "Producto registrado correctamente.");
                limpiarCampos();
                listarProductos();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al registrar el producto. Verifique que el SKU no esté duplicado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Precio y Peso deben ser valores numéricos válidos.");
        }
    }

    private void modificar() {
        int filaSeleccionada = vista.tblProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto de la tabla para actualizar.");
            return;
        }

        int idProduct = Integer.parseInt(vista.tblProductos.getValueAt(filaSeleccionada, 0).toString());
        String sku = vista.txtSku.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String descripcion = vista.txtDescripcion.getText().trim();
        String seleccionCat = (String) vista.cbCategorias.getSelectedItem();

        if (sku.isEmpty() || nombre.isEmpty() || seleccionCat == null) {
            JOptionPane.showMessageDialog(vista, "Campos SKU, Nombre y Categoría son requeridos.");
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(vista.txtPrecio.getText().trim().replace("S/. ", ""));
            BigDecimal peso = new BigDecimal(vista.txtPeso.getText().trim().replace(" Kg", ""));
            int idCategory = mapCategorias.get(seleccionCat);

            Product p = new Product(idProduct, idCategory, sku, nombre, descripcion, "", peso, precio, vista.cbStatus.getSelectedItem().toString());

            if (productDao.modificar(p)) {
                JOptionPane.showMessageDialog(vista, "Producto actualizado correctamente.");
                limpiarCampos();
                listarProductos();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar el producto.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Verifique los formatos numéricos de precio y peso.");
        }
    }

    private void eliminar() {
        int filaSeleccionada = vista.tblProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione una fila para eliminar.");
            return;
        }

        int idProduct = Integer.parseInt(vista.tblProductos.getValueAt(filaSeleccionada, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(vista, "¿Desea eliminar el producto seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (productDao.eliminar(idProduct)) {
                JOptionPane.showMessageDialog(vista, "Producto eliminado.");
                limpiarCampos();
                listarProductos();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo eliminar el producto.");
            }
        }
    }

    private void limpiarCampos() {
        vista.txtSku.setText("");
        vista.txtNombre.setText("");
        vista.txtDescripcion.setText("");
        vista.txtPrecio.setText("");
        vista.txtPeso.setText("");
        vista.cbCategorias.setSelectedIndex(0);
        vista.cbStatus.setSelectedIndex(0);
        vista.tblProductos.clearSelection();
    }
}