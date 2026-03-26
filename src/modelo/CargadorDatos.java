/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
/**
 *
 * @author alvar
 */

public class CargadorDatos {

    //Lee un fichero TSPLIB y devuelve un array de Puntos
    public static Punto[] cargarFichero(File archivo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;
        ArrayList<Punto> listaTemporal = new ArrayList<>();
        int dimensionEsperada = -1;
        boolean leyendoCoordenadas = false;

        while ((linea = br.readLine()) != null) {
            linea = linea.trim();
            
            // 1. Buscamos el tamaño
            if (linea.startsWith("DIMENSION")) {
                String[] partes = linea.split(":");
                dimensionEsperada = Integer.parseInt(partes[1].trim());
            } 
            //2. Detectamos el inicio de los datos
            else if (linea.equals("NODE_COORD_SECTION")) {
                leyendoCoordenadas = true;
                continue; // Saltamos esta línea para empezar a leer números en la siguiente
            } 
            // 3. Fin del fichero
            else if (linea.equals("EOF")) {
                break;
            }

            // 4. Leer las coordenadas
            if (leyendoCoordenadas) {
                // Formato: ID X Y (separados por espacios)
                String[] partes = linea.trim().split("\\s+");
                
                if (partes.length >= 3) {
                    try {
                        int id = Integer.parseInt(partes[0]);
                        double x = Double.parseDouble(partes[1]);
                        double y = Double.parseDouble(partes[2]);
                        
                        listaTemporal.add(new Punto(id, x, y));
                    } catch (NumberFormatException e) {
                        // Si hay nada raro, la ignoramos
                    }
                }
            }
        }
        br.close();

        // Convertimos a Array simple.
        return listaTemporal.toArray(new Punto[0]);
    }
}