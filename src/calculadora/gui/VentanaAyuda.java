package calculadora.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

/** Ventana de ayuda: manual de usuario dividido en pestañas (una por cada parte de la calculadora). */
public class VentanaAyuda extends JDialog {

    private static final Color FONDO = new Color(0x0F1117);
    private static final Color TEXTO = new Color(0xE6E9F2);

    public VentanaAyuda(Window padre) {
        super(padre, "Ayuda — Manual de usuario", ModalityType.MODELESS);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        pestanas.addTab("General", pagina(GENERAL));
        pestanas.addTab("Derivadas", pagina(DERIVADAS));
        pestanas.addTab("Integrales", pagina(INTEGRALES));
        pestanas.addTab("Escritura", pagina(ESCRITURA));
        pestanas.addTab("Gráfica", pagina(GRAFICA));
        pestanas.addTab("Procedimiento y botones", pagina(BOTONES));
        pestanas.addTab("Errores", pagina(ERRORES));

        JButton cerrar = new JButton("Cerrar");
        cerrar.setFocusPainted(false);
        cerrar.addActionListener(e -> dispose());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pie.setBackground(FONDO);
        pie.add(cerrar);

        getContentPane().setBackground(FONDO);
        getContentPane().add(pestanas, BorderLayout.CENTER);
        getContentPane().add(pie, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(760, 600));
        pack();
        setLocationRelativeTo(padre);
    }

