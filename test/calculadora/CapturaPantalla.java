package calculadora;

import calculadora.gui.PanelCalculadora;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/** Genera una captura de la interfaz para el manual (funciona incluso sin pantalla: modo headless). */
public class CapturaPantalla {

    private static void disponer(Component c) {
        if (c instanceof Container k) {
            k.doLayout();
            for (Component h : k.getComponents()) disponer(h);
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SwingUtilities.invokeAndWait(() -> {
            try {
                PanelCalculadora p = new PanelCalculadora();
                Dimension d = p.getPreferredSize();
                p.setSize(d);
                disponer(p);
                p.cargarEjemplo();
                disponer(p);
                BufferedImage img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = img.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                p.paint(g);
                g.dispose();
                ImageIO.write(img, "png", new File(args[0]));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
