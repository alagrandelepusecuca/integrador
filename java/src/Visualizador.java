import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.HashSet;

public class Visualizador {
    private JComboBox<String> cmbEmpresa;
    private JComboBox<Integer> cmbPeríodo;
    private JTable tblCuentas;
    private JComboBox<String> cmbIndicador;
    private JButton volverButton;
    private JLabel lblIndicador;
    private JPanel panel;
    private JDialog dialog;
    private HashSet<Integer> períodos;
    private HashMap<String, Empresa> empresas;
    private DefaultTableModel dtm = new DefaultTableModel();

    private void cerrar() {
        dialog.setVisible(false);
        dialog.dispose();
    }
    
    private void actualizarValores(boolean soloIndicador) {
        String nomEmp = cmbEmpresa.getSelectedItem().toString();
        Empresa e = empresas.get(nomEmp);
        int per = (int) cmbPeríodo.getSelectedItem();

        if (!soloIndicador) {
            HashMap<String, Cuenta> cuentas = e.obtenerCuentasDelPeríodo(per);
            dtm.setRowCount(0);
            for (Cuenta c : cuentas.values())
                dtm.addRow(new Object[]{c.getNombre(), c.getValor()});
        }

        Indicador i = Indicador.get(cmbIndicador.getSelectedItem().toString());
        i.calcularValor(e, per);
        String lbl = i.esVálido() ? Float.toString(i.valor()) : i.error();
        lblIndicador.setText(lbl);
    }

    private void completarCombos() {
        for (int p : períodos) cmbPeríodo.addItem(p);
        for (String e : empresas.keySet()) cmbEmpresa.addItem(e);
        for (String i : Indicador.indicadores.keySet()) cmbIndicador.addItem(i);
    }

    Visualizador(JFrame frame, HashSet<Integer> períodos, HashMap<String, Empresa> empresas) {
        this.períodos = períodos;
        this.empresas = empresas;
        completarCombos();
        dtm.addColumn("Nombre");
        dtm.addColumn("Valor");
        tblCuentas.setModel(dtm);

        volverButton.addActionListener(actionEvent -> cerrar());
        cmbEmpresa.addActionListener(actionEvent -> actualizarValores(false));
        cmbPeríodo.addActionListener(actionEvent -> actualizarValores(false));
        cmbIndicador.addActionListener(actionEvent -> actualizarValores(true));

        actualizarValores(false);

        dialog = new JDialog(frame, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setVisible(true);
    }
}
