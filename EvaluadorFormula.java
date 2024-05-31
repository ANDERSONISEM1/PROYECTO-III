package proyecto.iii;

import java.util.function.DoubleBinaryOperator;
import javax.swing.table.DefaultTableModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Clase que se encarga de evaluar las fórmulas en las celdas de una tabla.
 */
public class EvaluadorFormula {
/**
     * Método para evaluar una fórmula en una celda de una tabla.
     * @param modelo El modelo de la tabla.
     * @param fila La fila de la celda a evaluar.
     * @param columna La columna de la celda a evaluar.
     */
    public static void evaluarCelda(DefaultTableModel modelo, int fila, int columna) {
        Object valor = modelo.getValueAt(fila, columna);
        // Verificar si el valor es una cadena y comienza con "="
        if (!(valor instanceof String) || !((String) valor).startsWith("=")) {
            return;// No es una fórmula, salir
        }

        String formula = ((String) valor).substring(1);// Obtener la fórmula sin el "="
        modelo.setValueAt("", fila, columna); // Limpiar la celda antes de evaluarla

        try {
             // Verificar si la fórmula es una función predefinida (SUMA, MULTIPLICAR, RESTA, DIVIDIR)
            if (formula.toUpperCase().startsWith("SUMA(") && formula.endsWith(")")) {
                double suma = evaluateSum(formula.substring(5, formula.length() - 1), modelo);
                modelo.setValueAt(suma, fila, columna);
            } else if (formula.toUpperCase().startsWith("MULTIPLICAR(") && formula.endsWith(")")) {
                double producto = evaluateMultiplication(formula.substring(12, formula.length() - 1), modelo);
                modelo.setValueAt(producto, fila, columna);
            } else if (formula.toUpperCase().startsWith("RESTA(") && formula.endsWith(")")) {
                double resta = evaluateResta(formula.substring(5, formula.length() - 1), modelo);
                modelo.setValueAt(resta, fila, columna);
            } else if (formula.toUpperCase().startsWith("DIVIDIR(") && formula.endsWith(")")) {
                double division = evaluateDivision(formula.substring(8, formula.length() - 1), modelo);
                modelo.setValueAt(division, fila, columna);
            } else if (formula.contains("+")) {
                evaluarOperacion(modelo, fila, columna, formula, "\\+", (a, b) -> a + b);
            } else if (formula.contains("-")) {
                evaluarOperacion(modelo, fila, columna, formula, "-", (a, b) -> a - b);
            } else if (formula.contains("*")) {
                evaluarOperacion(modelo, fila, columna, formula, "\\*", (a, b) -> a * b);
            } else if (formula.contains("/")) {
                evaluarOperacion(modelo, fila, columna, formula, "/", (a, b) -> {
                    if (b == 0) {
                        throw new ArithmeticException("División por cero");
                    }
                    return a / b;
                });
            } else {
                // Fórmula no válida
                modelo.setValueAt("Error", fila, columna);
            }
        } catch (NumberFormatException | ArithmeticException e) {
             // Capturar errores de formato numérico o división por cero
            modelo.setValueAt("Error: " + e.getMessage(), fila, columna);
        }
    }
   /**
     * Método para evaluar una operación en una fórmula.
     * @param modelo El modelo de la tabla.
     * @param fila La fila de la celda a evaluar.
     * @param columna La columna de la celda a evaluar.
     * @param formula La fórmula a evaluar.
     * @param operadorRegex La expresión regular del operador.
     * @param operacion La operación a realizar.
     */
    private static void evaluarOperacion(DefaultTableModel modelo, int fila, int columna, String formula, String operadorRegex, DoubleBinaryOperator operacion){
        String[] partes = formula.split(operadorRegex);
        double resultado = Double.parseDouble(partes[0].trim());
        for (int i = 1; i < partes.length; i++) {
            resultado = operacion.applyAsDouble(resultado, Double.parseDouble(partes[i].trim()));
        }
        modelo.setValueAt(resultado, fila, columna);
    }
    /**
     * Método para evaluar la función SUMA en una fórmula.
     * @param expression La expresión a evaluar.
     * @param modelo El modelo de la tabla.
     * @return El resultado de la suma.
     */
    private static double evaluateSum(String expression, DefaultTableModel modelo) {
        double sum = 0;
        expression = expression.replace(" ", "");// Eliminar espacios en blanco
        Pattern pattern = Pattern.compile("[0-9]+");// Expresión regular para encontrar números
        Matcher matcher = pattern.matcher(expression); // Sumar los números encontrados

        while (matcher.find()) {
            sum += Double.parseDouble(matcher.group());
        }

        return sum;
    }
     /**
     * Método para evaluar la función MULTIPLICAR en una fórmula.
     * @param expression La expresión a evaluar.
     * @param modelo El modelo de la tabla.
     * @return El resultado de la multiplicación.
     */
    // Método para evaluar la función MULTIPLICAR en una fórmula
    private static double evaluateMultiplication(String expression, DefaultTableModel modelo) {
        double product = 1;
        expression = expression.replace(" ", "");// Eliminar espacios en blanco
        Pattern pattern = Pattern.compile("[0-9]+");// Expresión regular para encontrar números
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            product *= Double.parseDouble(matcher.group());// Multiplicar los números encontrados
        
        }

        return product;
    }
    /**
     * Método para evaluar la función RESTA en una fórmula.
     * @param expression La expresión a evaluar.
     * @param modelo El modelo de la tabla.
     * @return El resultado de la resta.
     */
    // Método para evaluar la función RESTA en una fórmula
    private static double evaluateResta(String expression, DefaultTableModel modelo) {
        double resta = 0;
        expression = expression.replace(" ", "");// Eliminar espacios en blanco
        Pattern pattern = Pattern.compile("[0-9]+");// Expresión regular para encontrar números
        Matcher matcher = pattern.matcher(expression);

        if (matcher.find()) {
            resta = Double.parseDouble(matcher.group());// Obtener el primer número
        }

        while (matcher.find()) {
            resta -= Double.parseDouble(matcher.group()); // Restar los números siguientes
        }

        return resta;
    }
    
    /**
     * Método para evaluar la función DIVIDIR en una fórmula.
     * @param expression La expresión a evaluar.
     * @param modelo El modelo de la tabla.
     * @return El resultado de la división.
     */
      // Método para evaluar la función DIVIDIR en una fórmula
    private static double evaluateDivision(String expression, DefaultTableModel modelo) {
        double division = 0;
        expression = expression.replace(" ", "");// Eliminar espacios en blanco
        Pattern pattern = Pattern.compile("[0-9]+");// Expresión regular para encontrar números
        Matcher matcher = pattern.matcher(expression);

        if (matcher.find()) {
            division = Double.parseDouble(matcher.group());// Obtener el primer número
        }

        while (matcher.find()) {
            division /= Double.parseDouble(matcher.group());// Dividir por los números siguientes
        }

        return division;
    }
}
