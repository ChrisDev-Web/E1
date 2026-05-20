package Views;

import Controllers.InventarioController;
import Models.Inventory;
import Models.ReferenceItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import jiconfont.icons.font_awesome.FontAwesome;

public class InventarioJPanel extends BasePanel {

    private final InventarioController controller = new InventarioController();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JButton btnNew;
    private JButton btnEdit;
    private JButton btnTransfer;
    private JButton btnSearch;

    public InventarioJPanel() {
        super(
                "inventario",
                "Inventario",
                "Revise existencias, movimientos de mercancia y control de stock.",
                ViewIcons.build(FontAwesome.CLIPBOARD, 28)
        );
        initModuloComponents();
        cargarDatos();
    }

    private void initModuloComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlTop.setBackground(Color.WHITE);

        txtSearch = new JTextField(15);
        btnSearch = new JButton("Filtrar");
        btnNew = new JButton("Asignar Stock");
        btnEdit = new JButton("Ajustar Stock");
        btnTransfer = new JButton("Transferir Stock");

        pnlTop.add(new JLabel("Buscar:"));
        pnlTop.add(txtSearch);
        pnlTop.add(btnSearch);
        pnlTop.add(btnNew);
        pnlTop.add(btnEdit);
        pnlTop.add(btnTransfer);
        add(pnlTop, BorderLayout.NORTH);

