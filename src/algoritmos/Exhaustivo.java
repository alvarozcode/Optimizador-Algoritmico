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
public class Exhaustivo implements Algoritmo {

    @Override
    public Resultado resolver(Punto[] puntos) {
        long calculos = 0;
        double minDistancia = Double.MAX_VALUE;
        Punto pA = null;
        Punto pB = null;

        int n = puntos.length;

        // Doble for: Compara cada punto 'i' con todos los siguientes 'j'
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                
                double distCuadrada = puntos[i].distanciaCuadrado(puntos[j]);
                calculos++; //Incremento en uno los calculos de distancia

                if (distCuadrada < minDistancia) {
                    minDistancia = distCuadrada;
                    pA = puntos[i];
                    pB = puntos[j];
                }
            }
        }

        //Calculo la distancia real
        double distanciaReal = Math.sqrt(minDistancia);

        //Empaqueto y devuelvo el resultado
        return new Resultado(pA, pB, distanciaReal, calculos);
    }
}