package calculadora.gui;

import calculadora.modelo.Fraccion;
import calculadora.modelo.Polinomio;
import calculadora.modelo.Termino;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

/**
 * Plano cartesiano donde se grafica la función y su derivada, o el integrando y su antiderivada
 * (con el área sombreada si la integral es definida). Si la función tiene otra variable,
 * esta se evalúa en 1.
 */
public class PanelGrafica extends JPanel {

    private static final Color FONDO = new Color(0x0B0D13);
    private static final Color REJILLA = new Color(0x1B2030);
    private static final Color EJES = new Color(0x6B7390);
    private static final Color TEXTO = new Color(0x8D93A8);
    private static final Color AZUL = new Color(0x6C8CFF);
    private static final Color NARANJA = new Color(0xF59E42);
    private static final Color VERDE = new Color(0x5FD4A0);

    private Polinomio principal;      // f
    private Polinomio secundaria;     // f' o F
    private String nombrePrincipal = "";
    private String nombreSecundaria = "";
    private Color colorPrincipal = AZUL;
    private Color colorSecundaria = VERDE;
    private char variable = 'x';
    private double a, b;
    private boolean sombrear;
    private String nota = "";

    public PanelGrafica() {
        setBackground(FONDO);
        setPreferredSize(new Dimension(100, 250));
    }

    // ------------------------------------------------------------------ API

    public void mostrarDerivada(Polinomio f, char v) {
        principal = f;
        secundaria = f.derivar(v);
        variable = v;
        nombrePrincipal = f.usaVariable(otra(v)) ? "f(x, y)" : "f(" + v + ")";
        nombreSecundaria = f.usaVariable(otra(v)) ? "∂f/∂" + v : "f'(" + v + ")";
        colorPrincipal = AZUL;
        colorSecundaria = VERDE;
        sombrear = false;
        nota = f.usaVariable(otra(v)) ? otra(v) + " = 1" : "";
        repaint();
    }

    public void mostrarIntegral(Polinomio f, char v, Fraccion limInf, Fraccion limSup) {
        principal = f;
        secundaria = f.integrar(v);
        variable = v;
        nombrePrincipal = f.usaVariable(otra(v)) ? "f(x, y)" : "f(" + v + ")";
        nombreSecundaria = "F(" + v + ") (C = 0)";
        colorPrincipal = NARANJA;
        colorSecundaria = VERDE;
        sombrear = limInf != null && limSup != null;
        if (sombrear) {
            a = limInf.aDouble();
            b = limSup.aDouble();
        }
        nota = f.usaVariable(otra(v)) ? otra(v) + " = 1" : "";
        repaint();
    }

    public void limpiar() {
        principal = null;
        secundaria = null;
        repaint();
    }

    // ------------------------------------------------------------------ evaluación

    private char otra(char v) {
        return v == 'x' ? 'y' : 'x';
    }

    private double evaluar(Polinomio p, double t) {
        double suma = 0;
        for (Termino term : p.getTerminos()) {
            double valor = term.getCoeficiente().aDouble();
            int ev = term.exponente(variable);
            int eo = term.exponente(otra(variable));
            valor *= Math.pow(t, ev);
            // la otra variable vale 1, así que no cambia el valor
            if (eo < 0) valor = Double.NaN;
            suma += valor;
        }
        return suma;
    }

    // ------------------------------------------------------------------ dibujo

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g.setColor(FONDO);
        g.fillRect(0, 0, w, h);

        if (principal == null) {
            dibujarVacio(g, w, h);
            g.dispose();
            return;
        }

        // Rango de x
        double x0 = -5, x1 = 5;
        if (sombrear) {
            double lo = Math.min(a, b), hi = Math.max(a, b);
            double margen = Math.max(1.5, (hi - lo) * 0.35);
            x0 = Math.min(-1, lo - margen);
            x1 = Math.max(1, hi + margen);
            if (lo >= 0) x0 = Math.min(x0, -(hi - lo) * 0.15 - 0.5);
        }

