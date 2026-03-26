/*
 * Algoritmo Divide y Vencerás Mejorado (Ordenación Y en la franja)
 */
package algoritmos;

import modelo.Punto;
import modelo.Resultado;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author alvar
 */

public class DyVMejorado implements Algoritmo {

    private long calculos;

    @Override
    public Resultado resolver(Punto[] puntosOriginales) {
        // Trabajamos sobre una copia para no modificar el original
        Punto[] puntos = puntosOriginales.clone();
        
        // Ordenamos por coordenada X antes de empezar
        Arrays.sort(puntos, (p1, p2) -> Double.compare(p1.getX(), p2.getX()));
        
        calculos = 0;
        return algoritmoRecursivo(puntos, 0, puntos.length - 1);
    }

    private Resultado algoritmoRecursivo(Punto[] puntos, int izq, int der) {
        int n = der - izq + 1;

        //Si hay pocos puntos, fuerza bruta
        if (n <= 3) {
            return fuerzaBruta(puntos, izq, der);
        }

        // División
        int medio = (izq + der) / 2;
        double xMedio = puntos[medio].getX();

        //Recursividad (Izquierda y Derecha)
        Resultado resIzq = algoritmoRecursivo(puntos, izq, medio);
        Resultado resDer = algoritmoRecursivo(puntos, medio + 1, der);

        //Nos quedamos con la mejor distancia de los dos lados
        Resultado mejorResultado;
        if (resIzq.getDistancia() < resDer.getDistancia()) {
            mejorResultado = resIzq;
        } else {
            mejorResultado = resDer;
        }
        double dMin = mejorResultado.getDistancia();

        
        // 1. Seleccionamos los puntos cercanos a la línea divisoria
        ArrayList<Punto> franja = new ArrayList<>();
        for (int i = izq; i <= der; i++) {
            if (Math.abs(puntos[i].getX() - xMedio) < dMin) {
                franja.add(puntos[i]);
            }
        }

        // 2. Ordenamos la franja por coordenada Y 
        Collections.sort(franja, (p1, p2) -> Double.compare(p1.getY(), p2.getY()));

        // 3. Buscamos en la franja
        for (int i = 0; i < franja.size(); i++) {
            // Solo necesitamos comparar con los siguientes vecinos
            for (int j = i + 1; j < franja.size(); j++) {
                Punto p1 = franja.get(i);
                Punto p2 = franja.get(j);

                // Si la distancia vertical ya es mayor que dMin, paramos 
                if ((p2.getY() - p1.getY()) >= dMin) {
                    break; 
                }

                double dist = p1.distancia(p2);
                calculos++;

                if (dist < dMin) {
                    dMin = dist;
                    mejorResultado = new Resultado(p1, p2, dMin, 0);
                }
            }
        }

        return new Resultado(mejorResultado.getP1(), mejorResultado.getP2(), dMin, calculos);
    }

    // Método auxiliar para casos pequeños
    private Resultado fuerzaBruta(Punto[] puntos, int izq, int der) {
        double minD = Double.MAX_VALUE;
        Punto pA = null, pB = null;

        for (int i = izq; i <= der; i++) {
            for (int j = i + 1; j <= der; j++) {
                double d = puntos[i].distancia(puntos[j]);
                calculos++;
                if (d < minD) {
                    minD = d;
                    pA = puntos[i];
                    pB = puntos[j];
                }
            }
        }
        
        if (pA == null) return new Resultado(null, null, Double.MAX_VALUE, 0);
        return new Resultado(pA, pB, minD, 0);
    }
}