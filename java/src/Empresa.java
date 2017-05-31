import java.util.ArrayList;
import java.util.HashMap;

class Empresa {
    private String nombre;
    private ArrayList<Cuenta> cuentas = new ArrayList<>();

    Empresa(String nombre) { this.nombre = nombre; }

    String getNombre() { return nombre; }

    void agregarCuenta(Cuenta c) { cuentas.add(c); }

    HashMap<String, Cuenta> obtenerCuentasDelPeríodo(Short period) {
        HashMap<String, Cuenta> cuentasDelPeríodo = new HashMap<>();
        for (Cuenta c : cuentas)
            if (c.getPeriod().equals(period))
                cuentasDelPeríodo.put(c.getNombre(), c);
        return cuentasDelPeríodo;
    }
}