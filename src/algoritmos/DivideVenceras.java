package algoritmos;

import modelo.Punto;
import modelo.Resultado;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author alvar
 */
public class DivideVenceras implements Algoritmo {

    private long calculos; 

    @Override
    public Resultado resolver(Punto[] puntosOriginales) {
        
        //Clonamos y ordenamos el array.
        Punto[] puntos = puntosOriginales.clone();
        Arrays.sort(puntos, (p1, p2) -> Double.compare(p1.getX(), p2.getX()));

        calculos = 0;
        return algoritmoRecursivo(puntos, 0, puntos.length - 1);
    }

    private Resultado algoritmoRecursivo(Punto[] puntos, int izq, int der) {
        // Calculamos cuántos puntos tenemos en este trozo
        int n = der - izq + 1;

        //Si son pocos puntos...
        if (n <= 3) {
            return fuerzaBruta(puntos, izq, der);
        }

        //Buscamos la mitad
        int medio = (izq + der) / 2;
        double xMedio = puntos[medio].getX(); 

        //Llamamos recursivamente a los dos lados
        Resultado resIzq = algoritmoRecursivo(puntos, izq, medio);
        Resultado resDer = algoritmoRecursivo(puntos, medio + 1, der);

        //¿Distancia más pequeña en hijos?
        Resultado mejorResultado;
        double distanciaMinima;

        if (resIzq.getDistancia() < resDer.getDistancia()) {
            mejorResultado = resIzq;
        } else {
            mejorResultado = resDer;
        }
        distanciaMinima = mejorResultado.getDistancia();

        // Ahora miramos la Franja central:
        // Puede que haya dos puntos, uno a cada lado de la línea, que estén más cerca
        // que 'distanciaMinima'.
        ArrayList<Punto> puntosEnFranja = new ArrayList<>();
        
        for (int i = izq; i <= der; i++) {
            //Buscamos los puntos que esten cerca de la linea divisoria únicamente
            if (Math.abs(puntos[i].getX() - xMedio) < distanciaMinima) {
                puntosEnFranja.add(puntos[i]);
            }
        }

        // Recorremos la franja buscando si hay una pareja mejor.
        //Comparamos con todo los que puntos que hay dentro de la franja
        for (int i = 0; i < puntosEnFranja.size(); i++) {
            for (int j = i + 1; j < puntosEnFranja.size(); j++) {
                Punto p1 = puntosEnFranja.get(i);
                Punto p2 = puntosEnFranja.get(j);

                // Calculamos distancia
                double dist = p1.distancia(p2);
                calculos++; 

                // Si encontramos una pareja mejor, actualizamos
                if (dist < distanciaMinima) {
                    distanciaMinima = dist;
                    mejorResultado = new Resultado(p1, p2, distanciaMinima, 0);
                }
            }
        }

        // Devolvemos el ganador de esta ronda y los calculos realizados el final
        return new Resultado(mejorResultado.getP1(), mejorResultado.getP2(), distanciaMinima, calculos);
    }

    // Método auxiliar para cuando quedan pocos puntos
    private Resultado fuerzaBruta(Punto[] puntos, int izq, int der) {
        double minD = Double.MAX_VALUE;
        Punto pA = null;
        Punto pB = null;

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
        
        // Si el array solo tiene 1 elementos --> devolvemos infinito
        if (pA == null) return new Resultado(null, null, Double.MAX_VALUE, 0);
        
        return new Resultado(pA, pB, minD, 0);
    }
}