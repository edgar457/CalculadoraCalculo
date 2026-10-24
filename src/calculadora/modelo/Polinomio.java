package calculadora.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Suma de términos. Al construirse reduce términos semejantes, elimina ceros y ordena por grado. */
public final class Polinomio {

    private final List<Termino> terminos;

    public Polinomio(List<Termino> originales) {
        Map<Long, Termino> mapa = new LinkedHashMap<>();
        for (Termino t : originales) {
            long clave = (long) t.getExpX() * 100_000L + t.getExpY();
            Termino previo = mapa.get(clave);
            if (previo == null) {
                mapa.put(clave, t);
            } else {
                mapa.put(clave, new Termino(previo.getCoeficiente().sumar(t.getCoeficiente()),
                        t.getExpX(), t.getExpY()));
            }
        }
        List<Termino> lista = new ArrayList<>();
        for (Termino t : mapa.values()) {
            if (!t.getCoeficiente().esCero()) {
                lista.add(t);
            }
        }
        lista.sort(Comparator.comparingInt(Termino::grado).reversed()
                .thenComparing(Comparator.comparingInt(Termino::getExpX).reversed()));
        this.terminos = Collections.unmodifiableList(lista);
    }

    public List<Termino> getTerminos() { return terminos; }

    public boolean esCero() { return terminos.isEmpty(); }

    public boolean usaVariable(char v) {
        for (Termino t : terminos) {
            if (t.exponente(v) > 0) return true;
        }
        return false;
    }

    public boolean esConstante() {
        for (Termino t : terminos) {
            if (!t.esConstante()) return false;
        }
        return true;
    }

    /** Valor numérico si el polinomio es constante. */
    public Fraccion valorConstante() {
        return terminos.isEmpty() ? Fraccion.CERO : terminos.get(0).getCoeficiente();
    }

    public Polinomio derivar(char v) {
        List<Termino> r = new ArrayList<>();
        for (Termino t : terminos) r.add(t.derivar(v));
        return new Polinomio(r);
    }

    public Polinomio integrar(char v) {
        List<Termino> r = new ArrayList<>();
        for (Termino t : terminos) r.add(t.integrar(v));
        return new Polinomio(r);
    }

    public Polinomio sustituir(char v, Fraccion valor) {
        List<Termino> r = new ArrayList<>();
        for (Termino t : terminos) r.add(t.sustituir(v, valor));
        return new Polinomio(r);
    }

    public Polinomio restar(Polinomio otro) {
        List<Termino> r = new ArrayList<>(terminos);
        for (Termino t : otro.terminos) {
            r.add(new Termino(t.getCoeficiente().negar(), t.getExpX(), t.getExpY()));
        }
        return new Polinomio(r);
    }

    @Override
    public String toString() {
        if (terminos.isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terminos.size(); i++) {
            Termino t = terminos.get(i);
            if (i == 0) {
                sb.append(t.toString());
            } else {
                sb.append(t.getCoeficiente().esNegativa() ? " - " : " + ").append(t.textoSinSigno());
            }
        }
        return sb.toString();
    }
}
