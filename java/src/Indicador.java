import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;

class Indicador {
    private String nombre;
    private String formula;
    private StreamTokenizer tokens;
    private int token;
    private float valor;
    private String error = null;
    private HashMap<String, Cuenta> cuentas;

    Indicador(String nombre, String formula) {
        this.nombre = nombre;
        this.formula = formula;
    }

    Indicador(String nombre, String formula, HashMap<String, Cuenta> cuentas) {
        this(nombre, formula);
        calcularValor(cuentas);
    }

    void calcularValor(HashMap<String, Cuenta> cuentas) {
        this.cuentas = cuentas;
        calcularValor();
    }

    private void calcularValor() {
        Reader reader = new StringReader(formula);
        tokens = new StreamTokenizer(reader);
        tokens.ordinaryChar(Símbolos.DIVI.aChar());

        getToken();
        valor = expr();
        if (!esToken(Símbolos.FIN))
            genError("error de sintaxis");
    }

    boolean valida() { return error == null; }
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
            boolean cuentaEncontrada = cuentas.containsKey(tokens.sval);
            if (cuentaEncontrada) valor = cuentas.get(tokens.sval).getValor();
            else {
                genError("error por cuenta no encontrada");
                getToken();
            }
        } else if (aceptar(Símbolos.ABRE)) {
            valor = expr();
            esperar(Símbolos.CIERRA);
        } else {
            genError("factor error");
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
