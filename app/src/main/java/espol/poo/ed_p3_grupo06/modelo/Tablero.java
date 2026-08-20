package espol.poo.ed_p3_grupo06.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Queue;

public class Tablero {
    private Queue<Jugada> historialReal = new ArrayDeque<>();
    private List<Character> celdas;

    public Tablero() {
        celdas = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            celdas.add('-');
        }
    }

    // Constructor para clonar (NOTA: El clon no copia el historial para no afectar la interfaz)
    private Tablero(List<Character> celdasCopia) {
        this.celdas = new ArrayList<>(celdasCopia);
    }

    public class Jugada{
        public int index;
        public char simbol;

        public Jugada(int index, char simbol){
            this.index = index;
            this.simbol = simbol;
        }
    }

    public boolean hacerJugada(int fila, int col, char simbolo) {
        int indice = (fila * 3) + col;

        if (indice >= 0 && indice < 9 && celdas.get(indice) == '-') {
            celdas.set(indice, simbolo);
            return true;
        }
        return false;
    }

    public void registrarJugadaHistorial(int fila, int col, char simbolo) {
        int indice = (fila * 3) + col;
        historialReal.offer(new Jugada(indice, simbolo));
    }

    public Queue<Jugada> getHistorialReal() {
        return historialReal;
    }

    public Tablero clonar() {
        return new Tablero(this.celdas);
    }

    // Getters
    public char getCasilla(int fila, int col) {
        int indice = (fila * 3) + col;
        return celdas.get(indice);
    }

    public List<Character> getCeldas() {
        return celdas;
    }

    public int calcularUtilidad(char jugadorTurno) {
        char oponente = (jugadorTurno == 'X') ? 'O' : 'X';

        if (hayGanador(jugadorTurno)) {
            return 100; // Premia con un número inmenso si la máquina gana
        }
        // El algoritmo MiniMax al simular una posible victoria del oponente vera el -100
        // el cual es un gran numero en contra por lo cual hara lo necesario para bloquear
        // de inmediato la jugada que lo hace peligrar
        if (hayGanador(oponente)) {
            return -100; // Castiga con un número negativo inmenso si el usuario gana
        }
        int pJugador = contarLineasDisponibles(jugadorTurno, oponente);
        int pOponente = contarLineasDisponibles(oponente, jugadorTurno);
        return pJugador - pOponente;
    }

    private int contarLineasDisponibles(char jugador, char rival) {
        int lineasDisponibles = 0;
        int[][] lineas = {
                { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, //filas
                { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, //columnas
                { 0, 4, 8 }, { 2, 4, 6 } //diagonales
        };
        for (int[] linea : lineas) {
            if (celdas.get(linea[0]) != rival &&
                    celdas.get(linea[1]) != rival &&
                    celdas.get(linea[2]) != rival) {
                lineasDisponibles++;
            }
        }
        return lineasDisponibles;
    }

    // Método para verificar si existe un ganador
    public boolean hayGanador(char simbolo) {
        int[][] lineasGanadoras = {
                { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, // Filas
                { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, // Columnas
                { 0, 4, 8 }, { 2, 4, 6 } // Diagonales
        };

        for (int[] linea : lineasGanadoras) {
            if (celdas.get(linea[0]) == simbolo &&
                    celdas.get(linea[1]) == simbolo &&
                    celdas.get(linea[2]) == simbolo) {
                return true; // Encontró 3 alineadas con el mismo símbolo
            }
        }
        return false;
    }

    // Método auxiliar para verificar ganador sin especificar símbolo
    public boolean hayGanador() {
        return hayGanador('X') || hayGanador('O');
    }

    public boolean estaLleno(){
        return !celdas.contains('-');
    }

    public void imprimirTablero() {
        for (int i = 0; i < 9; i++) {
            System.out.print(celdas.get(i) + " ");
            if ((i + 1) % 3 == 0) {
                System.out.println();
            }
        }
        System.out.println("------");
    }
}