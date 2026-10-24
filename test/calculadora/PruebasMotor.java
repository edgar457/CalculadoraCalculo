package calculadora;

import calculadora.logica.MotorCalculo;
import calculadora.logica.Resultado;

/** Pruebas unitarias sencillas (sin dependencias externas). Ejecutar: java -cp out calculadora.PruebasMotor */
public class PruebasMotor {

    static int ok = 0, fallos = 0;
    static final MotorCalculo m = new MotorCalculo();

    static void esperar(String nombre, String obtenido, String esperado) {
        if (obtenido.equals(esperado)) {
            ok++;
            System.out.println("OK    " + nombre + "  ->  " + obtenido);
        } else {
            fallos++;
            System.out.println("FALLO " + nombre + "  esperado: " + esperado + "  obtenido: " + obtenido);
        }
    }

    static void esperarError(String nombre, Runnable r) {
        try {
            r.run();
            fallos++;
            System.out.println("FALLO " + nombre + " (debía lanzar error)");
        } catch (IllegalArgumentException | ArithmeticException e) {
            ok++;
            System.out.println("OK    " + nombre + "  ->  error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        esperar("d/dx 3x^2+5x", m.derivar("3x^2 + 5x", 'x').resumen(), "f'(x) = 6x + 5");
        esperar("d/dx x^3-4x+7", m.derivar("x^3 - 4x + 7", 'x').resumen(), "f'(x) = 3x² - 4");
        esperar("d/dx constante", m.derivar("9", 'x').resumen(), "f'(x) = 0");
        esperar("d/dy y^3+2y", m.derivar("y^3 + 2y", 'y').resumen(), "f'(y) = 3y² + 2");
        esperar("d/dx 2xy+y^3", m.derivar("2xy + y^3", 'x').resumen(), "∂f/∂x = 2y");
        esperar("d/dy 2xy+y^3", m.derivar("2xy + y^3", 'y').resumen(), "∂f/∂y = 3y² + 2x");
        esperar("términos semejantes", m.derivar("x^2 + 3x^2", 'x').resumen(), "f'(x) = 8x");
        esperar("superíndices", m.derivar("3x² + 5x", 'x').resumen(), "f'(x) = 6x + 5");
        esperar("∫ 2x dx", m.integralIndefinida("2x", 'x').resumen(), "∫ f dx = x² + C");
        esperar("∫ x^2 dx", m.integralIndefinida("x^2", 'x').resumen(), "∫ f dx = x³/3 + C");
        esperar("∫ 3x^2+5x dx", m.integralIndefinida("3x^2 + 5x", 'x').resumen(), "∫ f dx = x³ + 5x²/2 + C");
        esperar("∫ 2xy dy", m.integralIndefinida("2xy", 'y').resumen(), "∫ f dy = xy² + C");
        esperar("∫ 4 dx", m.integralIndefinida("4", 'x').resumen(), "∫ f dx = 4x + C");
        esperar("∫₀² 2x dx", m.integralDefinida("2x", 'x', "0", "2").resumen(), "Resultado = 4");
        esperar("∫₁³ x^2 dx", m.integralDefinida("x^2", 'x', "1", "3").resumen(), "Resultado = 26/3");
        esperar("∫₁³ x^2 dx decimal", m.integralDefinida("x^2", 'x', "1", "3").detalle(), "≈ 8.666667");
        esperar("∫₋₁¹ x^3 dx", m.integralDefinida("x^3", 'x', "-1", "1").resumen(), "Resultado = 0");
        esperar("∫₀¹ 3x^2+5x dx", m.integralDefinida("3x^2 + 5x", 'x', "0", "1").resumen(), "Resultado = 7/2");
        esperar("límites invertidos", m.integralDefinida("2x", 'x', "2", "0").resumen(), "Resultado = -4");
        esperar("límites fraccionarios", m.integralDefinida("2x", 'x', "0", "1/2").resumen(), "Resultado = 1/4");
        esperar("def. con otra variable", m.integralDefinida("2xy", 'x', "0", "3").resumen(), "Resultado = 9y");
        esperarError("expresión vacía", () -> m.derivar("  ", 'x'));
        esperarError("carácter inválido", () -> m.derivar("3z + 1", 'x'));
        esperarError("paréntesis", () -> m.derivar("(x+1)^2", 'x'));
        esperarError("exponente negativo", () -> m.derivar("x^-2", 'x'));
        esperarError("signo colgante", () -> m.derivar("3x +", 'x'));
        esperarError("límite vacío", () -> m.integralDefinida("x", 'x', "", "1"));
        esperarError("límite inválido", () -> m.integralDefinida("x", 'x', "a", "1"));
        System.out.println();
        System.out.println("Pruebas correctas: " + ok + "   Fallidas: " + fallos);
        if (fallos > 0) System.exit(1);
    }
}
