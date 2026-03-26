/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;

/**
 * Clase principal de arranque de la aplicación.
 * Práctica 1 AMC: Pares más Cercanos - 
 * * @author [Álvaro Zarzuela Moncada]
 */
public class Main {
    
    public static void main(String[] args) {
        // Ejecutamos la interfaz en el hilo seguro de Swing.
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Error: No se pudo cargar el sistema.");
            }
            
            // Creamos y mostramos la ventana principal
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