    private Component pagina(String cuerpo) {
        JEditorPane panel = new JEditorPane();
        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet css = new StyleSheet();
        css.addRule("body { font-family: SansSerif; font-size: 13pt; color: #E6E9F2; margin: 14px; }");
        css.addRule("h1 { color: #FFFFFF; font-size: 19pt; }");
        css.addRule("h2 { color: #6C8CFF; font-size: 15pt; }");
        css.addRule("b { color: #FFFFFF; }");
        css.addRule("code { font-family: Monospaced; color: #F59E42; }");
        css.addRule("td { padding: 4px 10px; }");
        css.addRule("th { color: #8D93A8; text-align: left; padding: 4px 10px; }");
        css.addRule("li { margin-bottom: 4px; }");
        kit.setStyleSheet(css);
        panel.setEditorKit(kit);
        panel.setContentType("text/html");
        panel.setEditable(false);
        panel.setBackground(FONDO);
        panel.setForeground(TEXTO);
        panel.setText("<html><body>" + cuerpo + "</body></html>");
        panel.setCaretPosition(0);
        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // ------------------------------------------------------------------ contenido

    private static final String GENERAL = """
            <h1>¿Qué hace esta calculadora?</h1>
            <p>Calcula <b>derivadas</b> e <b>integrales</b> (indefinidas y definidas) de <b>polinomios</b>
            en las variables <code>x</code> e <code>y</code>, muestra el <b>procedimiento paso a paso</b>
            y dibuja la <b>gráfica</b> en un plano cartesiano.</p>
            <h2>Partes de la ventana</h2>
            <table>
              <tr><th>Zona</th><th>Para qué sirve</th></tr>
              <tr><td><b>Derivada</b> (izquierda, azul)</td><td>Escribir una función y obtener su derivada.</td></tr>
              <tr><td><b>Escritura</b> (centro)</td><td>Teclado de ayuda para escribir potencias y símbolos.</td></tr>
              <tr><td><b>Integral</b> (derecha, naranja)</td><td>Escribir un integrando y calcular su integral.</td></tr>
              <tr><td><b>Procedimiento paso a paso</b></td><td>Explica cómo se obtuvo el último resultado.</td></tr>
              <tr><td><b>Gráfica</b></td><td>Muestra la función y su derivada o antiderivada.</td></tr>
              <tr><td><b>Ayuda · Cargar ejemplo · Limpiar</b></td><td>Esta guía, un ejemplo listo y borrar todo.</td></tr>
            </table>
            <h2>Inicio rápido</h2>
            <ol>
              <li>Pulse <b>Cargar ejemplo</b> para ver todo funcionando.</li>
              <li>O escriba su propia función en Derivada o Integral.</li>
              <li>Pulse <b>Derivar</b> o <b>Integrar</b> (también sirve Enter dentro del campo).</li>
              <li>Revise el resultado, el procedimiento y la gráfica.</li>
            </ol>
            """;

    private static final String DERIVADAS = """
            <h1>Derivadas</h1>
            <ol>
              <li>Elija la <b>variable</b> (x o y) en la lista desplegable. La etiqueta cambia a
                  <code>f(x) =</code> o <code>f(y) =</code>.</li>
              <li>Escriba la función, por ejemplo <code>3x^2 + 5x</code>.</li>
              <li>Pulse <b>Derivar</b>.</li>
            </ol>
            <p>Se aplica la <b>regla de la potencia</b>: la derivada de <code>c·xⁿ</code> es
            <code>c·n·xⁿ⁻¹</code>, y la derivada de una constante es 0.</p>
            <h2>Derivadas parciales</h2>
            <p>Si la función tiene las dos variables (por ejemplo <code>2xy + y^3</code>) y deriva respecto de
            <code>x</code>, la otra variable se trata como constante y el resultado se muestra como
            <code>∂f/∂x</code>.</p>
            <h2>Ejemplos</h2>
            <table>
              <tr><th>Entrada</th><th>Variable</th><th>Resultado</th></tr>
              <tr><td><code>3x^2 + 5x</code></td><td>x</td><td>f'(x) = 6x + 5</td></tr>
              <tr><td><code>2xy + y^3</code></td><td>y</td><td>∂f/∂y = 3y² + 2x</td></tr>
              <tr><td><code>x^4 - x/2</code></td><td>x</td><td>f'(x) = 4x³ - 1/2</td></tr>
            </table>
            """;

    private static final String INTEGRALES = """
            <h1>Integrales</h1>
            <h2>Indefinida</h2>
            <ol>
              <li>Elija la <b>variable</b> de integración (el texto <code>dx</code> o <code>dy</code> se actualiza).</li>
              <li>Deje marcada la opción <b>Indefinida</b>.</li>
              <li>Escriba el integrando junto al signo ∫ y pulse <b>Integrar</b>.</li>
            </ol>
            <p>El resultado es la antiderivada más la constante <code>+ C</code>.</p>
            <h2>Definida</h2>
            <ol>
              <li>Marque <b>Definida</b>: se activan los campos de los límites <b>a</b> (inferior) y
                  <b>b</b> (superior).</li>
              <li>Escriba los límites. Aceptan enteros, decimales y fracciones: <code>-1</code>,
                  <code>0.5</code>, <code>1/3</code>.</li>
              <li>Escriba el integrando y pulse <b>Integrar</b>.</li>
            </ol>
            <p>Se calcula <code>F(b) − F(a)</code> (teorema fundamental del cálculo). Si el resultado es una
            fracción, también se muestra su valor decimal aproximado.</p>
            <h2>Ejemplos</h2>
            <table>
              <tr><th>Tipo</th><th>Entrada</th><th>Resultado</th></tr>
              <tr><td>Indefinida en x</td><td><code>3x^2 + 5x</code></td><td>x³ + 5x²/2 + C</td></tr>
              <tr><td>Definida en [0, 2]</td><td><code>2x</code></td><td>4</td></tr>
              <tr><td>Definida en [1, 3]</td><td><code>x^2</code></td><td>26/3 ≈ 8.666667</td></tr>
            </table>
            """;

    private static final String ESCRITURA = """
            <h1>Cómo escribir funciones</h1>
            <table>
              <tr><th>Escriba</th><th>Significa</th></tr>
              <tr><td><code>3x^2 + 5x</code></td><td>3x² + 5x</td></tr>
              <tr><td><code>x^3 - 4x + 7</code></td><td>x³ − 4x + 7</td></tr>
              <tr><td><code>2xy + y^3</code></td><td>2xy + y³ (dos variables)</td></tr>
              <tr><td><code>0.5x^2</code>, <code>x^2/2</code>, <code>1/2x^2</code></td><td>un medio de x²</td></tr>
              <tr><td><code>3x²</code></td><td>también se aceptan superíndices</td></tr>
            </table>
            <p><b>Reglas:</b> exponentes enteros desde 0 (hasta 2 cifras) con <code>^</code>; el signo
            <code>*</code> es opcional; <b>no</b> se usan paréntesis (expanda antes, por ejemplo
            <code>(x+1)^2</code> → <code>x^2 + 2x + 1</code>); solo variables <code>x</code> e
            <code>y</code>. Se aceptan espacios, mayúsculas y coma decimal.</p>
            <h2>Teclado de escritura (centro)</h2>
            <p>Escribe en el <b>último campo en el que hizo clic</b> (la función, <code>a</code> o
            <code>b</code>), en la posición del cursor:</p>
            <ul>
              <li><b>x · y · ^</b>: variables y signo de potencia.</li>
              <li><b>x² · x³ · xⁿ</b>: insertan la variable ya con su exponente (<code>xⁿ</code> deja
                  <code>x^</code> para que escriba el exponente).</li>
              <li><b>² ³ ⁴</b>: superíndices sueltos.</li>
              <li><b>0–9 · . · /</b>: números, punto decimal y fracción.</li>
              <li><b>+ − ·</b>: suma, resta y multiplicación.</li>
              <li><b>← →</b> mueven el cursor; <b>⌫</b> borra el carácter anterior; <b>CE</b> vacía el campo.</li>
            </ul>
            """;

    private static final String GRAFICA = """
            <h1>La gráfica</h1>
            <p>Se dibuja automáticamente cada vez que pulsa <b>Derivar</b> o <b>Integrar</b>.</p>
            <table>
              <tr><th>Operación</th><th>Qué se dibuja</th></tr>
              <tr><td><b>Derivar</b></td><td>La función <code>f</code> (línea azul continua) y su derivada
                  (línea verde discontinua).</td></tr>
              <tr><td><b>Integral indefinida</b></td><td>El integrando <code>f</code> (naranja) y su
                  antiderivada <code>F</code> con C = 0 (verde discontinua).</td></tr>
              <tr><td><b>Integral definida</b></td><td>Lo mismo, más el <b>área sombreada</b> entre
                  <code>a</code> y <code>b</code>, con los límites marcados con líneas verticales punteadas.</td></tr>
            </table>
            <h2>Cómo leerla</h2>
            <ul>
              <li>Los ejes se cruzan en el origen; los números a los lados son la escala.</li>
              <li>La escala se ajusta sola a la función. Si crece muy rápido, la parte más alta se recorta.</li>
              <li>Si la función tiene la otra variable (por ejemplo <code>2xy</code> derivada respecto de x),
                  esa variable se evalúa en <b>1</b> para poder graficar; la leyenda lo indica.</li>
              <li>El área bajo el eje horizontal también se sombrea; recuerde que cuenta como valor negativo
                  en la integral.</li>
            </ul>
            """;

    private static final String BOTONES = """
            <h1>Procedimiento paso a paso</h1>
            <p>El cuadro bajo las tarjetas explica el último cálculo: función o integrando, regla aplicada,
            operación sobre cada término y resultado final. Si hay muchos pasos, use la barra de
            desplazamiento.</p>
            <h1>Botones inferiores</h1>
            <table>
              <tr><th>Botón</th><th>Qué hace</th></tr>
              <tr><td><b>Ayuda</b></td><td>Abre esta guía.</td></tr>
              <tr><td><b>Cargar ejemplo</b></td><td>Carga <code>f(x) = 3x² + 5x</code> y la integral definida de
                  <code>2x</code> entre 0 y 2, y calcula ambos (resultados: 6x + 5 y 4).</td></tr>
              <tr><td><b>Limpiar</b></td><td>Borra los campos, los resultados, el procedimiento y la gráfica.</td></tr>
            </table>
            """;

    private static final String ERRORES = """
            <h1>Mensajes de error</h1>
            <table>
              <tr><th>Mensaje</th><th>Causa y solución</th></tr>
              <tr><td>La expresión está vacía.</td><td>Escriba una función.</td></tr>
              <tr><td>Carácter no válido…</td><td>Solo se permiten números y las variables x, y.</td></tr>
              <tr><td>No se admiten paréntesis…</td><td>Expanda la expresión, por ejemplo
                  <code>(x+1)^2</code> → <code>x^2 + 2x + 1</code>.</td></tr>
              <tr><td>Exponente inválido…</td><td>Use exponentes enteros no negativos de hasta 2 cifras.</td></tr>
              <tr><td>Falta un término después de un signo…</td><td>Elimine el <code>+</code> o <code>-</code> sobrante.</td></tr>
              <tr><td>Escriba el límite… / no es un número válido.</td><td>Complete o corrija <code>a</code> y <code>b</code>.</td></tr>
            </table>
            <p>Cuando hay un error, no se grafica nada nuevo y el motivo aparece en rojo bajo el botón
            y en el cuadro de procedimiento.</p>
            """;
}
