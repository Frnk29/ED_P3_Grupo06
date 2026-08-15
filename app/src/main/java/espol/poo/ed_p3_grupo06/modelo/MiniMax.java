package espol.poo.ed_p3_grupo06.modelo;
import java.util.*;
public class MiniMax {
    // Guarda los tableros que ya analizó para no repetir el cálculo matemático.
    private Map<String, Integer> memoriaDeTableros;

    public MiniMax() {
        this.memoriaDeTableros = new HashMap<>();
    }

    public Nodo encontrarMejorMovimiento(ArbolNario arbol, char simboloComputadora) {
        Nodo raiz = arbol.getRaiz();
        int maxUtilidad = Integer.MIN_VALUE;
        Nodo mejorMovimiento = null;

        memoriaDeTableros.clear();

        // Nivel 1: Recorremos las posibles jugadas iniciales de la computadora
        for (Nodo jugadaComputadora : raiz.getHijos()) {

            if (jugadaComputadora.getHijos().isEmpty()) {
                int utilidadTerminal = recordarOCalcularUtilidad(jugadaComputadora.getEstado(), simboloComputadora);
                jugadaComputadora.setUtilidad(utilidadTerminal);

                if (utilidadTerminal > maxUtilidad) {
                    maxUtilidad = utilidadTerminal;
                    mejorMovimiento = jugadaComputadora;
                }
                continue;
            }

            int minUtilidadFamilia = Integer.MAX_VALUE;

            // Nivel 2: Recorremos las posibles respuestas que nos daría el humano
            for (Nodo respuestaHumano : jugadaComputadora.getHijos()) {

                int utilidadActual = recordarOCalcularUtilidad(respuestaHumano.getEstado(), simboloComputadora);
                respuestaHumano.setUtilidad(utilidadActual);

                if (utilidadActual < minUtilidadFamilia) {
                    minUtilidadFamilia = utilidadActual;
                }
            }

            jugadaComputadora.setUtilidad(minUtilidadFamilia);

            if (minUtilidadFamilia > maxUtilidad) {
                maxUtilidad = minUtilidadFamilia;
                mejorMovimiento = jugadaComputadora;
            }
        }

        return mejorMovimiento;
    }


    private int recordarOCalcularUtilidad(Tablero tablero, char simboloComputadora) {

        StringBuilder sb = new StringBuilder();
        for (char c : tablero.getCeldas()) {
            sb.append(c);
        }
        String fotoDelTablero = sb.toString();

        if (memoriaDeTableros.containsKey(fotoDelTablero)) {
            return memoriaDeTableros.get(fotoDelTablero);
        }
        // 3. Si es un tablero nuevo que nunca había visto, hace el cálculo matemático completo
        int utilidadCalculada = tablero.calcularUtilidad(simboloComputadora);

        // 4. Anota el resultado en su memoria para no tener que volver a calcularlo si lo vuelve a ver
        memoriaDeTableros.put(fotoDelTablero, utilidadCalculada);

        return utilidadCalculada;

    }

}
