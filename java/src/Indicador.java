import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;

class Indicador {
    static HashMap<String, Indicador> indicadores = new HashMap<>();
    private String nombre;
    private String formula;
    private Empresa empresa;
    private int período;
    private StreamTokenizer tokens;
    private int token;
    private float valor = 0;
    private String error = null;
    private HashMap<String, Cuenta> cuentas;

    Indicador(String nombre, String formula) {
        this.nombre = nombre;
        this.formula = formula;
        indicadores.put(nombre, this);
    }

    Indicador(String nombre, String formula, Empresa empr, int período) {
        this(nombre, formula);
        calcularValor(empr, período);
    }

    static Indicador get(String nombre) {
        return indicadores.get(nombre);
    }

    float calcularValor(Empresa empr, int período) {
        this.empresa = empr;
        this.período = período;
        this.cuentas = empr.obtenerCuentasDelPeríodo(período);
        return calcularValor();
    }

    private float calcularValor() {
        Reader reader = new StringReader(formula);
        tokens = new StreamTokenizer(reader);

        for (Símbolos s : Símbolos.values())
            tokens.ordinaryChar(s.aChar());

        getToken();
        valor = expr();
        if (!esToken(Símbolos.FIN))
            genError("error de sintaxis");
        return valor;
    }

    boolean esVálido() { return error == null; }
    float valor() { return valor; }
    String error() { return error; }

    // expr = [addop] term {(addop) term} FIN
    private float expr() {
        int sign = 1;
        aceptar(Símbolos.MAS);
        if (aceptar(Símbolos.MENOS)) sign = -1;
        float valor = sign * term();
        while (Símbolos.esAdic(token)) {
            if (aceptar(Símbolos.MAS)) valor += term();
            if (aceptar(Símbolos.MENOS)) valor -= term();
        }
        return valor;
    }

    // term = factor {(multOp) factor} FIN
    private float term() {
        float valor = factor();
        while (Símbolos.esMult(token)) {
            if (aceptar(Símbolos.MULT)) valor *= factor();
            if (aceptar(Símbolos.DIVI)) valor /= factor();
        }
        return valor;
    }

    // factor = NOMBRE | "(" expr ")" FIN
    private float factor() {
        float valor = 0;
        if (esToken(Símbolos.NOMBRE)) {
            String id = tokens.sval;
            if (indicadores.containsKey(id)) {
                valor = indicadores.get(id).calcularValor(empresa, período);
            } else {
                boolean cuentaEstá = cuentas.containsKey(id);
                if (cuentaEstá) valor = cuentas.get(id).getValor();
                else genError("error por cuenta no encontrada");
            }
            getToken();
        } else if (aceptar(Símbolos.ABRE)) {
            valor = expr();
            esperar(Símbolos.CIERRA);
        } else {
            genError("error en parseo de factores");
            getToken();
        }
        return valor;
    }

    private void getToken() {
        try {
            token = tokens.nextToken();
        } catch (IOException e) {
            genError("error de e/s " + e.getMessage());
        }
    }

    // Devuelve verdadero si el token actual coincide con el recibido
    private boolean esToken(Símbolos ss) {
        return token == ss.token();
    }

    // Requiere un símbolo coincidente; genera un error si no llega
    private void esperar(Símbolos ss) {
        if (aceptar(ss)) return;
        genError("missing " + ss.aChar());
    }

    // Avanza si el token actual coincide con el símbolo recibido
    private boolean aceptar(Símbolos ss) {
        if (esToken(ss)) {
            getToken();
            return true;
        }
        return false;
    }
    
    private void genError(String ss) {
        if (error == null)
            error = ss + " at " + tokens.toString().replaceAll(",.*$", "");
    }

    String getNombre() {
        return nombre;
    }
}

enum Símbolos {
    MAS ('+'), MENOS ('-'), MULT ('*'), DIVI ('/'),
    ABRE ('('), CIERRA (')'),
    FIN (StreamTokenizer.TT_EOF),
    NOMBRE (StreamTokenizer.TT_WORD);

    private int token;

    Símbolos(int token) {
        this.token = token;
    }

    public int token()  {
        return this.token;
    }

    public char aChar() {
        if (this.token < 32) return '\ufffd';
        else return (char) this.token;
    }

    public static boolean esAdic(int token) {
        return token == MAS.token || token == MENOS.token;
    }

    public static boolean esMult(int token) {
        return token == MULT.token || token == DIVI.token;
    }
}
