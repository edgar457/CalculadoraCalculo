package calculadora.logica;

import java.util.List;

/** Resultado de una operación: línea final para mostrar, detalle opcional y el procedimiento. */
public record Resultado(String resumen, String detalle, List<String> pasos) {

    public String pasosComoTexto() {
        return String.join(System.lineSeparator(), pasos);
    }
}
