package vista;

import algoritmos.*;
import modelo.CargadorDatos;
import modelo.Punto;
import modelo.Resultado;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Ventana Principal de la aplicación.
 * Gestiona la interacción con el usuario, la visualización del mapa y la ejecución de algoritmos.
 * * @author [Álvaro Zarzuela Moncada]
 */
public class VentanaPrincipal extends JFrame {

    // Componentes principales
    private PanelPuntos panelMapa;
    private JTextArea areaTexto;
    private JLabel barraEstado; 
    
    private Punto[] puntosActuales;
    private JFileChooser selectorArchivos;

    // Registro de algoritmos.
    private final Algoritmo[] listaAlgoritmos = {
        new Exhaustivo(), 
        new ExhaustivoPoda(), 
        new DivideVenceras(), 
        new DyVMejorado()
    };
    
    private final String[] nombresAlgoritmos = {
        "Exhaustivo", 
        "Exhaustivo con Poda", 
        "Divide y Vencerás", 
        "DyV Mejorado"
    };

    public VentanaPrincipal() {
        super("Práctica 1 AMC: Pares más Cercanos");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        
        //Inicializamos el selector de archivos en la carpeta actual
        try {
            selectorArchivos = new JFileChooser(".");
        } catch (Exception e) {
            selectorArchivos = new JFileChooser();
        }

        inicializarInterfaz();
    }

    
    private void inicializarInterfaz() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // --- 1. ZONA CENTRAL: Mapa ---
        panelMapa = new PanelPuntos();
        contentPane.add(panelMapa, BorderLayout.CENTER);

        // --- 2. ZONA DERECHA: Panel de Control ---
        JPanel panelDerecha = new JPanel(new BorderLayout());
        panelDerecha.setPreferredSize(new Dimension(300, 0)); //Ancho fijo
        
