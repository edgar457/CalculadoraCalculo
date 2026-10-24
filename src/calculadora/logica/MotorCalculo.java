package calculadora.logica;

import calculadora.modelo.Fraccion;
import calculadora.modelo.Polinomio;
import calculadora.modelo.Termino;

import java.util.ArrayList;
import java.util.List;

/**
 * Lógica de la calculadora (sin interfaz gráfica): deriva e integra polinomios en x e y,
 * generando además el procedimiento paso a paso.
 */
public class MotorCalculo {

    /** Derivada respecto de la variable elegida. */
    public Resultado derivar(String expresion, char v) {
        Polinomio f = ParserPolinomio.parsear(expresion);
        List<String> pasos = new ArrayList<>();
        pasos.add("1) Función: " + nombreFuncion(f, v) + " = " + f);
        pasos.add("2) Regla de la potencia: d/d" + v + " [c·" + v + "ⁿ] = c·n·" + v
                + "ⁿ⁻¹ (la derivada de una constante es 0).");
        pasos.add("3) Se deriva término a término respecto de " + v + ":");
        for (Termino t : f.getTerminos()) {
            Termino d = t.derivar(v);
            int e = t.exponente(v);
            if (e == 0) {
                pasos.add("   • " + t + "  →  no contiene " + v + ", es constante  →  0");
            } else {
                pasos.add("   • " + t + "  →  (" + t.getCoeficiente() + ")·" + e + "·" + v + "^" + (e - 1)
                        + "  =  " + d);
            }
        }
        Polinomio r = f.derivar(v);
        String etiqueta = f.usaVariable(otra(v)) ? "∂f/∂" + v : "f'(" + v + ")";
        pasos.add("4) Se suman los términos semejantes: " + etiqueta + " = " + r);
        return new Resultado(etiqueta + " = " + r, "", pasos);
    }

    /** Integral indefinida: ∫ f dv = F + C. */
    public Resultado integralIndefinida(String expresion, char v) {
        Polinomio f = ParserPolinomio.parsear(expresion);
        List<String> pasos = new ArrayList<>();
        pasos.add("1) Integrando: " + f + "   (respecto de d" + v + ")");
        pasos.add("2) Regla de la potencia: ∫ c·" + v + "ⁿ d" + v + " = c·" + v + "ⁿ⁺¹/(n+1) + C");
        pasos.add("3) Se integra término a término:");
        for (Termino t : f.getTerminos()) {
            int e = t.exponente(v);
            String nota = t.exponente(otra(v)) > 0 ? "  (" + otra(v) + " se trata como constante)" : "";
            pasos.add("   • " + t + "  →  (" + t.getCoeficiente() + ")·" + v + "^" + (e + 1) + "/" + (e + 1)
                    + "  =  " + t.integrar(v) + nota);
        }
        Polinomio F = f.integrar(v);
        String res = F.esCero() ? "C" : F + " + C";
        pasos.add("4) Se agrega la constante de integración C.");
        return new Resultado("∫ f d" + v + " = " + res, "", pasos);
    }

    /** Integral definida en [a, b]: F(b) − F(a) (Teorema fundamental del cálculo). */
    public Resultado integralDefinida(String expresion, char v, String limInf, String limSup) {
        Polinomio f = ParserPolinomio.parsear(expresion);
        Fraccion a = leerLimite(limInf, "inferior");
        Fraccion b = leerLimite(limSup, "superior");
        List<String> pasos = new ArrayList<>();
        pasos.add("1) Integrando: " + f + "   en el intervalo [" + a + ", " + b + "], variable " + v);
        Polinomio F = f.integrar(v);
        pasos.add("2) Antiderivada (sin C): F(" + v + ") = " + F);
        Polinomio Fb = F.sustituir(v, b);
        Polinomio Fa = F.sustituir(v, a);
        pasos.add("3) Se evalúa en el límite superior: F(" + b + ") = " + Fb);
        pasos.add("4) Se evalúa en el límite inferior: F(" + a + ") = " + Fa);
        Polinomio r = Fb.restar(Fa);
        pasos.add("5) Teorema fundamental del cálculo: ∫ₐᵇ f d" + v + " = F(b) − F(a) = (" + Fb + ") − (" + Fa
                + ") = " + r);
        String detalle = "";
        if (r.esConstante()) {
            Fraccion val = r.valorConstante();
            if (val.getDenominador() != 1) {
                detalle = "≈ " + val.aDecimal();
            }
        }
        return new Resultado("Resultado = " + r, detalle, pasos);
    }

    private Fraccion leerLimite(String texto, String nombre) {
        String t = texto == null ? "" : texto.trim().replace(',', '.');
        if (t.isEmpty()) {
            throw new IllegalArgumentException("Escriba el límite " + nombre + ".");
        }
        try {
            return Fraccion.deTexto(t);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El límite " + nombre + " no es un número válido.");
        }
    }

    private String nombreFuncion(Polinomio f, char v) {
        return f.usaVariable(otra(v)) ? "f(x, y)" : "f(" + v + ")";
    }

    private char otra(char v) {
        return v == 'x' ? 'y' : 'x';
    }
}
