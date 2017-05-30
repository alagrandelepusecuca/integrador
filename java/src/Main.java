import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

class Main {
    private HashMap<String, Empresa> empresas = new HashMap<>();
    private ArrayList<Indicador> indicadores = new ArrayList<>();
    private ArrayList<Metodología> metodologías = new ArrayList<>();

    public static void main(String[] args) {
        Main m = new Main();
        m.run();
    }

    private void run() {
        cargarDatos("cuentas.xlsx");
    }

    private void cargarDatos(String path) {
        XSSFWorkbook wb;
        Sheet s = null;
        try {
            InputStream inp = new FileInputStream(path);
            wb = new XSSFWorkbook(inp);
            s = wb.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert s != null;
        for (Row r : s) {
            String nombreEmpresa = r.getCell(0).getStringCellValue();
            if (nombreEmpresa.isEmpty()) break;
            short período = (short) r.getCell(1).getNumericCellValue();
            String nombreCuenta = r.getCell(2).getStringCellValue();
            float valorCuenta = (float) r.getCell(3).getNumericCellValue();

            boolean empresaConocida = empresas.containsKey(nombreEmpresa);
            Empresa e = empresaConocida ? empresas.get(nombreEmpresa) : new Empresa(nombreEmpresa);
            Cuenta c = new Cuenta(período, nombreCuenta, valorCuenta);

            e.agregarCuenta(c);
            if (!empresaConocida) empresas.put(nombreEmpresa, e);
        }
    }

}
