package calculadora.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Número racional inmutable (numerador/denominador) siempre simplificado.
 * Se usa para que la integración sea exacta: por ejemplo ∫x dx = x²/2 y no 0.5x².
 */
public final class Fraccion implements Comparable<Fraccion> {

    public static final Fraccion CERO = new Fraccion(0, 1);
    public static final Fraccion UNO = new Fraccion(1, 1);

    private final long num;
    private final long den;

    public Fraccion(long numerador, long denominador) {
        if (denominador == 0) {
            throw new ArithmeticException("División entre cero.");
        }
        if (denominador < 0) {
            numerador = -numerador;
            denominador = -denominador;
        }
        long g = mcd(Math.abs(numerador), denominador);
        this.num = numerador / g;
        this.den = denominador / g;
    }

    public static Fraccion de(long entero) {
        return new Fraccion(entero, 1);
    }

    /** Convierte texto como "3", "2.5" o "1/3" a fracción. */
    public static Fraccion deTexto(String texto) {
        try {
            if (texto.contains("/")) {
                String[] partes = texto.split("/", -1);
                if (partes.length != 2) {
                    throw new NumberFormatException();
                }
                return decimal(partes[0]).dividir(decimal(partes[1]));
            }
            return decimal(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido: '" + texto + "'.");
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Número inválido o demasiado grande: '" + texto + "'.");
        }
    }

    private static Fraccion decimal(String s) {
        BigDecimal bd = new BigDecimal(s.trim());
        long n = bd.unscaledValue().longValueExact();
        int escala = bd.scale();
        if (escala > 0) {
            return new Fraccion(n, potenciaDiez(escala));
        }
        return new Fraccion(Math.multiplyExact(n, potenciaDiez(-escala)), 1);
    }

    private static long potenciaDiez(int e) {
        long r = 1;
        for (int i = 0; i < e; i++) {
            r = Math.multiplyExact(r, 10L);
        }
        return r;
    }

    private static long mcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a == 0 ? 1 : a;
    }

    public Fraccion sumar(Fraccion o) {
        return new Fraccion(
                Math.addExact(Math.multiplyExact(num, o.den), Math.multiplyExact(o.num, den)),
                Math.multiplyExact(den, o.den));
    }

    public Fraccion restar(Fraccion o) {
        return sumar(o.negar());
    }

    public Fraccion multiplicar(Fraccion o) {
        return new Fraccion(Math.multiplyExact(num, o.num), Math.multiplyExact(den, o.den));
    }

    public Fraccion dividir(Fraccion o) {
        if (o.num == 0) {
            throw new ArithmeticException("División entre cero.");
        }
        return new Fraccion(Math.multiplyExact(num, o.den), Math.multiplyExact(den, o.num));
    }

    public Fraccion negar() {
        return new Fraccion(-num, den);
    }

    public Fraccion abs() {
        return num < 0 ? negar() : this;
    }

    public Fraccion potencia(int exponente) {
        Fraccion r = UNO;
        for (int i = 0; i < exponente; i++) {
            r = r.multiplicar(this);
        }
        return r;
    }

    public boolean esCero() { return num == 0; }
    public boolean esNegativa() { return num < 0; }
    public boolean esUno() { return num == 1 && den == 1; }
    public long getNumerador() { return num; }
    public long getDenominador() { return den; }
    public double aDouble() { return (double) num / den; }

    /** Representación decimal con hasta 6 decimales (sin ceros sobrantes). */
    public String aDecimal() {
        BigDecimal bd = BigDecimal.valueOf(num).divide(BigDecimal.valueOf(den), 6, RoundingMode.HALF_UP);
        return bd.stripTrailingZeros().toPlainString();
    }

    @Override
    public int compareTo(Fraccion o) {
        return Long.compare(Math.multiplyExact(num, o.den), Math.multiplyExact(o.num, den));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Fraccion f && f.num == num && f.den == den;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(num) * 31 + Long.hashCode(den);
    }

    @Override
    public String toString() {
        return den == 1 ? Long.toString(num) : num + "/" + den;
    }
}
