/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author alvar
 */
public class Resultado {
    private Punto p1;
    private Punto p2;
    private double distanciaMinima;
    private long calculosDistancia; // Para la comparativa de eficiencia

    public Resultado(Punto p1, Punto p2, double distancia, long calculos) {
        this.p1 = p1;
        this.p2 = p2;
        this.distanciaMinima = distancia;
        this.calculosDistancia = calculos;
    }

    public Punto getP1() { return p1; }
    public Punto getP2() { return p2; }
    public double getDistancia() { return distanciaMinima; }
    public long getCalculos() { return calculosDistancia; }
}