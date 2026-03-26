package vista;

import modelo.Punto;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 *
 * @author alvar
 */

public class PanelPuntos extends JPanel {

    //Datos
    private Punto[] puntos;
    private Punto par1, par2; // La solución que devolvemos

    // Variables de Zoom y Pan
    private double zoomFactor = 1.0;
    private double xOffset = 0;
    private double yOffset = 0;
    private int startX, startY;

    //Variables de Escala 
    private double minX, maxX, minY, maxY;
    private double factorEscalaX, factorEscalaY;
    private final int MARGEN = 40; // Margen estético alrededor de los puntos

    // --- CONSTRUCTOR ---
    public PanelPuntos() {
        this.setBackground(Color.WHITE);
        initZoomListeners(); 
    }

    // --- MÉTODOS DE DATOS ---
    public void mostrarPuntos(Punto[] puntos) {
        this.puntos = puntos;
        this.par1 = null;
        this.par2 = null;
        calcularLimites();
        centrarVista(); // Reseteamos cámara al cargar nuevos datos
        repaint();
    }

    public void mostrarSolucion(Punto p1, Punto p2) {
        this.par1 = p1;
        this.par2 = p2;
        repaint();
    }

    // --- GESTIÓN DE RATÓN (ZOOM Y PAN) ---
    private void initZoomListeners() {
        // 1. Rueda = Zoom
        this.addMouseWheelListener(e -> {
            // Hacia abajo aleja, hacia arriba acerca
            double factor = (e.getPreciseWheelRotation() < 0) ? 1.1 : 0.9;
            double nuevoZoom = zoomFactor * factor;

            // Limitamos el zoom (entre x0.1 y x1000)
            if (nuevoZoom >= 0.1 && nuevoZoom <= 1000.0) {
                // Zoom hacia el puntero del ratón
                double mouseX = e.getX();
                double mouseY = e.getY();
                xOffset = (xOffset - mouseX) * factor + mouseX;
                yOffset = (yOffset - mouseY) * factor + mouseY;
                
                zoomFactor = nuevoZoom;
                repaint();
            }
        });

        // 2. Clic = Agarrar
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) centrarVista(); // Doble clic resetea
            }
        });

        // 3. Arrastrar = Mover
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                xOffset += (e.getX() - startX);
                yOffset += (e.getY() - startY);
                startX = e.getX();
                startY = e.getY();
                repaint();
            }
        });
    }

    private void centrarVista() {
        zoomFactor = 1.0;
        xOffset = 0;
        yOffset = 0;
        repaint();
    }

    // --- DIBUJADO (La parte estética) ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // 1. Fondo Blanco Limpio
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // --- PANTALLA DE BIENVENIDA (Si no hay datos) ---
        if (puntos == null || puntos.length == 0) {
            dibujarBienvenida(g2);
            return; 
        }

        // --- CÁMARA ---
        AffineTransform at = new AffineTransform();
        at.translate(xOffset, yOffset);
        at.scale(zoomFactor, zoomFactor);
        g2.transform(at);

        actualizarFactoresEscala();

        // --- TAMAÑOS INVERSOS (Para que no cambie el grosor al hacer zoom) ---
        double escalaInversa = 1.0 / zoomFactor;
        
        double radioPunto = 4.0 * escalaInversa; 
        double radioSolucion = 6.0 * escalaInversa; 
        double grosorLinea = 1.5 * escalaInversa; 
        
        // Fuentes adaptativas
        float sizeFuente = (float)(10.0 * escalaInversa);
        if (sizeFuente < 0.1f) sizeFuente = 0.1f;
        Font fuenteNormal = new Font("SansSerif", Font.PLAIN, 1).deriveFont(sizeFuente);
        
        float sizeFuenteSol = (float)(12.0 * escalaInversa);
        if (sizeFuenteSol < 0.1f) sizeFuenteSol = 0.1f;
        Font fuenteSolucion = new Font("SansSerif", Font.BOLD, 1).deriveFont(sizeFuenteSol);

        // ==========================================================
        // CAPA 1: PUNTOS AZULES
        // ==========================================================
        g2.setFont(fuenteNormal);
        for (Punto p : puntos) {
            // Si es parte de la solución, lo saltamos para pintarlo después encima
            if ((par1 != null && p.getId() == par1.getId()) || 
                (par2 != null && p.getId() == par2.getId())) {
                continue; 
            }

            double x = escalarX(p.getX());
            double y = escalarY(p.getY());

            g2.setColor(new Color(60, 100, 160)); 
            // Usamos Double para precisión infinita
            Shape circulo = new Ellipse2D.Double(x - radioPunto, y - radioPunto, radioPunto * 2.0, radioPunto * 2.0);
            g2.fill(circulo);

            // Texto (Solo si no hay saturación o hay mucho zoom)
            if (puntos.length <= 100 || zoomFactor > 2.0) {
                g2.setColor(Color.GRAY);
                g2.drawString(String.valueOf(p.getId()), 
                             (float)(x + radioPunto + (2 * escalaInversa)), 
                             (float)(y - radioPunto - (2 * escalaInversa)));
            }
        }

        // ==========================================================
        // CAPA 2: SOLUCIÓN (ROJA)
        // ==========================================================
        if (par1 != null && par2 != null) {
            double x1 = escalarX(par1.getX());
            double y1 = escalarY(par1.getY());
            double x2 = escalarX(par2.getX());
            double y2 = escalarY(par2.getY());

            // 1. Línea de unión
            g2.setColor(new Color(220, 20, 60)); //Rojo
            g2.setStroke(new BasicStroke((float)grosorLinea));
            g2.draw(new Line2D.Double(x1, y1, x2, y2));

            // 2. Puntos Rojos destacados
            Shape c1 = new Ellipse2D.Double(x1 - radioSolucion, y1 - radioSolucion, radioSolucion * 2.0, radioSolucion * 2.0);
            Shape c2 = new Ellipse2D.Double(x2 - radioSolucion, y2 - radioSolucion, radioSolucion * 2.0, radioSolucion * 2.0);
            g2.fill(c1);
            g2.fill(c2);

            // 3. Etiquetas de texto
            g2.setColor(Color.BLACK);
            g2.setFont(fuenteSolucion);
            float offset = (float)(radioSolucion + (4 * escalaInversa));
            
            g2.drawString("ID:" + par1.getId(), (float)x1 + offset, (float)y1 - offset);
            g2.drawString("ID:" + par2.getId(), (float)x2 + offset, (float)y2 - offset);
            
            // Distancia (Verde)
            double midX = (x1 + x2) / 2.0;
            double midY = (y1 + y2) / 2.0;
            g2.setColor(new Color(0, 140, 0));
            String distTxt = String.format("%.8f", par1.distancia(par2)); 
            g2.drawString(distTxt, (float)(midX + offset), (float)midY);
        }
    }
    
    //Mensaje de bienvenida, nada más abrir
    private void dibujarBienvenida(Graphics2D g2) {
        String titulo = "Práctica 1 AMC: Pares más Cercanos";
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 24);
        g2.setFont(fuenteTitulo);
        g2.setColor(new Color(100, 100, 100)); // Gris oscuro profesional
        
        FontMetrics fm = g2.getFontMetrics();
        int xTitulo = (getWidth() - fm.stringWidth(titulo)) / 2;
        int yCentro = getHeight() / 2;
        
        g2.drawString(titulo, xTitulo, yCentro - 20);
        
        String subtitulo = "Carga un fichero o genera puntos aleatorios para comenzar";
        Font fuenteSub = new Font("Segoe UI", Font.PLAIN, 14);
        g2.setFont(fuenteSub);
        g2.setColor(new Color(150, 150, 150)); // Gris más suave
        
        fm = g2.getFontMetrics();
        int xSub = (getWidth() - fm.stringWidth(subtitulo)) / 2;
        
        g2.drawString(subtitulo, xSub, yCentro + 15);
        
        // Línea decorativa
        g2.setColor(new Color(220, 220, 220));
        g2.drawLine(getWidth()/4, yCentro + 40, (getWidth()/4)*3, yCentro + 40);
    }

    // --- MÉTODOS AUXILIARES MATEMÁTICOS ---
    private void calcularLimites() {
        if (puntos == null || puntos.length == 0) return;
        
        minX = Double.MAX_VALUE; maxX = -Double.MAX_VALUE;
        minY = Double.MAX_VALUE; maxY = -Double.MAX_VALUE;
        
        for (Punto p : puntos) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() < minY) minY = p.getY();
            if (p.getY() > maxY) maxY = p.getY();
        }
    }

    private void actualizarFactoresEscala() {
        double anchoPanel = getWidth() - (2 * MARGEN);
        double altoPanel = getHeight() - (2 * MARGEN);
        
        double anchoMapa = maxX - minX;
        double altoMapa = maxY - minY;
        
        if (anchoMapa == 0) anchoMapa = 1.0;
        if (altoMapa == 0) altoMapa = 1.0;
        
        factorEscalaX = anchoPanel / anchoMapa;
        factorEscalaY = altoPanel / altoMapa;
    }

    private int escalarX(double x) { 
        return MARGEN + (int) ((x - minX) * factorEscalaX); 
    }
    
    private int escalarY(double y) { 
        return MARGEN + (int) ((y - minY) * factorEscalaY); 
    }
}