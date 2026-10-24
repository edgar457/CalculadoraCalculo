package calculadora.gui;

import calculadora.logica.MotorCalculo;
import calculadora.logica.ParserPolinomio;
import calculadora.logica.Resultado;
import calculadora.modelo.Fraccion;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

/** Interfaz gráfica (Swing) de la calculadora de cálculo. Usa {@link MotorCalculo} para operar. */
public class PanelCalculadora extends JPanel {

    private static final Color FONDO = new Color(0x0F1117);
    private static final Color TARJETA_DER = new Color(0x161C2E);
    private static final Color TARJETA_INT = new Color(0x241B14);
    private static final Color BORDE_DER = new Color(0x3A4A8A);
    private static final Color BORDE_INT = new Color(0x8A5A2A);
    private static final Color TEXTO = new Color(0xE6E9F2);
    private static final Color TENUE = new Color(0x8D93A8);
    private static final Color NARANJA = new Color(0xF59E42);
    private static final Color AZUL = new Color(0x6C8CFF);
    private static final Color CAMPO = new Color(0x0B0D13);
    private static final Color ERROR = new Color(0xFF6B6B);

    private final MotorCalculo motor = new MotorCalculo();

    // Derivada
    private final JComboBox<String> varDerivada = new JComboBox<>(new String[]{"x", "y"});
    private final JLabel etiquetaFuncion = new JLabel("f(x) =");
    private final JTextField campoDerivada = new JTextField();
    private final JLabel resultadoDerivada = new JLabel(" ");

    // Integral
    private final JComboBox<String> varIntegral = new JComboBox<>(new String[]{"x", "y"});
    private final JRadioButton rbIndefinida = new JRadioButton("Indefinida", true);
    private final JRadioButton rbDefinida = new JRadioButton("Definida");
    private final JTextField campoIntegral = new JTextField();
    private final JTextField campoA = new JTextField("0", 5);
    private final JTextField campoB = new JTextField("2", 5);
    private final JLabel etiquetaDx = new JLabel("dx");
    private final JLabel resultadoIntegral = new JLabel(" ");

    private final JTextArea areaPasos = new JTextArea(6, 40);
    private final PanelGrafica grafica = new PanelGrafica();
    private VentanaAyuda ventanaAyuda;
    /** Campo de texto que recibe los caracteres del teclado de ayuda (el último que tuvo el foco). */
    private JTextField campoActivo = campoDerivada;