        // Muestreo y rango de y
        int n = Math.max(2, w);
        double[] ys1 = new double[n + 1];
        double[] ys2 = new double[n + 1];
        double ymin = 0, ymax = 0;
        for (int i = 0; i <= n; i++) {
            double t = x0 + (x1 - x0) * i / n;
            ys1[i] = evaluar(principal, t);
            ys2[i] = evaluar(secundaria, t);
        }
        // Se calcula el rango con percentiles recortados para que un crecimiento muy rápido no aplaste el resto.
        double[] todos = new double[2 * (n + 1)];
        System.arraycopy(ys1, 0, todos, 0, n + 1);
        System.arraycopy(ys2, 0, todos, n + 1, n + 1);
        for (double v : todos) {
            if (Double.isFinite(v)) {
                ymin = Math.min(ymin, v);
                ymax = Math.max(ymax, v);
            }
        }
        double tope = 6;
        ymax = Math.min(ymax, tope * Math.max(1, rangoTipico(todos)));
        ymin = Math.max(ymin, -tope * Math.max(1, rangoTipico(todos)));
        if (ymax - ymin < 1e-9) {
            ymax += 1;
            ymin -= 1;
        }
        double pad = (ymax - ymin) * 0.08;
        ymax += pad;
        ymin -= pad;
        if (ymax < 1) ymax = 1;
        if (ymin > -1) ymin = -1;

        int izq = 44, der = 14, arr = 14, aba = 26;
        int pw = w - izq - der;
        int ph = h - arr - aba;
        if (pw < 20 || ph < 20) {
            g.dispose();
            return;
        }
        Escala e = new Escala(x0, x1, ymin, ymax, izq, arr, pw, ph);

        dibujarRejilla(g, e);

