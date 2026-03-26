# 🗺️ Analizador y Optimizador Algorítmico Geoespacial

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Algoritmia](https://img.shields.io/badge/Algorithms-O(n%20log%20n)-blue?style=for-the-badge)

Este repositorio contiene un motor de búsqueda de proximidad desarrollado en Java, diseñado para procesar conjuntos de datos geoespaciales masivos (formato TSPLIB) y calcular la distancia mínima euclídea entre coordenadas.

## 🎯 Objetivo del Proyecto

El objetivo principal de este desarrollo es demostrar la aplicación práctica de la teoría de complejidad algorítmica, comparando el rendimiento temporal y computacional de diferentes estrategias de búsqueda frente a volúmenes de datos crecientes.

Se ha logrado reducir la complejidad del algoritmo de fuerza bruta original ($O(n^2)$) a una solución altamente eficiente ($O(n \log n)$).

## ⚙️ Estrategias Implementadas y Auditadas

El sistema implementa, prueba y grafica las siguientes 4 aproximaciones algorítmicas sobre los mismos *datasets* para asegurar la precisión del *benchmarking*:

1. **Búsqueda Exhaustiva (Fuerza Bruta):** Complejidad $O(n^2)$.
2. **Búsqueda Exhaustiva con Poda:** Implementación de heurísticas para descartar cálculos redundantes ordenando previamente por un eje de coordenadas.
3. **Técnica *Divide y Vencerás* (D&C):** Partición recursiva del plano espacial. Complejidad $O(n \log n)$.
4. ***Divide y Vencerás* Optimizado:** Mejora en la zona intermedia (franja $\delta$) para prevenir degradaciones de rendimiento en el peor caso teórico (puntos alineados verticalmente).

## 🛠️ Tecnologías y Patrones

* **Lenguaje:** Java Orientado a Objetos.
* **Ordenación:** Integración de algoritmos de ordenación avanzada (`QuickSort` / `HeapSort`) como paso previo a las estrategias de poda.
* **Estructuras de Datos:** Parseo y procesamiento en memoria dinámica de ficheros `.tsp` estándar de la industria (TSPLIB).
* **Análisis Experimental:** Pruebas de estrés con conjuntos de hasta 5.000 puntos para simular escenarios de caso medio y peor caso matemático.

## 💡 Habilidades Demostradas (Soft & Hard Skills)

* **Pensamiento Analítico:** Capacidad para evaluar el coste computacional de múltiples soluciones a un mismo problema de negocio.
* **Orientación a Resultados (Eficiencia):** Búsqueda de la optimización absoluta del código por encima de la primera solución funcional.
* **Ingeniería de Software:** Separación clara entre la lógica del algoritmo, la gestión de ficheros y el benchmarking empírico.

---
*Este proyecto es parte del portafolio técnico de **Álvaro Zarzuela Moncada**.*
