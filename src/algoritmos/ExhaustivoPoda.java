/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algoritmos;
import modelo.Punto;
import modelo.Resultado;

/**
 *
 * @author alvar
 */

public class ExhaustivoPoda implements Algoritmo {

    @Override
    public Resultado resolver(Punto[] puntosOriginales) {
        // 1. CLONAR ARRAY 
        // El algoritmo necesita ordenar los puntos. Si ordenamos el array original,
        // rompemos los datos de la ventana y el siguiente algoritmo fallará.
        Punto[] puntos = puntosOriginales.clone();
        
        // 2. ORDENAR POR EJE X (Usando QuickSort como pide el PDF)
        quickSort(puntos, 0, puntos.length - 1);

        long calculos = 0;
        double minDistancia = Double.MAX_VALUE;
        Punto pA = null;
        Punto pB = null;

        int n = puntos.length;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                
                // --- LA PODA ---
                double diffX = puntos[j].getX() - puntos[i].getX();
                
                // Si la diferencia en X ya es mayor que la mejor distancia encontrada entonces paramos.
                if (diffX * diffX >= minDistancia) {
                    break; // Salimos del bucle interno (j)
                }
                
                // Si pasa la poda, calculamos la distancia real.
                double distCuadrada = puntos[i].distanciaCuadrado(puntos[j]);
                calculos++;

                if (distCuadrada < minDistancia) {
                    minDistancia = distCuadrada;
                    pA = puntos[i];
                    pB = puntos[j];
                }
            }
        }

        return new Resultado(pA, pB, Math.sqrt(minDistancia), calculos);
    }

    // ---  Quicksort para la poda ---
    private void quickSort(Punto[] arr, int izq, int der) {
        if (izq < der) {
            int pivoteIndex = particion(arr, izq, der);
            quickSort(arr, izq, pivoteIndex - 1);
            quickSort(arr, pivoteIndex + 1, der);
        }
    }
    //Método auxiliar para el quicksort
    private int particion(Punto[] arr, int izq, int der) {
        // Elegimos el último elemento como pivote y lo ordenamos por X (horizotalmente)
        double pivoteX = arr[der].getX(); 
        int i = (izq - 1);

        for (int j = izq; j < der; j++) {
            //Ordenamos por coordenada X
            if (arr[j].getX() <= pivoteX) {
                i++;
                //Si se cumple intercambio
                Punto temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        //Coloco pivote en su sitio
        Punto temp = arr[i + 1];
        arr[i + 1] = arr[der];
        arr[der] = temp;

        return i + 1;
    }
}