        // Panel de botones con márgenes y espaciado (Estética mejorada)
        JPanel panelBotones = new JPanel(new GridLayout(18, 1, 8, 8)); // 8px de separación
        panelBotones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15), //Margen externo
                BorderFactory.createEtchedBorder()
        ));
        
        // Fuentes
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 14);
        Font fuenteBoton = new Font("Segoe UI", Font.PLAIN, 12);

        //SECCIÓN DE DATOS
        agregarTitulo(panelBotones, "GESTIÓN DE DATOS", fuenteTitulo);
        panelBotones.add(crearBoton("1. Cargar Fichero TSP", fuenteBoton, e -> accionCargarFichero()));
        panelBotones.add(crearBoton("2. Generar Aleatorio", fuenteBoton, e -> accionGenerarAleatorio()));
        panelBotones.add(new JSeparator());


        //SECCINES INDIVIDUALES
        agregarTitulo(panelBotones, "EJECUCIÓN INDIVIDUAL", fuenteTitulo);
        panelBotones.add(crearBotonAlgoritmo("3. Ejecutar Exhaustivo", listaAlgoritmos[0]));
        panelBotones.add(crearBotonAlgoritmo("4. Ejecutar Poda", listaAlgoritmos[1]));
        panelBotones.add(crearBotonAlgoritmo("5. Ejecutar DyV", listaAlgoritmos[2]));
        
        JButton btnDyVMejor = crearBotonAlgoritmo("6. Ejecutar DyV Mejorado", listaAlgoritmos[3]);
        panelBotones.add(btnDyVMejor);
        panelBotones.add(new JSeparator());

        //ESTUDIO EXPERIMENTAL
        agregarTitulo(panelBotones, "ESTUDIO EXPERIMENTAL", fuenteTitulo);
        JButton btnComprobar = crearBoton("7. COMPROBAR ESTRATEGIAS", new Font("Segoe UI", Font.PLAIN, 12), e -> accionComprobarEstrategias());
        panelBotones.add(btnComprobar);
        
        JButton btnCompTodas = crearBoton("8. COMPARAR TODAS", new Font("Segoe UI", Font.PLAIN, 12), e -> accionCompararTodas());
        panelBotones.add(btnCompTodas);
        
        JButton btnCompDos = crearBoton("9. COMPARAR 2 ESTRATEGIAS", new Font("Segoe UI", Font.PLAIN, 12), e -> accionCompararDos());
        panelBotones.add(btnCompDos);

        panelDerecha.add(panelBotones, BorderLayout.NORTH);
        contentPane.add(panelDerecha, BorderLayout.EAST);

        // --- 3. Zona Inferior: Consola ---
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        // Consola de texto
        areaTexto = new JTextArea(10, 40);
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        areaTexto.setBackground(new Color(250, 250, 250));
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados "));
        panelInferior.add(scroll, BorderLayout.CENTER);

        // Barra de Estado (Feedback al usuario)
        barraEstado = new JLabel(" Sistema listo. Cargue o genere puntos para comenzar.");
        barraEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        barraEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panelInferior.add(barraEstado, BorderLayout.SOUTH);

        contentPane.add(panelInferior, BorderLayout.SOUTH);
    }

    //---------- Métodos Auxiliares para la interfaz gráfica ------------
    private void agregarTitulo(JPanel panel, String texto, Font fuente) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(fuente);
        lbl.setForeground(Color.DARK_GRAY);
        panel.add(lbl);
    }

    private JButton crearBoton(String texto, Font fuente, java.awt.event.ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.setFont(fuente);
        btn.setFocusPainted(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        btn.addActionListener(accion);
        return btn;
    }

    private JButton crearBotonAlgoritmo(String nombre, Algoritmo algoritmo) {
        JButton btn = new JButton(nombre);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> accionEjecutarAlgoritmo(algoritmo, nombre));
        return btn;
    }

    // ================================================================================
    //                               MÉTODOS PARA CADA OPCIÓN
    // =================================================================================

    // --- 1. GESTIÓN DE DATOS ---
    private void accionCargarFichero() {
        int returnVal = selectorArchivos.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File archivo = selectorArchivos.getSelectedFile();
            barraEstado.setText(" Cargando fichero...");
            try {
                puntosActuales = CargadorDatos.cargarFichero(archivo);
                panelMapa.mostrarPuntos(puntosActuales);
                imprimir("\n>>> Fichero cargado: " + archivo.getName() + " (" + puntosActuales.length + " puntos)");
                barraEstado.setText(" Datos cargados correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el fichero: " + ex.getMessage());
                barraEstado.setText(" Error en la carga.");
            }
        }
    }

    private void accionGenerarAleatorio() {
        String input = JOptionPane.showInputDialog(this, "Número de puntos:", "100");
        
        //Si le da a Cancelar, nos salimos y punto.
        if (input == null) return; 

        try {
            int n = Integer.parseInt(input);

            // Pregunta para elegir el modo
            int resp = JOptionPane.showConfirmDialog(this, 
                "¿Usar PEOR CASO (Alineados verticalmente)?", 
                "Tipo de generación", JOptionPane.YES_NO_OPTION);
            
            boolean esPeorCaso = (resp == JOptionPane.YES_OPTION);

            // Generamos datos
            puntosActuales = Punto.generarDataSet(n, esPeorCaso);
            panelMapa.mostrarPuntos(puntosActuales);

            String modo = esPeorCaso ? "PEOR CASO" : "Normal";
            imprimir("\n>>> Generados " + n + " puntos (" + modo + ")");
            barraEstado.setText(" Datos listos.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduce un número válido.");
        }
    }

    // ---------- VERIFICACIÓN Y ALGORITMOS ----------------
    private void accionComprobarEstrategias() {
        if (puntosActuales == null) {
            JOptionPane.showMessageDialog(this, "Primero debe cargar o generar un conjunto de puntos.");
            return;
        }

        new Thread(() -> {
            barraEstado.setText(" Verificando estrategias... Por favor espere.");
            imprimir("\n========================================================================================================================");
            imprimir("                                            COMPROBACIÓN DE ESTRATEGIAS ");
            imprimir("========================================================================================================================");
            
            String cabecera = String.format("%-20s %-28s %-28s %-12s %-11s %-11s", 
                    "Estrategia", "Punto 1", "Punto 2", "Distancia", "Calculadas", "Tiempo(ms)");
            imprimir(cabecera);
            imprimir("------------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < 4; i++) {
                Algoritmo alg = listaAlgoritmos[i];
                long inicio = System.nanoTime();
                Resultado res = alg.resolver(puntosActuales);
                double tiempo = (System.nanoTime() - inicio) / 1e6;

                String p1Str = String.format("%d (%.3f, %.3f)", res.getP1().getId(), res.getP1().getX(), res.getP1().getY());
                String p2Str = String.format("%d (%.3f, %.3f)", res.getP2().getId(), res.getP2().getX(), res.getP2().getY());

                imprimir(String.format("%-20s %-28s %-28s %12.8f %10d %10.4f", 
                        nombresAlgoritmos[i], p1Str, p2Str, res.getDistancia(), res.getCalculos(), tiempo));
                
                if (i == 3) panelMapa.mostrarSolucion(res.getP1(), res.getP2());
            }
            imprimir("========================================================================================================================\n");
            barraEstado.setText(" Verificación completada.");
        }).start();
    }

    private void accionEjecutarAlgoritmo(Algoritmo alg, String nombre) {
        if (puntosActuales == null) {
            JOptionPane.showMessageDialog(this, "Primero debe cargar puntos.");
            return;
        }
        
        new Thread(() -> {
            barraEstado.setText(" Ejecutando " + nombre + ":");
            imprimir("Ejecutando " + nombre + ":");
            
            try {
                long inicio = System.nanoTime();
                Resultado res = alg.resolver(puntosActuales);
                double tiempoMs = (System.nanoTime() - inicio) / 1e6;

                SwingUtilities.invokeLater(() -> {
                    String msg = String.format("   ---> Distancia: %.8f | Pareja: %d-%d | Cálculos: %d | Tiempo: %.4f ms",
                            res.getDistancia(), res.getP1().getId(), res.getP2().getId(), res.getCalculos(), tiempoMs);
                    imprimir(msg);
                    panelMapa.mostrarSolucion(res.getP1(), res.getP2());
                    barraEstado.setText(" " + nombre + " finalizado en " + String.format("%.2f", tiempoMs) + " ms.");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // --- 3. ESTUDIO EXPERIMENTAL ---

    private void accionCompararTodas() {
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Analizar PEOR CASO (Puntos verticales)?", "Configuración Experimento", JOptionPane.YES_NO_OPTION);
        boolean peorCaso = (respuesta == JOptionPane.YES_OPTION);
        
        new Thread(() -> {
            barraEstado.setText(" Ejecutando comparativa completa... Esto puede tardar.");
            imprimir("\n=============== COMPARATIVA TOTAL (" + (peorCaso ? "PEOR CASO" : "NORMAL") + ") ===============");
            imprimir(String.format("%-8s | %-10s | %-10s | %-10s | %-10s", "", "Exhaustivo", "ExhPoda", "DyV", "DyV+"));
            imprimir(String.format("%-8s | %-10s | %-10s | %-10s | %-10s", "TALLA", "t(mseg)", "t(mseg)", "t(mseg)", "t(mseg)"));
            imprimir("---------+------------+------------+------------+------------");

            int[] tallas = {1000, 2000, 3000, 4000, 5000};
            int reps = 10;

            for (int n : tallas) {
                double[] tiempos = new double[4];
                for (int i = 0; i < reps; i++) {
                    Punto[] datos = Punto.generarDataSet(n, peorCaso);
                    for (int a = 0; a < 4; a++) tiempos[a] += medirTiempo(listaAlgoritmos[a], datos);
                }
                imprimir(String.format("%-8d | %-10.2f | %-10.2f | %-10.2f | %-10.2f", 
                        n, tiempos[0]/reps, tiempos[1]/reps, tiempos[2]/reps, tiempos[3]/reps));
            }
            imprimir("====================== FIN ===============================");
            barraEstado.setText(" Comparativa finalizada.");
        }).start();
    }

    private void accionCompararDos() {
        //Selecciono algoritmos con JComboBox
        JPanel panelSel = new JPanel(new GridLayout(2, 2, 5, 5));
        JComboBox<String> combo1 = new JComboBox<>(nombresAlgoritmos);
        JComboBox<String> combo2 = new JComboBox<>(nombresAlgoritmos);
        combo1.setSelectedIndex(0); 
        combo2.setSelectedIndex(2); 
        
        panelSel.add(new JLabel("Estrategia 1:"));
        panelSel.add(combo1);
        panelSel.add(new JLabel("Estrategia 2:"));
        panelSel.add(combo2);

        int result = JOptionPane.showConfirmDialog(null, panelSel, 
                "Selecciona algoritmos a enfrentar", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        Algoritmo alg1 = listaAlgoritmos[combo1.getSelectedIndex()];
        Algoritmo alg2 = listaAlgoritmos[combo2.getSelectedIndex()];
        String nom1 = nombresAlgoritmos[combo1.getSelectedIndex()];
        String nom2 = nombresAlgoritmos[combo2.getSelectedIndex()];

        int respPeor = JOptionPane.showConfirmDialog(this, "¿Analizar PEOR CASO (Vertical)?", "Configuración", JOptionPane.YES_NO_OPTION);
        boolean peorCaso = (respPeor == JOptionPane.YES_OPTION);

        new Thread(() -> {
            barraEstado.setText(" Comparando " + nom1 + " vs " + nom2 + "...");
            imprimir("\n=== VS: " + nom1 + " vs " + nom2 + " (" + (peorCaso ? "PEOR CASO" : "NORMAL") + ") ===");
            
            // Nombres cortos para la tabla
            String n1 = nom1.length() > 7 ? nom1.substring(0,7) : nom1;
            String n2 = nom2.length() > 7 ? nom2.substring(0,7) : nom2;
            
            imprimir(String.format("%-6s | T.%-7s | T.%-7s | D.%-7s | D.%-7s", "TALLA", n1, n2, n1, n2));
            imprimir("-------+-----------+-----------+-----------+-----------");

            int[] tallas = {1000, 2000, 3000, 4000, 5000};
            int reps = 10;

            for (int n : tallas) {
                double t1 = 0, t2 = 0;
                long d1 = 0, d2 = 0;

                for (int i = 0; i < reps; i++) {
                    Punto[] datos = Punto.generarDataSet(n, peorCaso);
                    
                    ResultadoMedicion r1 = medirCompleto(alg1, datos);
                    t1 += r1.tiempoMs;
                    d1 += r1.calculos;
                    
                    ResultadoMedicion r2 = medirCompleto(alg2, datos);
                    t2 += r2.tiempoMs;
                    d2 += r2.calculos;
                }

                imprimir(String.format("%-6d | %-9.2f | %-9.2f | %-9d | %-9d", 
                        n, t1/reps, t2/reps, d1/reps, d2/reps));
            }
            imprimir("================== FIN COMPARATIVA VS =================");
            barraEstado.setText(" Comparativa completada.");
        }).start();
    }

    //--- UTILIDADES ---

    private double medirTiempo(Algoritmo alg, Punto[] datos) {
        long ini = System.nanoTime();
        alg.resolver(datos);
        return (System.nanoTime() - ini) / 1e6;
    }

    //Clase interna para guardar métricas
    private static class ResultadoMedicion {
        double tiempoMs;
        long calculos;
        public ResultadoMedicion(double t, long c) { this.tiempoMs = t; this.calculos = c; }
    }

    private ResultadoMedicion medirCompleto(Algoritmo alg, Punto[] datos) {
        long ini = System.nanoTime();
        Resultado res = alg.resolver(datos);
        long fin = System.nanoTime();
        return new ResultadoMedicion((fin - ini) / 1e6, res.getCalculos());
    }

    private void imprimir(String texto) {
        SwingUtilities.invokeLater(() -> {
            areaTexto.append(texto + "\n");
            areaTexto.setCaretPosition(areaTexto.getDocument().getLength());
        });
    }
}