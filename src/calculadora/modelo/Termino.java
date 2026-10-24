package calculadora.modelo;

/**
 * Un término del polinomio: coeficiente · x^expX · y^expY.
 * Generaliza la clase "Polinomio" del ejemplo (coeficiente, exponente) para admitir dos variables.
 * Cada objeto sabe derivarse e integrarse a sí mismo respecto de la variable elegida.
 */
public final class Termino {

    private static final String SUPERINDICES = "⁰¹²³⁴⁵⁶⁷⁸⁹";

    private final Fraccion coeficiente;
    private final int expX;
    private final int expY;

    public Termino(Fraccion coeficiente, int expX, int expY) {
        this.coeficiente = coeficiente;
        this.expX = expX;
        this.expY = expY;
    }

    public Fraccion getCoeficiente() { return coeficiente; }
    public int getExpX() { return expX; }
    public int getExpY() { return expY; }

    /** Exponente de la variable indicada ('x' o 'y'). */
    public int exponente(char variable) {
        return variable == 'x' ? expX : expY;
    }

    private Termino conExponente(char variable, int nuevoExp, Fraccion nuevoCoef) {
        return variable == 'x'
                ? new Termino(nuevoCoef, nuevoExp, expY)
                : new Termino(nuevoCoef, expX, nuevoExp);
    }

    /** d/dv [c·vⁿ] = c·n·vⁿ⁻¹ ; si el término no contiene v es constante y su derivada es 0. */
    public Termino derivar(char variable) {
        int e = exponente(variable);
        if (e == 0) {
            return new Termino(Fraccion.CERO, 0, 0);
        }
        return conExponente(variable, e - 1, coeficiente.multiplicar(Fraccion.de(e)));
    }

    /** ∫ c·vⁿ dv = c·vⁿ⁺¹/(n+1) (las demás variables se tratan como constantes). */
    public Termino integrar(char variable) {
        int e = exponente(variable);
        return conExponente(variable, e + 1, coeficiente.dividir(Fraccion.de(e + 1)));
    }

    /** Sustituye la variable por un valor numérico: c·vⁿ → c·(valor)ⁿ. */
    public Termino sustituir(char variable, Fraccion valor) {
        int e = exponente(variable);
        return conExponente(variable, 0, coeficiente.multiplicar(valor.potencia(e)));
    }

    public boolean mismaParteLiteral(Termino o) {
        return expX == o.expX && expY == o.expY;
    }

    public boolean esConstante() {
        return expX == 0 && expY == 0;
    }

    public int grado() {
        return expX + expY;
    }

    public static String superindice(int n) {
        StringBuilder sb = new StringBuilder();
        for (char c : Integer.toString(n).toCharArray()) {
            sb.append(SUPERINDICES.charAt(c - '0'));
        }
        return sb.toString();
    }

    /** Parte literal, por ejemplo "x²y". */
    public String parteLiteral() {
        StringBuilder sb = new StringBuilder();
        if (expX > 0) {
            sb.append('x');
            if (expX > 1) sb.append(superindice(expX));
        }
        if (expY > 0) {
            sb.append('y');
            if (expY > 1) sb.append(superindice(expY));
        }
        return sb.toString();
    }

    /** Texto del término sin el signo (el signo lo pone el polinomio). */
    public String textoSinSigno() {
        Fraccion c = coeficiente.abs();
        String lit = parteLiteral();
        if (lit.isEmpty()) {
            return c.toString();
        }
        String cabeza = c.getNumerador() == 1 ? "" : Long.toString(c.getNumerador());
        String cola = c.getDenominador() == 1 ? "" : "/" + c.getDenominador();
        return cabeza + lit + cola;
    }

    @Override
    public String toString() {
        return (coeficiente.esNegativa() ? "-" : "") + textoSinSigno();
    }
}
