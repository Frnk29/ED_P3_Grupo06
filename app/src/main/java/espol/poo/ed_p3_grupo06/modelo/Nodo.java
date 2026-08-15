package espol.poo.ed_p3_grupo06.modelo;

import java.util.*;

public class Nodo {
    private Tablero estado;
    private List<Nodo> hijos;
    private int utilidad;
    private char turnoJugador; // 'X' u 'O' - Quién debe jugar en este estado

    // Coordenadas de la jugada que llevó a este estado (útil para que la PC sepa qué mover)
    private int filaJugada;
    private int colJugada;

    public Nodo(Tablero estado, char turnoJugador) {
        this.estado = estado;
        this.turnoJugador = turnoJugador;
        this.hijos = new ArrayList<>();
        this.utilidad = 0; // Se calculará después
        this.filaJugada = -1;
        this.colJugada = -1;
    }

    // Método para agregar un estado futuro derivado de este
    public void agregarHijo(Nodo hijo) {
        this.hijos.add(hijo);
    }

    // Getters y Setters
    public Tablero getEstado() { return estado; }
    public List<Nodo> getHijos() { return hijos; }

    public int getUtilidad() { return utilidad; }
    public void setUtilidad(int utilidad) { this.utilidad = utilidad; }

    public char getTurnoJugador() { return turnoJugador; }

    public int getFilaJugada() { return filaJugada; }
    public void setFilaJugada(int fila) { this.filaJugada = fila; }

    public int getColJugada() { return colJugada; }
    public void setColJugada(int col) { this.colJugada = col; }
}
