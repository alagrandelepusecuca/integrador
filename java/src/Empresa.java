import java.util.ArrayList;

class Empresa {
    private String nombre;
    private ArrayList<Cuenta> cuentas = new ArrayList<>();

    Empresa(String nombre) { this.nombre = nombre; }

    String getNombre() { return nombre; }

    void agregarCuenta(Cuenta c) { cuentas.add(c); }

    ArrayList<Cuenta> obtenerCuentasDelPeríodo(short y) {
        ArrayList<Cuenta> cuentasDelPeríodo = new ArrayList<>();
        for (Cuenta c : cuentas)
            if (c.getPeríodo() == y)
                cuentasDelPeríodo.add(c);
        return cuentasDelPeríodo;
    }
}