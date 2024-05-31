package proyecto.iii;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.io.FileOutputStream;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HojaCalculo extends JFrame {

    private JTabbedPane tabbedPane;
    private static final int ANCHO_COLUMNA = 100;
    private static final int CANTIDAD_COLUMNAS = 26 * 26 * 26;

    public HojaCalculo() {
        setTitle("Hoja de Cálculo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        tabbedPane = new JTabbedPane();

        // Crear la primera hoja
        crearNuevaHoja("Hoja 1");
        crearNuevaHoja("Hoja 2");

        // Crear y agregar el botón "+"
        JPanel panelBotonMas = new JPanel(new BorderLayout());
        JButton botonMas = new JButton("+");
        botonMas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearNuevaHoja("Hoja " + (tabbedPane.getTabCount() + 1));
            }
        });
        panelBotonMas.add(botonMas, BorderLayout.EAST);

        // Crear y agregar el menú
        JMenuBar menuBar = crearbarramenús();
        setJMenuBar(menuBar);

        // Agregar el botón "+" al panel de contenido
        getContentPane().add(panelBotonMas, BorderLayout.SOUTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

   private void crearNuevaHoja(String titulo) {
    DefaultTableModel modelo = new DefaultTableModel(100, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            // Hacer que B2 (fila 1, columna 2) y C2 (fila 1, columna 3) no sean editables en Hoja 2
            if ("Hoja 2".equals(titulo) && fila == 1 && (columna == 2 || columna == 3)) {
                return false;
            }
            return true;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            super.setValueAt(aValue, row, column);
            EvaluadorFormula.evaluarCelda(this, row, column);
        }
    };

    // Agregar la columna adicional antes de "A"
    modelo.addColumn(" ");

    // Número de columnas hasta "XFD"
    final int CANTIDAD_COLUMNAS = 16383;

    // Agregar columnas
    for (int i = 0; i < CANTIDAD_COLUMNAS; i++) {
        StringBuilder nombreColumna = new StringBuilder();
        int n = i + 1;
        while (n > 0) {
            int resto = (n - 1) % 26;
            nombreColumna.insert(0, (char) ('A' + resto));
            n = (n - 1) / 26;
        }
        modelo.addColumn(nombreColumna.toString());
    }

    JTable tabla = new ExcelLikeTable(modelo);
    JScrollPane panelDesplazamiento = new JScrollPane(tabla);

    JScrollBar barraDesplazamientoVertical = createScrollBar(JScrollBar.VERTICAL);
    JScrollBar barraDesplazamientoHorizontal = createScrollBar(JScrollBar.HORIZONTAL);
    panelDesplazamiento.setVerticalScrollBar(barraDesplazamientoVertical);
    panelDesplazamiento.setHorizontalScrollBar(barraDesplazamientoHorizontal);

    JPanel panelHoja = new JPanel(new BorderLayout());
    panelHoja.add(panelDesplazamiento, BorderLayout.CENTER);

    if ("Hoja 2".equals(titulo)) {
        modelo.setValueAt("CLAVE", 1, 2); // B2
        modelo.setValueAt("VALOR", 1, 3); // C2

        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 5, 5));
        JButton btnAgregarElemento = new JButton("Agregar elemento");
        JButton btnObtenerElemento = new JButton("Obtener elemento");
        JButton btnEliminarElemento = new JButton("Eliminar elemento");
        JButton btnVerTamanioTabla = new JButton("Ver tamaño de la tabla");
        JButton btnVerificarSiEstaVacia = new JButton("Verificar si está vacía");

        // Agregar acciones a los botones
        btnAgregarElemento.addActionListener(e -> agregarElemento(tabla));
        btnObtenerElemento.addActionListener(e -> obtenerElemento(tabla));
        btnEliminarElemento.addActionListener(e -> eliminarElemento(tabla));
        btnVerTamanioTabla.addActionListener(e -> verTamanioTabla(tabla));
        btnVerificarSiEstaVacia.addActionListener(e -> verificarSiEstaVacia(tabla));

        panelBotones.add(btnAgregarElemento);
        panelBotones.add(btnObtenerElemento);
        panelBotones.add(btnEliminarElemento);
        panelBotones.add(btnVerTamanioTabla);
        panelBotones.add(btnVerificarSiEstaVacia);

        panelHoja.add(panelBotones, BorderLayout.EAST);
    }

    tabbedPane.addTab(titulo, panelHoja);
    int index = tabbedPane.indexOfComponent(panelHoja);
    tabbedPane.setTabComponentAt(index, crearPanelPestania(tabbedPane, panelHoja, titulo));
}

    private void agregarElemento(JTable tabla) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();

        String clave = JOptionPane.showInputDialog(this, "Ingrese la CLAVE:");
        if (clave == null || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La clave no puede estar vacía");
            return;
        }

        String valor = JOptionPane.showInputDialog(this, "Ingrese el VALOR:");
        if (valor == null || valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El valor no puede estar vacío");
            return;
        }

        // Encontrar la primera fila vacía en las columnas "CLAVE" y "VALOR"
        int rowCount = modelo.getRowCount();
        int filaClave = -1;
        int filaValor = -1;

        for (int i = 2; i < rowCount; i++) { // Comienza en 2 para saltar las filas "CLAVE" y "VALOR"
            if (modelo.getValueAt(i, 2) == null) {
                filaClave = i;
                break;
            }
        }

        if (filaClave == -1) {
            filaClave = rowCount;
            modelo.setRowCount(rowCount + 1);
        }

        filaValor = filaClave; // La fila de "VALOR" será la misma que la de "CLAVE"

        modelo.setValueAt(clave, filaClave, 2); // Columna B (índice 2)
        modelo.setValueAt(valor, filaValor, 3); // Columna C (índice 3)
    }

    private void obtenerElemento(JTable tabla) {
             // Obtener el modelo de la tabla para acceder a los datos
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        // Solicitar al usuario que ingrese la clave a buscar
        String clave = JOptionPane.showInputDialog(this, "Ingrese la CLAVE que desea buscar:");
        // Validar si la clave ingresada es válida
        if (clave == null || clave.isEmpty()) {
             // Mostrar un mensaje de error si la clave está vacía
            JOptionPane.showMessageDialog(this, "La clave no puede estar vacía");
            return;
        }
        // Buscar la clave en el modelo de la tabla
        int rowCount = modelo.getRowCount();
        for (int i = 2; i < rowCount; i++) {
            if (clave.equals(modelo.getValueAt(i, 2))) {
                // Si se encuentra la clave, mostrar el valor correspondiente
                Object valor = modelo.getValueAt(i, 3);
                JOptionPane.showMessageDialog(this, "El valor correspondiente a la clave " + clave + " es: " + valor);
                return;
            }
        }
        // Mostrar un mensaje si la clave no se encuentra en la tabla
        JOptionPane.showMessageDialog(this, "La clave " + clave + " no existe.");
    }

    private void eliminarElemento(JTable tabla) {
         // Obtener el modelo de la tabla para acceder a los datos
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
         // Solicitar al usuario que ingrese la clave del elemento a eliminar
        String clave = JOptionPane.showInputDialog(this, "Ingrese la CLAVE del elemento a eliminar:");
                // Validar si la clave ingresada es válida
        if (clave == null || clave.isEmpty()) {
              // Mostrar un mensaje de error si la clave está vacía
            JOptionPane.showMessageDialog(this, "La clave no puede estar vacía");
            return;
        }
        // Buscar la clave en el modelo de la tabla
        int rowCount = modelo.getRowCount();
        for (int i = 2; i < rowCount; i++) {
            if (clave.equals(modelo.getValueAt(i, 2))) {
                 // Si se encuentra la clave, eliminar la fila correspondiente
                modelo.removeRow(i);
                JOptionPane.showMessageDialog(this, "El elemento con clave " + clave + " ha sido eliminado.");
                return;
            }
        }
        // Mostrar un mensaje si la clave no se encuentra en la tabla
        JOptionPane.showMessageDialog(this, "La clave " + clave + " no existe.");
    }

    private void verTamanioTabla(JTable tabla) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        int filasConClaves = 0;

        for (int i = 2; i < modelo.getRowCount(); i++) { // Comienza en 2 para saltar las filas "CLAVE" y "VALOR"
            if (modelo.getValueAt(i, 2) != null) {
                filasConClaves++;
            }
        }

        int columnas = modelo.getColumnCount();
        JOptionPane.showMessageDialog(this, "Tamaño de la tabla: " + filasConClaves );
    }

    private void verificarSiEstaVacia(JTable tabla) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        boolean vacia = true;
        for (int i = 2; i < modelo.getRowCount(); i++) {
            Object clave = modelo.getValueAt(i, 2);
            if (clave != null            && !clave.toString().isEmpty()) {
                vacia = false;
                break;
            }
        }
        JOptionPane.showMessageDialog(null, vacia ? "La tabla está vacía" : "La tabla no está vacía");
    }

    private JScrollBar createScrollBar(int orientation) {
        JScrollBar scrollBar = new JScrollBar(orientation);
        scrollBar.setVisibleAmount(1);
        scrollBar.setUI(new MiScrollBarUI());
        return scrollBar;
    }

  private JMenuBar crearbarramenús() {
    JMenuBar menuBar = new JMenuBar();
    
    // Crear el menú "Archivo"
    JMenu archivoMenu = new JMenu("Archivo");
    
  // Crear el ítem "Guardar" y agregarlo al menú "Archivo"
JMenuItem itemGuardar = new JMenuItem("Guardar");
itemGuardar.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        guardarArchivo();
    }
});
archivoMenu.add(itemGuardar);

    
    // Agregar el menú "Archivo" a la barra de menús
    menuBar.add(archivoMenu);
    
    // Crear el ítem "Ayuda" y agregar el ActionListener
    JMenuItem ayudaMenuItem = new JMenuItem("Ayuda");
    ayudaMenuItem.addActionListener(e -> mostrarOperaciones());
    
    // Agregar el ítem "Ayuda" directamente a la barra de menús
    menuBar.add(ayudaMenuItem);
    
    return menuBar;
}


    private void mostrarOperaciones() {
        String textoOperaciones = "Las operaciones que se pueden realizar en la hoja de cálculo son:\n" +
                "- Crear nuevas hojas de cálculo.\n" +
                "- Guardar y abrir archivos Excel.\n" +
                "- Ver y desplazarse por las hojas existentes.\n" +
                "- Funciones: Suma =suma(), Resta =resta(), Multiplicacion =multiplicar() y Division =dividir()" +
                "- Ayuda para obtener más información.";
        JOptionPane.showMessageDialog(null, textoOperaciones, "Operaciones de la Hoja de Cálculo", JOptionPane.INFORMATION_MESSAGE);
    }

    private JMenuItem createMenuItem(String label) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener(e -> {
            if (label.equals("Nuevo")) {
                JOptionPane.showMessageDialog(null, "Nuevo archivo creado");
            } else if (label.equals("Guardar")) {
                guardarArchivo();
            }
        });
        return menuItem;
    }