        String[] columnas = {"ID Registro", "Almacen", "SKU", "Producto", "Stock Actual", "Reservado", "Minimo"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> buscar());
        btnNew.addActionListener(e -> openModal(null));
        btnEdit.addActionListener(e -> prepararEdicion());
        btnTransfer.addActionListener(e -> abrirModalTransferencia());
    }

    private void cargarDatos() {
        try {
            fillTable(controller.listarActivos());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void buscar() {
        try {
            fillTable(controller.buscarInventario(txtSearch.getText()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void fillTable(List<Inventory> items) {
        model.setRowCount(0);

        for (Inventory i : items) {
            model.addRow(new Object[]{
                i.getIdInventory(),
                i.getWarehouseName(),
                i.getProductSku(),
                i.getProductName(),
                i.getStock(),
                i.getReservedStock(),
                i.getMinStock()
            });
        }
    }

    private void abrirModalTransferencia() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione el registro de stock origen que desea mover.");
            return;
        }

        int idSourceInv = (int) table.getValueAt(row, 0);
        String whName = (String) table.getValueAt(row, 1);
        String prodName = (String) table.getValueAt(row, 3);
        int maxAvailable = (int) table.getValueAt(row, 4);

        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Transferencia Interna de Mercaderia", true);
        modal.setLayout(new GridLayout(5, 2, 10, 10));
        modal.setSize(460, 230);
        modal.setLocationRelativeTo(this);

        JTextField tOrigen = new JTextField(whName + " (" + prodName + ")");
        tOrigen.setEnabled(false);

        JComboBox<ReferenceItem> cmbTargetWarehouse = new JComboBox<>();
        JTextField tCantidad = new JTextField("0");
        loadReferenceModel(cmbTargetWarehouse, () -> controller.listarAlmacenes());

        modal.add(new JLabel(" Origen actual:"));
        modal.add(tOrigen);
        modal.add(new JLabel(" Almacen destino:"));
        modal.add(cmbTargetWarehouse);
        modal.add(new JLabel(" Cantidad a mover (Max " + maxAvailable + "):"));
        modal.add(tCantidad);

        JButton btnProcesar = new JButton("Confirmar envio");
        btnProcesar.addActionListener(e -> {
            try {
                ReferenceItem destino = getSelectedReference(cmbTargetWarehouse, "Seleccione un almacen destino.");
                int cant = Integer.parseInt(tCantidad.getText().trim());

                if (cant > maxAvailable) {
                    JOptionPane.showMessageDialog(modal, "No puedes transferir mas del stock fisico disponible.");
                    return;
                }

                controller.transferirExistencias(idSourceInv, destino.getId(), cant);
                JOptionPane.showMessageDialog(this, "Transferencia realizada con exito.");
                modal.dispose();
                cargarDatos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(modal, "Verifique que la cantidad sea numerica.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(modal, ex.getMessage());
            }
        });

        modal.add(new JLabel());
        modal.add(btnProcesar);
        modal.setVisible(true);
    }

    private void prepararEdicion() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila de la grilla.");
            return;
        }

        Inventory i = new Inventory();
        i.setIdInventory((int) table.getValueAt(row, 0));
        i.setWarehouseName(String.valueOf(table.getValueAt(row, 1)));
        i.setProductSku(String.valueOf(table.getValueAt(row, 2)));
        i.setProductName(String.valueOf(table.getValueAt(row, 3)));
        i.setStock((int) table.getValueAt(row, 4));
        i.setReservedStock((int) table.getValueAt(row, 5));
        i.setMinStock((int) table.getValueAt(row, 6));
        openModal(i);
    }

    private void openModal(Inventory inv) {
        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Formulario de Existencias", true);
        modal.setLayout(new GridLayout(6, 2, 10, 10));
        modal.setSize(460, 280);
        modal.setLocationRelativeTo(this);

        JComboBox<ReferenceItem> cmbWarehouse = new JComboBox<>();
        JComboBox<ReferenceItem> cmbProduct = new JComboBox<>();
        JTextField tStock = new JTextField(inv != null ? String.valueOf(inv.getStock()) : "0");
        JTextField tRes = new JTextField(inv != null ? String.valueOf(inv.getReservedStock()) : "0");
        JTextField tMin = new JTextField(inv != null ? String.valueOf(inv.getMinStock()) : "0");

        loadReferenceModel(cmbWarehouse, () -> controller.listarAlmacenes());
        loadReferenceModel(cmbProduct, () -> controller.listarProductos());

        java.awt.Component warehouseField = cmbWarehouse;
        java.awt.Component productField = cmbProduct;

        if (inv != null) {
            JTextField tWarehouse = new JTextField(inv.getWarehouseName());
            JTextField tProduct = new JTextField(inv.getProductSku() + " - " + inv.getProductName());
            tWarehouse.setEnabled(false);
            tProduct.setEnabled(false);
            warehouseField = tWarehouse;
            productField = tProduct;
        }

        modal.add(new JLabel(" Almacen:"));
        modal.add(warehouseField);
        modal.add(new JLabel(" Producto:"));
        modal.add(productField);
        modal.add(new JLabel(" Stock real:"));
        modal.add(tStock);
        modal.add(new JLabel(" Reservado:"));
        modal.add(tRes);
        modal.add(new JLabel(" Stock minimo:"));
        modal.add(tMin);

        JButton btnSave = new JButton("Procesar");
        btnSave.addActionListener(e -> {
            try {
                int s = Integer.parseInt(tStock.getText().trim());
                int r = Integer.parseInt(tRes.getText().trim());
                int m = Integer.parseInt(tMin.getText().trim());

                if (inv == null) {
                    ReferenceItem almacen = getSelectedReference(cmbWarehouse, "Seleccione un almacen.");
                    ReferenceItem producto = getSelectedReference(cmbProduct, "Seleccione un producto.");
                    controller.registrarInventario(almacen.getId(), producto.getId(), s, r, m);
                } else {
                    controller.modificarInventario(inv.getIdInventory(), s, r, m);
                }

                modal.dispose();
                cargarDatos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(modal, "Campos numericos invalidos.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(modal, ex.getMessage());
            }
        });

        modal.add(new JLabel());
        modal.add(btnSave);
        modal.setVisible(true);
    }

    private void loadReferenceModel(JComboBox<ReferenceItem> comboBox, ReferenceLoader loader) {
        try {
            DefaultComboBoxModel<ReferenceItem> comboModel = new DefaultComboBoxModel<>();

            for (ReferenceItem item : loader.load()) {
                comboModel.addElement(item);
            }

            comboBox.setModel(comboModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private ReferenceItem getSelectedReference(JComboBox<ReferenceItem> comboBox, String message) throws Exception {
        ReferenceItem item = (ReferenceItem) comboBox.getSelectedItem();

        if (item == null || item.getId() <= 0) {
            throw new Exception(message);
        }

        return item;
    }

    private interface ReferenceLoader {
        List<ReferenceItem> load() throws Exception;
    }
}
