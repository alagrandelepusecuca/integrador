import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

class Main {
    private HashSet<Short> períodos = new HashSet<>();
    private HashMap<String, Empresa> empresas = new HashMap<>();
    private HashMap<String, Indicador> indicadores = new HashMap<>();
    private HashMap<String, Metodología> metodologías = new HashMap<>();

    public static void main(String[] args) {
        Main m = new Main();
        m.run();
    }

    private void run() {
        cargarDatos();
        agregarIndicador("INET", "INOC+INOD");
        agregarIndicador("ROE", "(INET-DVD)/CAPT");
        Empresa e = empresas.entrySet().iterator().next().getValue();
        Indicador i = indicadores.get("INET");
        i.calcularValor(e.obtenerCuentasDelPeríodo((short) 2014));
        assert (i.valida());
    }

    private void agregarIndicador(String nombre, String formula) {
        Indicador i = new Indicador(nombre, formula);
        indicadores.put(i.getNombre(), i);
    }

    private void cargarDatos() {
        XSSFWorkbook wb;
        Sheet s = null;
        try {
            InputStream inp = new FileInputStream("cuentas.xlsx");
            wb = new XSSFWorkbook(inp);
            s = wb.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert s != null;
        for (Row r : s) {
            String nombreEmpresa = r.getCell(0).getStringCellValue();
            if (nombreEmpresa.isEmpty()) break;
            Short período = (short) r.getCell(1).getNumericCellValue();
            String nombreCuenta = r.getCell(2).getStringCellValue();
            float valorCuenta = (float) r.getCell(3).getNumericCellValue();

            boolean empresaConocida = empresas.containsKey(nombreEmpresa);
            períodos.add(período);
            Empresa e = empresaConocida ? empresas.get(nombreEmpresa) : new Empresa(nombreEmpresa);
            Cuenta c = new Cuenta(período, nombreCuenta, valorCuenta);

            e.agregarCuenta(c);
            if (!empresaConocida) empresas.put(nombreEmpresa, e);
        }
    }

}