private void guardarArchivo() {
    Component selectedComponent = tabbedPane.getSelectedComponent();
    if (selectedComponent instanceof JPanel) {
        JPanel panelHoja = (JPanel) selectedComponent;
        JScrollPane scrollPane = (JScrollPane) panelHoja.getComponent(0); // El JScrollPane debe ser el primer componente del JPanel
        JTable tablaSeleccionada = (JTable) scrollPane.getViewport().getView();

        if (tablaSeleccionada.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para guardar en la hoja de cálculo");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));
        int seleccion = fileChooser.showSaveDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!archivo.getName().endsWith(".xlsx")) {
                archivo = new File(archivo.getAbsolutePath() + ".xlsx");
            }
            guardarArchivoXLSX(tablaSeleccionada, archivo);
        }
    } else {
        JOptionPane.showMessageDialog(this, "No se ha seleccionado una hoja de cálculo");
    }
}
private void guardarArchivoXLSX(JTable tabla, File archivo) {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Hoja 1");

    DefaultTableModel model = (DefaultTableModel) tabla.getModel();

    // Crear filas y columnas sin el encabezado
    for (int row = 0; row < model.getRowCount(); row++) {
        Row sheetRow = sheet.createRow(row); // Comenzar desde la fila 0
        for (int col = 0; col < model.getColumnCount(); col++) {
            Cell cell = sheetRow.createCell(col);
            Object value = model.getValueAt(row, col);
            if (value != null) {
                cell.setCellValue(value.toString());
            }
        }
    }

    try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
        workbook.write(fileOut);
        workbook.close();
        JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente en " + archivo.getAbsolutePath());
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    
    private void abrirArchivoExcel() {
          // Crear un selector de archivos
        JFileChooser fileChooser = new JFileChooser();
              // Mostrar el diálogo para seleccionar un archivo
        int seleccion = fileChooser.showOpenDialog(null);
        // Verificar si se seleccionó un archivo
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            // Obtener el archivo seleccionado
            File archivoExcel = fileChooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new FileReader(archivoExcel))) {
                String linea;
                
                // Leer cada línea del archivo
                while ((linea = br.readLine()) != null) {
                      // Dividir la línea en datos separados por comas
                    String[] datos = linea.split(",");
                    // Obtener el modelo de la tabla actualmente seleccionada en el tabbedPane
                    DefaultTableModel modelo = (DefaultTableModel) ((JTable) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView()).getModel();
                     // Agregar una nueva fila con los datos obtenidos
                    modelo.addRow(datos);
                }
                 // Notificar al modelo de la tabla que los datos han cambiado
                ((DefaultTableModel) ((JTable) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView()).getModel()).fireTableDataChanged();
            } catch (IOException ex) {
                // Mostrar un mensaje de error si ocurre una excepción al abrir el archivo
                mostrarMensajeError("Error al abrir el archivo Excel: " + ex.getMessage());
            }
        }
    }

    private void mostrarMensajeError(String mensaje) {
         /* Mostrar un cuadro de diálogo con un mensaje de error
         El cuadro de diálogo se mostrará en el centro de la pantalla (null)
         El mensaje se mostrará con el título "Error" y un icono de error*/
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel crearPanelPestania(JTabbedPane tabbedPane, Component panelHoja, String titulo) {
        // Crear un panel para la pestaña con un diseño de FlowLayout y sin opacidad
        JPanel panelPestania = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelPestania.setOpaque(false);
        // Crear una etiqueta con el título de la pestaña y agregarla al panel
        JLabel etiquetaTitulo = new JLabel(titulo);
        panelPestania.add(etiquetaTitulo);
        // Crear un botón para cerrar la pestaña
        JButton botonCerrar = new JButton("x");
        botonCerrar.setOpaque(true);// Establecer la opacidad del botón
        botonCerrar.setBorder(BorderFactory.createEmptyBorder());// Establecer un borde vacío para que no se vean los bordes del botón
        botonCerrar.setFocusPainted(false);// Evitar que el botón muestre un efecto de foco al ser seleccionado
        botonCerrar.setContentAreaFilled(false);// Establecer que el área de contenido del botón no se pinte
        botonCerrar.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));// Establecer un borde gris para el botón
        botonCerrar.setMargin(new java.awt.Insets(2, 2, 2, 2));// Establecer márgenes internos para el texto del botón
        botonCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener el índice de la pestaña actual en el JTabbedPane y removerla si es válida
                int index = tabbedPane.indexOfComponent(panelHoja);
                if (index != -1) {
                    tabbedPane.remove(index);
                }
            }
        });
        // Agregar el botón de cierre al panel
        panelPestania.add(botonCerrar);
     // Retornar el panel creado para la pestaña
        return panelPestania;
    }

    public static void main(String[] args) {
        // Ejecutar la creación de la hoja de cálculo en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> new HojaCalculo().setVisible(true));
    }
}

