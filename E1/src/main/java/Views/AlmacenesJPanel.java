package Views;

import Controllers.AlmacenesController;
import Models.Warehouses;
import jiconfont.icons.font_awesome.FontAwesome;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Swing + Herencia: vista base para la seccion de almacenes.
public class AlmacenesJPanel extends BasePanel {

    private final AlmacenesController controller = new AlmacenesController();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JButton btnNew, btnEdit, btnDelete, btnInactives, btnSearch;
    private boolean mostrandoInactivos = false;

    public AlmacenesJPanel() {
        super(
                "almacenes",
                "Almacenes",
                "Visualice y gestione los almacenes del sistema.",
                ViewIcons.build(FontAwesome.BUILDING_O, 28)
        );
        // Inicializamos los componentes propios después de construir la base
        initModuloComponents();
        cargarDatos();
    }

    private void initModuloComponents() {
        // Configuramos el layout principal de este panel (abajo de la cabecera que arme BasePanel)
        // Nota: Si tu BasePanel usa BorderLayout, añadimos esto al CENTER.
        this.setLayout(new BorderLayout(10, 10));

        // Panel de acciones (Buscador y Botones)
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlTop.setBackground(Color.WHITE);

        txtSearch = new JTextField(15);
        btnSearch = new JButton("Buscar");
        btnNew = new JButton("Nuevo");
        btnEdit = new JButton("Editar");
        btnDelete = new JButton("Eliminar");
        btnInactives = new JButton("Ver Inactivos");

        pnlTop.add(new JLabel("Filtrar:"));
        pnlTop.add(txtSearch);
        pnlTop.add(btnSearch);
        pnlTop.add(btnNew);
        pnlTop.add(btnEdit);
        pnlTop.add(btnDelete);
        pnlTop.add(btnInactives);

        this.add(pnlTop, BorderLayout.NORTH);

        // Tabla de datos
        String[] columnas = {"ID", "Nombre Almacén", "Dirección", "Ciudad", "País", "Teléfono"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.add(new JScrollPane(table), BorderLayout.CENTER);

        // Eventos de los botones disparando al controlador
        btnSearch.addActionListener(e -> buscar());
        btnNew.addActionListener(e -> openModal(null));
        btnEdit.addActionListener(e -> prepararEdicion());
        btnDelete.addActionListener(e -> ejecutarEliminacion());
        btnInactives.addActionListener(e -> conmutarVistaInactivos());
    }

    private void cargarDatos() {
        try {
            model.setRowCount(0);
            List<Warehouses> datos = mostrandoInactivos ? controller.listarInactivos() : controller.listarActivos();
            for (Warehouses w : datos) {
                model.addRow(new Object[]{w.getIdWarehouse(), w.getWarehouseName(), w.getAddress(), w.getCity(), w.getCountry(), w.getPhone()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        try {
            model.setRowCount(0);
            List<Warehouses> datos = controller.buscarAlmacenes(txtSearch.getText(), mostrandoInactivos);
            for (Warehouses w : datos) {
                model.addRow(new Object[]{w.getIdWarehouse(), w.getWarehouseName(), w.getAddress(), w.getCity(), w.getCountry(), w.getPhone()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prepararEdicion() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla.");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        Warehouses seleccionado = new Warehouses(id, (String) table.getValueAt(row, 1), (String) table.getValueAt(row, 2), (String) table.getValueAt(row, 3), (String) table.getValueAt(row, 4), (String) table.getValueAt(row, 5), "ACTIVE");
        openModal(seleccionado);
    }

    private void ejecutarEliminacion() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila primero.");
            return;
        }
        int id = (int) table.getValueAt(row, 0);

        if (mostrandoInactivos) {
            int op = JOptionPane.showConfirmDialog(this, "¿Eliminar permanentemente de la BD?", "Borrado Físico (Crítico)", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                try {
                    controller.eliminarDefinitivo(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        } else {
            int op = JOptionPane.showConfirmDialog(this, "¿Enviar este almacén al listado de inactivos?", "Eliminación Lógica", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                try {
                    controller.eliminarLogico(id);
                    cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        }
    }

    private void conmutarVistaInactivos() {
        mostrandoInactivos = !mostrandoInactivos;
        if (mostrandoInactivos) {
            btnInactives.setText("Ver Activos");
            btnNew.setEnabled(false);
            btnEdit.setText("Restaurar");
        } else {
            btnInactives.setText("Ver Inactivos");
            btnNew.setEnabled(true);
            btnEdit.setText("Editar");
        }
        txtSearch.setText("");
        cargarDatos();
    }

    private void openModal(Warehouses w) {
        if (mostrandoInactivos && w != null) {
            try {
                controller.restaurarAlmacen(w.getIdWarehouse());
                cargarDatos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
            return;
        }

        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Gestión de Almacén", true);
        modal.setLayout(new GridLayout(6, 2, 8, 8));
        modal.setSize(380, 250);
        modal.setLocationRelativeTo(this);

        JTextField tName = new JTextField(w != null ? w.getWarehouseName() : "");
        JTextField tDir = new JTextField(w != null ? w.getAddress() : "");
        JTextField tCity = new JTextField(w != null ? w.getCity() : "");
        JTextField tCountry = new JTextField(w != null ? w.getCountry() : "");
        JTextField tPhone = new JTextField(w != null ? w.getPhone() : "");

        modal.add(new JLabel(" Nombre:"));
        modal.add(tName);
        modal.add(new JLabel(" Dirección:"));
        modal.add(tDir);
        modal.add(new JLabel(" Ciudad:"));
        modal.add(tCity);
        modal.add(new JLabel(" País:"));
        modal.add(tCountry);
        modal.add(new JLabel(" Teléfono:"));
        modal.add(tPhone);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                if (w == null) {
                    controller.registrarAlmacen(tName.getText(), tDir.getText(), tCity.getText(), tCountry.getText(), tPhone.getText());
                } else {
                    controller.modificarAlmacen(w.getIdWarehouse(), tName.getText(), tDir.getText(), tCity.getText(), tCountry.getText(), tPhone.getText());
                }
                modal.dispose();
                cargarDatos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(modal, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        modal.add(new JLabel());
        modal.add(btnGuardar);
        modal.setVisible(true);
    }
}
