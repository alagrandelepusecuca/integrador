class Cuenta {
    private Short período;
    private String nombre;
    private float valor;

    Cuenta(Short período, String nombre, float valor) {
        this.período = período;
        this.nombre = nombre;
        this.valor = valor;
    }

    Short getPeriod() { return período; }
    String getNombre() { return nombre; }
    float getValor() { return valor; }
}