        g.setClip(izq, arr, pw, ph);
        if (sombrear) sombrearArea(g, e, ys1, n, x0, x1);
        dibujarCurva(g, e, ys2, n, x0, x1, colorSecundaria, true);
        dibujarCurva(g, e, ys1, n, x0, x1, colorPrincipal, false);
        if (sombrear) {
            g.setColor(new Color(0xF59E42));
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                    new float[]{3f, 3f}, 0f));
            int xa = e.px(Math.min(a, b));
            int xb = e.px(Math.max(a, b));
            g.drawLine(xa, arr, xa, arr + ph);
            g.drawLine(xb, arr, xb, arr + ph);
        }
        g.setClip(null);

        dibujarLeyenda(g, izq + 8, arr + 6);
        g.dispose();
    }

    private double rangoTipico(double[] vals) {
        // mediana de |valores| como referencia de escala
        double[] abs = new double[vals.length];
        int k = 0;
        for (double v : vals) if (Double.isFinite(v)) abs[k++] = Math.abs(v);
        if (k == 0) return 1;
        java.util.Arrays.sort(abs, 0, k);
        return abs[k / 2] + 1e-9;
    }

    private void dibujarVacio(Graphics2D g, int w, int h) {
        g.setColor(TEXTO);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        String msg = "La gráfica aparecerá aquí al derivar o integrar.";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
    }

    private void dibujarRejilla(Graphics2D g, Escala e) {
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();
        double px = paso(e.x1 - e.x0, e.pw / 70);
        double py = paso(e.y1 - e.y0, e.ph / 40);
        g.setStroke(new BasicStroke(1f));

        for (double x = Math.ceil(e.x0 / px) * px; x <= e.x1 + 1e-9; x += px) {
            int sx = e.px(x);
            g.setColor(REJILLA);
            g.drawLine(sx, e.top, sx, e.top + e.ph);
            if (Math.abs(x) > px / 2) {
                g.setColor(TEXTO);
                String s = formato(x);
                g.drawString(s, sx - fm.stringWidth(s) / 2, e.top + e.ph + 15);
            }
        }
        for (double y = Math.ceil(e.y0 / py) * py; y <= e.y1 + 1e-9; y += py) {
            int sy = e.py(y);
            g.setColor(REJILLA);
            g.drawLine(e.left, sy, e.left + e.pw, sy);
            if (Math.abs(y) > py / 2) {
                g.setColor(TEXTO);
                String s = formato(y);
                g.drawString(s, e.left - 6 - fm.stringWidth(s), sy + 4);
            }
        }
        // ejes
        g.setColor(EJES);
        g.setStroke(new BasicStroke(1.4f));
        int ox = e.px(0), oy = e.py(0);
        g.drawLine(e.left, oy, e.left + e.pw, oy);
        g.drawLine(ox, e.top, ox, e.top + e.ph);
        g.setColor(TEXTO);
        g.drawString("0", ox - 10, oy + 14);
        g.drawString(String.valueOf(variable), e.left + e.pw - 8, oy - 5);
        g.drawString("y", ox + 6, e.top + 11);
    }

    private void sombrearArea(Graphics2D g, Escala e, double[] ys, int n, double x0, double x1) {
        double lo = Math.min(a, b), hi = Math.max(a, b);
        Path2D.Double p = new Path2D.Double();
        boolean abierto = false;
        double ultimoX = lo;
        int pasos = 300;
        for (int i = 0; i <= pasos; i++) {
            double t = lo + (hi - lo) * i / pasos;
            double v = evaluar(principal, t);
            if (!Double.isFinite(v)) continue;
            if (!abierto) {
                p.moveTo(e.px(t), e.py(0));
                abierto = true;
            }
            p.lineTo(e.px(t), e.py(v));
            ultimoX = t;
        }
        if (abierto) {
            p.lineTo(e.px(ultimoX), e.py(0));
            p.closePath();
            g.setColor(new Color(0xF59E42, false));
            g.setColor(new Color(245, 158, 66, 70));
            g.fill(p);
        }
    }

    private void dibujarCurva(Graphics2D g, Escala e, double[] ys, int n, double x0, double x1,
                              Color color, boolean discontinua) {
        g.setColor(color);
        g.setStroke(discontinua
                ? new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{7f, 5f}, 0f)
                : new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D.Double path = new Path2D.Double();
        boolean pen = false;
        for (int i = 0; i <= n; i++) {
            double t = x0 + (x1 - x0) * i / n;
            double v = ys[i];
            if (!Double.isFinite(v) || Math.abs(v) > 1e6) {
                pen = false;
                continue;
            }
            double sx = e.px(t);
            double sy = e.py(v);
            if (!pen) {
                path.moveTo(sx, sy);
                pen = true;
            } else {
                path.lineTo(sx, sy);
            }
        }
        g.draw(path);
    }

    private void dibujarLeyenda(Graphics2D g, int x, int y) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        String t1 = nombrePrincipal;
        String t2 = nombreSecundaria;
        String extra = nota.isEmpty() ? "" : "   (" + nota + ")";
        int ancho = 30 + fm.stringWidth(t1) + 30 + fm.stringWidth(t2) + fm.stringWidth(extra) + 24;
        g.setColor(new Color(11, 13, 19, 215));
        g.fillRoundRect(x - 6, y - 4, ancho, 22, 8, 8);
        int cx = x;
        g.setStroke(new BasicStroke(2.4f));
        g.setColor(colorPrincipal);
        g.drawLine(cx, y + 7, cx + 18, y + 7);
        g.drawString(t1, cx + 24, y + 12);
        cx += 24 + fm.stringWidth(t1) + 16;
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f,
                new float[]{5f, 3f}, 0f));
        g.setColor(colorSecundaria);
        g.drawLine(cx, y + 7, cx + 18, y + 7);
        g.drawString(t2, cx + 24, y + 12);
        if (!extra.isEmpty()) {
            g.setColor(TEXTO);
            g.drawString(extra, cx + 24 + fm.stringWidth(t2), y + 12);
        }
    }

    // ------------------------------------------------------------------ utilidades

    /** Paso "bonito" (1, 2, 5 × 10^k) para que haya aproximadamente {@code marcas} divisiones. */
    private static double paso(double rango, double marcas) {
        double bruto = rango / Math.max(2, marcas);
        double pot = Math.pow(10, Math.floor(Math.log10(bruto)));
        double f = bruto / pot;
        double m = f < 1.5 ? 1 : f < 3.5 ? 2 : f < 7.5 ? 5 : 10;
        return m * pot;
    }

    private static String formato(double v) {
        if (Math.abs(v - Math.rint(v)) < 1e-9 && Math.abs(v) < 1e7) return String.valueOf((long) Math.rint(v));
        return new java.math.BigDecimal(v).round(new java.math.MathContext(3)).stripTrailingZeros().toPlainString();
    }

    private static final class Escala {
        final double x0, x1, y0, y1;
        final int left, top, pw, ph;

        Escala(double x0, double x1, double y0, double y1, int left, int top, int pw, int ph) {
            this.x0 = x0;
            this.x1 = x1;
            this.y0 = y0;
            this.y1 = y1;
            this.left = left;
            this.top = top;
            this.pw = pw;
            this.ph = ph;
        }

        int px(double x) { return left + (int) Math.round((x - x0) / (x1 - x0) * pw); }

        int py(double y) { return top + (int) Math.round((y1 - y) / (y1 - y0) * ph); }
    }
}
