
package proyecto.iii;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Clase que personaliza la apariencia de las barras de desplazamiento.
 */
public class MiScrollBarUI extends BasicScrollBarUI {
    // Color del "thumb" (la parte móvil de la barra de desplazamiento)
    private final Color colorThumb = new Color(170, 170, 170);
     // Color del "track" (la parte fija de la barra de desplazamiento)
    private final Color colorTrack = new Color(240, 240, 240);
     /**
     * Crea un botón para decrementar el valor de la barra de desplazamiento.
     * @param orientation Orientación del botón (vertical u horizontal).
     * @return El botón creado.
     */
    @Override
    protected JButton createDecreaseButton(int orientation) {
        // Se crea un botón sin contenido visual
        return new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 0);
            }
        };
    }
     /**
     * Crea un botón para incrementar el valor de la barra de desplazamiento.
     * @param orientation Orientación del botón (vertical u horizontal).
     * @return El botón creado.
     */
    @Override
    protected JButton createIncreaseButton(int orientation) {
        // Se crea un botón sin contenido visual
        return new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 0);
            }
        };
    }
     /**
     * Pinta el "thumb" de la barra de desplazamiento.
     * @param g Gráfico donde se va a pintar.
     * @param c Componente que contiene la barra de desplazamiento.
     * @param thumbBounds Área del "thumb" a pintar.
     */
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Se pinta el "thumb" con bordes redondeados
        g2.setColor(colorThumb);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
        g2.dispose();
    }
     /**
     * Pinta el "track" de la barra de desplazamiento.
     * @param g Gráfico donde se va a pintar.
     * @param c Componente que contiene la barra de desplazamiento.
     * @param trackBounds Área del "track" a pintar.
     */
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
       // Se pinta el "track" con un color específico
        g.setColor(colorTrack);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
}