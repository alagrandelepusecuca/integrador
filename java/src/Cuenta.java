class Cuenta {
    private short período;
    private String nombre;
    private float valor;

    Cuenta(short período, String nombre, float valor) {
        this.período = período;
        this.nombre = nombre;
        this.valor = valor;
    }

    short getPeríodo() { return período; }
    public String getNombre() { return nombre; }
    public float getValor() { return valor; }
}