    public PanelCalculadora() {
        setLayout(new BorderLayout(0, 12));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearCentro(), BorderLayout.CENTER);
        add(crearPasos(), BorderLayout.SOUTH);
        configurarEventos();
        actualizarEtiquetas();
        setPreferredSize(new Dimension(980, 900));
    }

    // ---------------------------------------------------------------- construcción de la interfaz

    private JComponent crearEncabezado() {
        JPanel p = new JPanel(new GridLayout(0, 1, 0, 2));
        p.setOpaque(false);
        JLabel titulo = new JLabel("∫ Calculadora de Cálculo (Integrales y Derivadas)");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titulo.setForeground(TEXTO);
        JLabel sub = new JLabel("Polinomios en x e y · Derivadas e integrales simples y definidas");
        sub.setForeground(TENUE);
        p.add(titulo);
        p.add(sub);
        return p;
    }

    private JComponent crearCentro() {
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 7);
        centro.add(crearTarjetaDerivada(), c);
        c.gridx = 1;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 7);
        centro.add(crearTeclado(), c);
        c.gridx = 2;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        centro.add(crearTarjetaIntegral(), c);
        return centro;
    }

    /** Teclado de ayuda para escribir ecuaciones: potencias, superíndices y operadores. */
    private JComponent crearTeclado() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(new Color(0x12151F));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x2A3040), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel titulo = etiqueta("ESCRITURA", TENUE);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        titulo.setHorizontalAlignment(JLabel.CENTER);
        p.add(titulo, BorderLayout.NORTH);
        p.setPreferredSize(new Dimension(170, 340));

        JPanel rejilla = new JPanel(new GridLayout(0, 3, 4, 4));
        rejilla.setOpaque(false);
        String[][] teclas = {
                {"x", "x"}, {"y", "y"}, {"^", "^"},
                {"x²", "x²"}, {"x³", "x³"}, {"xⁿ", "x^"},
                {"²", "²"}, {"³", "³"}, {"⁴", "⁴"},
                {"7", "7"}, {"8", "8"}, {"9", "9"},
                {"4", "4"}, {"5", "5"}, {"6", "6"},
                {"1", "1"}, {"2", "2"}, {"3", "3"},
                {"0", "0"}, {".", "."}, {"/", "/"},
                {"+", " + "}, {"−", " - "}, {"·", "*"},
        };
        for (String[] t : teclas) {
            rejilla.add(teclaTexto(t[0], t[1]));
        }
        JButton borrar = teclaBase("⌫", new Color(0x3A2A2A));
        borrar.setToolTipText("Borrar el carácter anterior");
        borrar.addActionListener(e -> borrarCaracter());
        JButton limpiarCampo = teclaBase("CE", new Color(0x3A2A2A));
        limpiarCampo.setToolTipText("Borrar todo el campo");
        limpiarCampo.addActionListener(e -> {
            campoActivo.setText("");
            campoActivo.requestFocusInWindow();
        });
        JButton izq = teclaBase("←", new Color(0x252B3D));
        izq.addActionListener(e -> moverCursor(-1));
        JButton der = teclaBase("→", new Color(0x252B3D));
        der.addActionListener(e -> moverCursor(1));
        JPanel edicion = new JPanel(new GridLayout(1, 4, 4, 0));
        edicion.setOpaque(false);
        edicion.add(izq);
        edicion.add(der);
        edicion.add(borrar);
        edicion.add(limpiarCampo);
        JPanel cuerpo = new JPanel(new BorderLayout(0, 6));
        cuerpo.setOpaque(false);
        cuerpo.add(rejilla, BorderLayout.CENTER);
        cuerpo.add(edicion, BorderLayout.SOUTH);
        p.add(cuerpo, BorderLayout.CENTER);

        JLabel pie = etiqueta("<html><center>Escribe en el<br>último campo<br>seleccionado</center></html>", TENUE);
        pie.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        pie.setHorizontalAlignment(JLabel.CENTER);
        p.add(pie, BorderLayout.SOUTH);
        return p;
    }

    private JButton teclaBase(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setFocusable(false);           // así el campo de texto conserva el foco y el cursor
        b.setFocusPainted(false);
        b.setBackground(fondo);
        b.setForeground(TEXTO);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(7, 4, 7, 4));
        return b;
    }

    private JButton teclaTexto(String rotulo, String insertar) {
        JButton b = teclaBase(rotulo, new Color(0x252B3D));
        b.addActionListener(e -> insertar(insertar));
        return b;
    }

    private void insertar(String texto) {
        campoActivo.replaceSelection(texto);
        campoActivo.requestFocusInWindow();
    }

    private void borrarCaracter() {
        JTextField f = campoActivo;
        try {
            Document d = f.getDocument();
            if (f.getSelectionStart() != f.getSelectionEnd()) {
                f.replaceSelection("");
            } else {
                int pos = f.getCaretPosition();
                if (pos > 0) d.remove(pos - 1, 1);
            }
        } catch (BadLocationException ignorada) {
            // no ocurre: la posición se valida antes
        }
        f.requestFocusInWindow();
    }

    private void moverCursor(int delta) {
        int pos = Math.max(0, Math.min(campoActivo.getText().length(), campoActivo.getCaretPosition() + delta));
        campoActivo.setCaretPosition(pos);
        campoActivo.requestFocusInWindow();
    }

    private JPanel tarjeta(Color fondo, Color borde) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(fondo);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        return p;
    }

    private GridBagConstraints gbc(int x, int y, int ancho) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = ancho;
        c.insets = new Insets(4, 3, 4, 3);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        return c;
    }

    private JComponent crearTarjetaDerivada() {
        JPanel p = tarjeta(TARJETA_DER, BORDE_DER);
        p.add(etiqueta("DERIVADA · VARIABLE:", AZUL), gbc(0, 0, 1));
        estiloCombo(varDerivada);
        GridBagConstraints c = gbc(1, 0, 1);
        c.weightx = 0;
        p.add(varDerivada, c);

        etiquetaFuncion.setForeground(TEXTO);
        etiquetaFuncion.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        c = gbc(0, 1, 1);
        c.weightx = 0;
        p.add(etiquetaFuncion, c);
        estiloCampo(campoDerivada);
        campoDerivada.setText("3x^2 + 5x");
        p.add(campoDerivada, gbc(1, 1, 1));

        JButton b = boton("Derivar", AZUL);
        b.addActionListener(e -> derivar());
        p.add(b, gbc(0, 2, 2));
        resultadoDerivada.setForeground(TEXTO);
        resultadoDerivada.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        p.add(resultadoDerivada, gbc(0, 3, 2));

        JLabel ayuda = etiqueta("<html>Ejemplos: 3x^2+5x · 2xy+y^3 · x^4-x/2 · 0.5x^2</html>", TENUE);
        p.add(ayuda, gbc(0, 4, 2));
        GridBagConstraints relleno = gbc(0, 5, 2);
        relleno.weighty = 1;
        relleno.fill = GridBagConstraints.BOTH;
        p.add(new JLabel(), relleno);
        return p;
    }

    private JComponent crearTarjetaIntegral() {
        JPanel p = tarjeta(TARJETA_INT, BORDE_INT);
        p.add(etiqueta("INTEGRAL · VARIABLE:", NARANJA), gbc(0, 0, 2));
        estiloCombo(varIntegral);
        GridBagConstraints c = gbc(2, 0, 2);
        c.weightx = 0;
        p.add(varIntegral, c);

        ButtonGroup grupo = new ButtonGroup();
        for (JRadioButton rb : new JRadioButton[]{rbIndefinida, rbDefinida}) {
            rb.setOpaque(false);
            rb.setForeground(TEXTO);
            rb.setFocusPainted(false);
            grupo.add(rb);
        }
        p.add(rbIndefinida, gbc(0, 1, 2));
        p.add(rbDefinida, gbc(2, 1, 2));

        p.add(etiqueta("a =", TENUE), anchoFijo(gbc(0, 2, 1)));
        estiloCampo(campoA);
        p.add(campoA, gbc(1, 2, 1));
        p.add(etiqueta("b =", TENUE), anchoFijo(gbc(2, 2, 1)));
        estiloCampo(campoB);
        p.add(campoB, gbc(3, 2, 1));

        JLabel signo = new JLabel("∫");
        signo.setForeground(NARANJA);
        signo.setFont(new Font(Font.SERIF, Font.PLAIN, 26));
        p.add(signo, anchoFijo(gbc(0, 3, 1)));
        estiloCampo(campoIntegral);
        campoIntegral.setText("2x");
        p.add(campoIntegral, gbc(1, 3, 2));
        etiquetaDx.setForeground(TEXTO);
        etiquetaDx.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        p.add(etiquetaDx, anchoFijo(gbc(3, 3, 1)));

        JButton b = boton("Integrar", NARANJA);
        b.addActionListener(e -> integrar());
        p.add(b, gbc(0, 4, 4));
        resultadoIntegral.setForeground(NARANJA);
        resultadoIntegral.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        p.add(resultadoIntegral, gbc(0, 5, 4));
        GridBagConstraints relleno = gbc(0, 6, 4);
        relleno.weighty = 1;
        relleno.fill = GridBagConstraints.BOTH;
        p.add(new JLabel(), relleno);
        return p;
    }

    private GridBagConstraints anchoFijo(GridBagConstraints c) {
        c.weightx = 0;
        return c;
    }

    private JComponent crearPasos() {
        areaPasos.setEditable(false);
        areaPasos.setBackground(CAMPO);
        areaPasos.setForeground(new Color(0xC9D1E8));
        areaPasos.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaPasos.setMargin(new Insets(8, 10, 8, 10));
        areaPasos.setLineWrap(true);
        areaPasos.setWrapStyleWord(true);
        areaPasos.setText("Aquí aparecerá el procedimiento paso a paso.");
        JScrollPane sp = new JScrollPane(areaPasos);
        sp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0x2A3040)), " Procedimiento paso a paso ",
                0, 0, null, TENUE));
        sp.setBackground(FONDO);
        sp.setPreferredSize(new Dimension(100, 150));

        JButton limpiar = boton("Limpiar", TENUE);
        limpiar.addActionListener(e -> limpiar());
        JButton ejemplo = boton("Cargar ejemplo", NARANJA);
        ejemplo.addActionListener(e -> cargarEjemplo());
        JButton ayuda = boton("Ayuda", AZUL);
        ayuda.addActionListener(e -> mostrarAyuda());
        JPanel botones = new JPanel(new GridLayout(1, 0, 8, 0));
        botones.setOpaque(false);
        botones.add(ejemplo);
        botones.add(limpiar);
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.add(ayuda, BorderLayout.WEST);
        fila.add(botones, BorderLayout.EAST);

        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        JPanel zonaGrafica = new JPanel(new BorderLayout());
        zonaGrafica.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0x2A3040)), " Gráfica ", 0, 0, null, TENUE));
        zonaGrafica.setBackground(FONDO);
        zonaGrafica.add(grafica, BorderLayout.CENTER);

        JPanel medio = new JPanel(new BorderLayout(0, 8));
        medio.setOpaque(false);
        medio.add(sp, BorderLayout.NORTH);
        medio.add(zonaGrafica, BorderLayout.CENTER);

        p.add(medio, BorderLayout.CENTER);
        p.add(fila, BorderLayout.SOUTH);
        return p;
    }

    // ---------------------------------------------------------------- estilos

    private JLabel etiqueta(String texto, Color color) {
        JLabel l = new JLabel(texto);
        l.setForeground(color);
        return l;
    }

    private void estiloCampo(JTextField f) {
        f.setBackground(CAMPO);
        f.setForeground(TEXTO);
        f.setCaretColor(TEXTO);
        f.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x2A3040)),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
    }

    private void estiloCombo(JComboBox<String> c) {
        c.setBackground(CAMPO);
        c.setForeground(TEXTO);
        c.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setBackground(color);
        b.setForeground(color == TENUE ? Color.WHITE : new Color(0x10131C));
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return b;
    }

    // ---------------------------------------------------------------- eventos y acciones

    private void configurarEventos() {
        varDerivada.addActionListener(e -> actualizarEtiquetas());
        varIntegral.addActionListener(e -> actualizarEtiquetas());
        rbIndefinida.addActionListener(e -> actualizarEtiquetas());
        rbDefinida.addActionListener(e -> actualizarEtiquetas());
        for (JTextField f : new JTextField[]{campoDerivada, campoIntegral, campoA, campoB}) {
            f.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent ev) {
                    campoActivo = f;
                }
            });
        }
        campoDerivada.addActionListener(e -> derivar());
        campoIntegral.addActionListener(e -> integrar());
    }

    private void actualizarEtiquetas() {
        etiquetaFuncion.setText("f(" + varDerivada.getSelectedItem() + ") =");
        etiquetaDx.setText("d" + varIntegral.getSelectedItem());
        boolean definida = rbDefinida.isSelected();
        campoA.setEnabled(definida);
        campoB.setEnabled(definida);
    }

    private void derivar() {
        char v = ((String) varDerivada.getSelectedItem()).charAt(0);
        try {
            Resultado r = motor.derivar(campoDerivada.getText(), v);
            resultadoDerivada.setForeground(TEXTO);
            resultadoDerivada.setText(r.resumen());
            areaPasos.setText(r.pasosComoTexto());
            grafica.mostrarDerivada(ParserPolinomio.parsear(campoDerivada.getText()), v);
        } catch (IllegalArgumentException | ArithmeticException ex) {
            mostrarError(resultadoDerivada, ex.getMessage());
        }
        areaPasos.setCaretPosition(0);
    }

    private void integrar() {
        char v = ((String) varIntegral.getSelectedItem()).charAt(0);
        try {
            Resultado r = rbDefinida.isSelected()
                    ? motor.integralDefinida(campoIntegral.getText(), v, campoA.getText(), campoB.getText())
                    : motor.integralIndefinida(campoIntegral.getText(), v);
            resultadoIntegral.setForeground(NARANJA);
            resultadoIntegral.setText(r.detalle().isEmpty() ? r.resumen() : r.resumen() + "  " + r.detalle());
            areaPasos.setText(r.pasosComoTexto());
            boolean def = rbDefinida.isSelected();
            grafica.mostrarIntegral(ParserPolinomio.parsear(campoIntegral.getText()), v,
                    def ? Fraccion.deTexto(campoA.getText().trim().replace(',', '.')) : null,
                    def ? Fraccion.deTexto(campoB.getText().trim().replace(',', '.')) : null);
        } catch (IllegalArgumentException | ArithmeticException ex) {
            mostrarError(resultadoIntegral, ex.getMessage());
        }
        areaPasos.setCaretPosition(0);
    }

    /** Abre (o trae al frente) la ventana de ayuda con el manual de usuario. */
    private void mostrarAyuda() {
        if (ventanaAyuda == null || !ventanaAyuda.isDisplayable()) {
            ventanaAyuda = new VentanaAyuda(SwingUtilities.getWindowAncestor(this));
        }
        ventanaAyuda.setVisible(true);
        ventanaAyuda.toFront();
    }

    private void mostrarError(JLabel etiqueta, String mensaje) {
        etiqueta.setForeground(ERROR);
        etiqueta.setText("<html>⚠ " + mensaje + "</html>");
        areaPasos.setText("No se pudo calcular: " + mensaje);
    }

    public void limpiar() {
        campoDerivada.setText("");
        campoIntegral.setText("");
        resultadoDerivada.setText(" ");
        resultadoIntegral.setText(" ");
        grafica.limpiar();
        areaPasos.setText("Aquí aparecerá el procedimiento paso a paso.");
    }

    /** Carga los ejemplos del enunciado: f(x)=3x²+5x y ∫₀² 2x dx. */
    public void cargarEjemplo() {
        varDerivada.setSelectedItem("x");
        varIntegral.setSelectedItem("x");
        campoDerivada.setText("3x^2 + 5x");
        campoIntegral.setText("2x");
        rbDefinida.setSelected(true);
        campoA.setText("0");
        campoB.setText("2");
        actualizarEtiquetas();
        derivar();
        integrar();
    }
}
