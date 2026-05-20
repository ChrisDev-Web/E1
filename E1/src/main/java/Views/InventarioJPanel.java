package Views;

import Controllers.InventarioController;
import Models.Inventory;
import jiconfont.icons.font_awesome.FontAwesome;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Swing + Herencia: Acoplado al BasePanel con soporte para SP y Transferencias
public class InventarioJPanel extends BasePanel {
    private final InventarioController controller = new InventarioController();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JButton btnNew, btnEdit, btnTransfer, btnSearch;

    public InventarioJPanel() {
        super(
                "inventario",
                "Inventario",
                "Revise existencias, movimientos de mercancía y control de stock.",
                ViewIcons.build(FontAwesome.CLIPBOARD, 28)
        );
        initModuloComponents();
        cargarDatos();
    }

    private void initModuloComponents() {
        this.setLayout(new BorderLayout(10, 10));

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
        this.add(pnlTop, BorderLayout.NORTH);

        String[] columnas = {"ID Registro", "Almacén", "SKU", "Producto", "Stock Actual", "Reservado", "Mínimo"};
        model = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.add(new JScrollPane(table), BorderLayout.CENTER);

        // Registro de Listeners de Eventos
        btnSearch.addActionListener(e -> buscar());
        btnNew.addActionListener(e -> openModal(null));
        btnEdit.addActionListener(e -> prepararEdicion());
        btnTransfer.addActionListener(e -> abrirModalTransferencia());
    }

    private void cargarDatos() {
        try {
            model.setRowCount(0);
            List<Inventory> datos = controller.listarActivos(); 
            for (Inventory i : datos) {
                model.addRow(new Object[]{i.getIdInventory(), i.getWarehouseName(), i.getProductSku(), i.getProductName(), i.getStock(), i.getReservedStock(), i.getMinStock()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void buscar() {
        try {
            model.setRowCount(0);
            List<Inventory> datos = controller.buscarInventario(txtSearch.getText().trim());
            for (Inventory i : datos) {
                model.addRow(new Object[]{i.getIdInventory(), i.getWarehouseName(), i.getProductSku(), i.getProductName(), i.getStock(), i.getReservedStock(), i.getMinStock()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
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

        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Transferencia Interna de Mercadería", true);
        modal.setLayout(new GridLayout(5, 2, 10, 10));
        modal.setSize(400, 220);
        modal.setLocationRelativeTo(this);

        JTextField tOrigen = new JTextField(whName + " (" + prodName + ")");
        tOrigen.setEnabled(false);
        JTextField tIdTargetWh = new JTextField(); 
        JTextField tCantidad = new JTextField("0");

        modal.add(new JLabel(" Origen actual:")); modal.add(tOrigen);
        modal.add(new JLabel(" ID Almacén Destino:")); modal.add(tIdTargetWh);
        modal.add(new JLabel(" Cantidad a Mover (Max " + maxAvailable + "):")); modal.add(tCantidad);

        JButton btnProcesar = new JButton("Confirmar Envío");
        btnProcesar.addActionListener(e -> {
            try {
                int idDestino = Integer.parseInt(tIdTargetWh.getText().trim());
                int cant = Integer.parseInt(tCantidad.getText().trim());

                if (cant > maxAvailable) {
                    JOptionPane.showMessageDialog(modal, "No puedes transferir más del stock físico disponible.");
                    return;
                }

                controller.transferirExistencias(idSourceInv, idDestino, cant);
                JOptionPane.showMessageDialog(this, "¡Transferencia realizada con éxito!");
                modal.dispose();
                cargarDatos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(modal, "Verifique que los campos numéricos sean válidos.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(modal, ex.getMessage());
            }
        });

        modal.add(new JLabel()); modal.add(btnProcesar);
        modal.setVisible(true);
    }

    private void prepararEdicion() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione una fila de la grilla."); return; }
        Inventory i = new Inventory();
        i.setIdInventory((int) table.getValueAt(row, 0));
        i.setStock((int) table.getValueAt(row, 4));
        i.setReservedStock((int) table.getValueAt(row, 5));
        i.setMinStock((int) table.getValueAt(row, 6));
        openModal(i);
    }

    private void openModal(Inventory inv) {
        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Formulario de Existencias", true);
        modal.setLayout(new GridLayout(6, 2, 10, 10));
        modal.setSize(350, 260);
        modal.setLocationRelativeTo(this);

        JTextField tIdW = new JTextField();
        JTextField tIdP = new JTextField();
        JTextField tStock = new JTextField(inv != null ? String.valueOf(inv.getStock()) : "0");
        JTextField tRes = new JTextField(inv != null ? String.valueOf(inv.getReservedStock()) : "0");
        JTextField tMin = new JTextField(inv != null ? String.valueOf(inv.getMinStock()) : "0");

        if (inv != null) {
            tIdW.setText("Fijo (Modo Edición)"); tIdW.setEnabled(false);
            tIdP.setText("Fijo (Modo Edición)"); tIdP.setEnabled(false);
        }

        modal.add(new JLabel(" ID Almacén (FK):")); modal.add(tIdW);
        modal.add(new JLabel(" ID Producto (FK):")); modal.add(tIdP);
        modal.add(new JLabel(" Stock Real:")); modal.add(tStock);
        modal.add(new JLabel(" Reservado:")); modal.add(tRes);
        modal.add(new JLabel(" Stock Mínimo:")); modal.add(tMin);

        JButton btnSave = new JButton("Procesar");
        btnSave.addActionListener(e -> {
            try {
                int s = Integer.parseInt(tStock.getText());
                int r = Integer.parseInt(tRes.getText());
                int m = Integer.parseInt(tMin.getText());

                if (inv == null) {
                    int idAlmacen = Integer.parseInt(tIdW.getText());
                    int idProd = Integer.parseInt(tIdP.getText());
                    controller.registrarInventario(idAlmacen, idProd, s, r, m);
                } else {
                    controller.modificarInventario(inv.getIdInventory(), s, r, m);
                }
                modal.dispose();
                cargarDatos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(modal, "Campos numéricos inválidos.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(modal, ex.getMessage());
            }
        });

        modal.add(new JLabel()); modal.add(btnSave);
        modal.setVisible(true);
    }
}