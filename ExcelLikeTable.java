
package proyecto.iii;



import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Clase que extiende JTable para crear una tabla similar a Excel.
 * Esta tabla tiene un comportamiento especial en la primera columna
 * y en el ajuste dinámico del tamaño de las filas.
 */
public class ExcelLikeTable extends JTable {
    // Ancho predeterminado de las columnas (excepto la primera)
    private static final int ANCHO_COLUMNA = 100;
    /**
     * Constructor de la clase ExcelLikeTable.
     * @param modelo El modelo de tabla a utilizar.
     */
    
    public ExcelLikeTable(DefaultTableModel modelo) {
        super(modelo);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(ANCHO_COLUMNA);
        }
        getColumnModel().getColumn(0).setPreferredWidth(40); // Ajustar el ancho de la primera columna
    }
    /**
     * Determina si una celda es editable o no.
     * @param row Fila de la celda.
     * @param column Columna de la celda.
     * @return true si la celda es editable, false si no lo es.
     */

    @Override
    public boolean isCellEditable(int row, int column) {
        // La primera columna (índice 0) no es editable
        return column != 0;
    }
    
    /**
     * Cambia la selección de la celda.
     * @param fila Fila de la celda.
     * @param columna Columna de la celda.
     * @param toggle Toggle.
     * @param extend Extend.
     */
    @Override
    public void changeSelection(int fila, int columna, boolean toggle, boolean extend) {
        super.changeSelection(fila, columna, toggle, extend);
    }
    /**
     * Establece el valor de una celda y evalúa su contenido como fórmula si es necesario.
     * @param aValue El nuevo valor.
     * @param row Fila de la celda.
     * @param column Columna de la celda.
     */
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
        EvaluadorFormula.evaluarCelda((DefaultTableModel) getModel(), row, column);
    }
    /**
     * Prepara el renderizado de una celda.
     * @param renderer El renderer de la celda.
     * @param fila Fila de la celda.
     * @param columna Columna de la celda.
     * @return El componente renderizado.
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int fila, int columna) {
        Component componente = super.prepareRenderer(renderer, fila, columna);
        int alturaPreferida = Math.max(componente.getPreferredSize().height, getRowHeight());
        // Si la fila está fuera de la tabla y la cantidad de filas es menor a 1 millón, se agrega una nueva fila
        if (fila + 1 >= getRowCount() && getModel().getRowCount() < 1000000) {
            ((DefaultTableModel) getModel()).setRowCount(getRowCount() + 1);
        }
        // Se obtienen las barras de desplazamiento vertical y horizontal del JScrollPane contenedor
        JScrollBar barraDesplazamientoVertical = ((JScrollPane) getParent().getParent()).getVerticalScrollBar();
        JScrollBar barraDesplazamientoHorizontal = ((JScrollPane) getParent().getParent()).getHorizontalScrollBar();
        // Se establece que las barras muestren su máximo valor
        barraDesplazamientoVertical.setVisibleAmount(barraDesplazamientoVertical.getMaximum());
        barraDesplazamientoHorizontal.setVisibleAmount(barraDesplazamientoHorizontal.getMaximum());
        
        // Se ajusta la altura de la fila
        setRowHeight(fila, alturaPreferida);
          // Se configura el color de fondo y el texto de la primera columna
        if (columna == 0) {
            componente.setBackground(Color.LIGHT_GRAY);
            ((JLabel) componente).setHorizontalAlignment(SwingConstants.CENTER);
            ((JLabel) componente).setText(String.valueOf(fila + 1));
        } else {
            componente.setBackground(Color.WHITE);
        }

        return componente;
    }
}