import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

public class Menu {
    private JButton cargarCuentasButton;
    private JButton visualizarButton;
    private JButton cargarIndicadorButton;
    private JPanel panel;
    private JFrame frame = new JFrame();
    private JFileChooser jfc = new JFileChooser();

    private HashSet<Integer> períodos = new HashSet<>();
    private HashMap<String, Empresa> empresas = new HashMap<>();
    private HashMap<String, Metodología> metodologías = new HashMap<>();

    private Menu() {
        cargarCuentasButton.addActionListener(actionEvent -> {
            if (jfc.showOpenDialog(cargarCuentasButton) == JFileChooser.APPROVE_OPTION)
                if (cargarDatos(jfc.getSelectedFile().getAbsolutePath())) visualizarButton.setEnabled(true);
        });

        cargarIndicadorButton.addActionListener(actionEvent -> new NuevoIndicador(frame));

        visualizarButton.addActionListener(actionEvent -> new Visualizador(frame, períodos, empresas));

        new Indicador("INET", "INOC+INOD");
        new Indicador("ROE", "(INET-DVD)/CAPT");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Menu();
    }

    private boolean cargarDatos(String ruta) {
        XSSFWorkbook wb;
        Sheet s = null;
        try {
            InputStream is = new FileInputStream(ruta);
            wb = new XSSFWorkbook(is);
            s = wb.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert s != null;
        short nuevasCuentas = 0;
        for (Row r : s) {
            String nombreEmpresa = r.getCell(0).getStringCellValue();
            if (nombreEmpresa.isEmpty()) break;
            nuevasCuentas++;
            int período = (int) r.getCell(1).getNumericCellValue();
            String nombreCuenta = r.getCell(2).getStringCellValue();
            float valorCuenta = (float) r.getCell(3).getNumericCellValue();

            boolean empresaConocida = empresas.containsKey(nombreEmpresa);
            períodos.add(período);
            Empresa e = empresaConocida ? empresas.get(nombreEmpresa) : new Empresa(nombreEmpresa);
            Cuenta c = new Cuenta(período, nombreCuenta, valorCuenta);

            e.agregarCuenta(c);
            if (!empresaConocida) empresas.put(nombreEmpresa, e);
        }
        return nuevasCuentas != 0;
    }
}
