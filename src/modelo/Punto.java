/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Random;
/**
 *
 * @author alvar
 */

public class Punto {
    private int id;
    private double x;
    private double y;

    public Punto(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    // Getters
    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }

    //Calcula la distancia Euclídea al cuadrado evitando raices si no son necesarias.
    public double distanciaCuadrado(Punto otro) {
        double dx = this.x - otro.x;
        double dy = this.y - otro.y;
        return dx * dx + dy * dy;
    }


    //Calcula la distancia Euclídea real.
    public double distancia(Punto otro) {
        return Math.sqrt(distanciaCuadrado(otro));
    }

    @Override
    public String toString() {
        return String.format("Punto %d (%.10f, %.10f)", id, x, y);
    }


    public static Punto[] generarDataSet(int n, boolean peorCaso) {
        Punto[] puntos = new Punto[n];
        Random rand = new Random(System.currentTimeMillis());
        

        double x, y, aux1;
        int num, den;

        if (peorCaso) { // PEOR CASO (Misma X)
            for (int i = 0; i < n; i++) {
                aux1 = rand.nextInt(1000) + 7; 
                y = aux1 / ((double) i + 1 + i * 0.100);
                
                num = rand.nextInt(3); 
                
                y += ((i % 500) - num * (rand.nextInt(100)));
                
                x = 1.0; // Todos en la misma vertical X=1
                
                puntos[i] = new Punto(i + 1, x, y);
            }
        } else { // CASO MEDIO (Aleatorio normal)
            for (int i = 0; i < n; i++) {
                num = rand.nextInt(4000) + 1;    
                den = rand.nextInt(11) + 7;      
                
                x = num / ((double) den + 0.37);
                
                y = (rand.nextInt(4000) + 1) / ((double) (rand.nextInt(11) + 7) + 0.37);
                
                puntos[i] = new Punto(i + 1, x, y);
            }
        }
        
        return puntos;
    }
}
