package calculadora.logica;

import calculadora.modelo.Fraccion;
import calculadora.modelo.Polinomio;
import calculadora.modelo.Termino;

import java.util.ArrayList;
import java.util.List;

/**
 * Convierte texto como "3x^2 + 5x - 4" o "2xy + y^3" en un {@link Polinomio}.
 * Admite: enteros, decimales (2.5), fracciones (1/2), variables x e y, exponentes enteros >= 0 con ^
 * (o superíndices ², ³...), el signo * opcional y términos semejantes repetidos.
 */
public final class ParserPolinomio {

    private static final String SUPERINDICES = "⁰¹²³⁴⁵⁶⁷⁸⁹";

    private ParserPolinomio() { }

    public static Polinomio parsear(String texto) {
        String s = normalizar(texto);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("La expresión está vacía.");
        }
        List<Termino> lista = new ArrayList<>();
        int i = 0;
        int n = s.length();
        while (i < n) {
            int signo = 1;
            while (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
                if (s.charAt(i) == '-') signo = -signo;
                i++;
            }
            Fraccion coef = Fraccion.UNO;
            boolean hayNumero = false;
            boolean hayVariable = false;
            int ex = 0;
            int ey = 0;
            while (i < n && s.charAt(i) != '+' && s.charAt(i) != '-') {
                char c = s.charAt(i);
                if (Character.isDigit(c) || c == '.') {
                    int j = i;
                    while (j < n && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.' || s.charAt(j) == '/')) j++;
                    coef = coef.multiplicar(Fraccion.deTexto(s.substring(i, j)));
                    hayNumero = true;
                    i = j;
                } else if (c == 'x' || c == 'y') {
                    i++;
                    int e = 1;
                    if (i < n && s.charAt(i) == '^') {
                        i++;
                        int j = i;
                        while (j < n && Character.isDigit(s.charAt(j))) j++;
                        if (j == i || j - i > 2) {
                            throw new IllegalArgumentException(
                                    "Exponente inválido: use enteros no negativos de hasta 2 cifras (ej. x^3).");
                        }
                        e = Integer.parseInt(s.substring(i, j));
                        i = j;
                    }
                    if (c == 'x') ex += e; else ey += e;
                    hayVariable = true;
                } else if (c == '*') {
                    i++;
                } else if (c == '(' || c == ')') {
                    throw new IllegalArgumentException(
                            "No se admiten paréntesis: expanda la expresión (ej. 3x^2 + 6x).");
                } else {
                    throw new IllegalArgumentException(
                            "Carácter no válido: '" + c + "'. Use solo números y las variables x, y.");
                }
            }
            if (!hayNumero && !hayVariable) {
                throw new IllegalArgumentException("Falta un término después de un signo + o -.");
            }
            if (signo < 0) coef = coef.negar();
            lista.add(new Termino(coef, ex, ey));
        }
        return new Polinomio(lista);
    }

    /** Minúsculas, sin espacios, superíndices convertidos a ^n y coma decimal convertida a punto. */
    static String normalizar(String texto) {
        StringBuilder sb = new StringBuilder();
        boolean enSuper = false;
        for (char c : texto.toLowerCase().toCharArray()) {
            int idx = SUPERINDICES.indexOf(c);
            if (idx >= 0) {
                if (!enSuper) sb.append('^');
                enSuper = true;
                sb.append(idx);
                continue;
            }
            enSuper = false;
            if (Character.isWhitespace(c)) continue;
            if (c == '×' || c == '·') c = '*';
            if (c == ',') c = '.';
            if (c == '−' || c == '–') c = '-';
            sb.append(c);
        }
        return sb.toString();
    }
}
