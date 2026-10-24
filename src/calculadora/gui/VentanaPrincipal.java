package calculadora.gui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/** Ventana principal (JFrame) que contiene el panel de la calculadora. */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        super("CalculadoraCalculo — Programación II");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(new PanelCalculadora());
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }
}
