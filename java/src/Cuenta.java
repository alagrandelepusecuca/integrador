class Cuenta {
    private int período;
    private String nombre;
    private float valor;

    Cuenta(int período, String nombre, float valor) {
        this.período = período;
        this.nombre = nombre;
        this.valor = valor;
    }

    int getPeriod() { return período; }
    String getNombre() { return nombre; }
    float getValor() { return valor; }
}