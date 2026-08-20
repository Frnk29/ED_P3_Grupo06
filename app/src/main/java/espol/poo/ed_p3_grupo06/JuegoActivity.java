package espol.poo.ed_p3_grupo06;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import espol.poo.ed_p3_grupo06.modelo.Tablero;
import espol.poo.ed_p3_grupo06.modelo.MiniMax;
import espol.poo.ed_p3_grupo06.modelo.ArbolNario;
import espol.poo.ed_p3_grupo06.modelo.Nodo;

public class JuegoActivity extends AppCompatActivity{
    private Tablero tablero;
    private MiniMax minimax;
    private char turnoHumano;
    private char turnoPC;
    private char turnoActual;
    private boolean juegoTerminado = false;

    private TextView tvEstado;
    private List<Button> botonesTablero;
    private Button btnVolver;

    private TextView tvHistorial;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        tvHistorial = findViewById(R.id.tvHistorialJugadas);
        tablero = new Tablero();
        minimax = new MiniMax();
        botonesTablero = new ArrayList<>();
        prefs = getSharedPreferences("RepositorioPartida", MODE_PRIVATE);

        tvEstado = findViewById(R.id.tvEstado);
        btnVolver = findViewById(R.id.btnVolver);

        configurarBotones();

        if (prefs.getBoolean("existe_guardado", false)) {
            cargarPartidaLocal();
        } else {
            turnoHumano = getIntent().getCharExtra("SIMBOLO_HUMANO", 'X');
            turnoPC = (turnoHumano == 'X') ? 'O' : 'X';
            boolean iniciaHumano = getIntent().getBooleanExtra("INICIA_HUMANO", true);
            turnoActual = iniciaHumano ? turnoHumano : turnoPC;
            verificarTurno();
        }
    }

    private void configurarBotones() {
        for (int i = 0; i < 9; i++) {
            int resID = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button btn = findViewById(resID);
            botonesTablero.add(btn);
            final int indice = i;
            btn.setOnClickListener(v -> procesarJugadaHumano(indice));
        }

        btnVolver.setOnClickListener(v -> {
            if (!juegoTerminado) {
                guardarPartidaLocal();
            } else {
                limpiarPartidaLocal();
            }
            finish();
        });
    }

    // LOGICA DE PERSISTENCIA
    private void guardarPartidaLocal() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("existe_guardado", true);
        editor.putString("turnoHumano", String.valueOf(turnoHumano));
        editor.putString("turnoPC", String.valueOf(turnoPC));
        editor.putString("turnoActual", String.valueOf(turnoActual));

        StringBuilder sb = new StringBuilder();
        for (char c : tablero.getCeldas()) {
            sb.append(c);
        }
        editor.putString("estadoTablero", sb.toString());
        editor.apply();
    }

    private void cargarPartidaLocal() {
        turnoHumano = prefs.getString("turnoHumano", "X").charAt(0);
        turnoPC = prefs.getString("turnoPC", "O").charAt(0);
        turnoActual = prefs.getString("turnoActual", "X").charAt(0);

        String estadoTablero = prefs.getString("estadoTablero", "---------");
        for (int i = 0; i < 9; i++) {
            if (estadoTablero.charAt(i) != '-') {
                int fila = i / 3;
                int col = i % 3;
                tablero.hacerJugada(fila, col, estadoTablero.charAt(i));
                tablero.registrarJugadaHistorial(fila, col, estadoTablero.charAt(i));
            }
        }
        actualizarInterfaz();
        actualizarHistorialVisual();
        verificarTurno();
    }

    private void limpiarPartidaLocal() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // LOGICA DEL JUEGO
    private void procesarJugadaHumano(int indice) {
        if (turnoActual != turnoHumano || juegoTerminado) return;

        int fila = indice / 3;
        int col = indice % 3;

        if (tablero.getCasilla(fila, col) == '-') {
            tablero.hacerJugada(fila, col, turnoHumano);
            tablero.registrarJugadaHistorial(fila, col, turnoHumano); // Guardamos la jugada
            agregarJugadaAlHistorial(fila, col, turnoHumano); // Añadimos al texto con append()

            turnoActual = turnoPC;
            actualizarInterfaz();
            verificarTurno();
        }
    }

    private void procesarJugadaPC() {
        tvEstado.setText("La PC está pensando...");
        ArbolNario arbol = new ArbolNario(tablero, turnoPC);
        arbol.generarArbol(turnoPC, turnoHumano);
        Nodo mejorJugada = minimax.encontrarMejorMovimiento(arbol, turnoPC);

        if (mejorJugada != null) {
            int filaPC = mejorJugada.getFilaJugada();
            int colPC = mejorJugada.getColJugada();

            tablero.hacerJugada(filaPC, colPC, turnoPC);
            tablero.registrarJugadaHistorial(filaPC, colPC, turnoPC); // Guardamos la jugada de la PC
            agregarJugadaAlHistorial(filaPC, colPC, turnoPC); // Añadimos al texto con append()
        }

        turnoActual = turnoHumano;
        actualizarInterfaz();
        verificarTurno();
    }

    private void verificarTurno() {
        if (tablero.hayGanador(turnoHumano)) {
            tvEstado.setText("¡Felicidades, ganaste!");
            finalizarPartida();
        } else if (tablero.hayGanador(turnoPC)) {
            tvEstado.setText("La Computadora Gana.");
            finalizarPartida();
        } else if (tablero.estaLleno()) {
            tvEstado.setText("¡Es un Empate!");
            finalizarPartida();
        } else {
            if (turnoActual == turnoHumano) {
                tvEstado.setText("Es tu turno (" + turnoHumano + ")");
            } else {
                tvEstado.postDelayed(this::procesarJugadaPC, 300);
            }
        }
    }

    private void finalizarPartida() {
        juegoTerminado = true;
        limpiarPartidaLocal();
    }

    private void actualizarInterfaz() {
        List<Character> celdas = tablero.getCeldas();
        for (int i = 0; i < 9; i++) {
            char simbolo = celdas.get(i);
            botonesTablero.get(i).setText(simbolo == '-' ? "" : String.valueOf(simbolo));
        }
    }

    // Método para reconstruir TODO el texto (solo se usa al cargar una partida guardada)
    private void actualizarHistorialVisual() {
        StringBuilder sb = new StringBuilder("Historial de Jugadas:\n");
        int paso = 1;

        for (Tablero.Jugada j : tablero.getHistorialReal()) {
            int fila = j.index / 3;
            int col = j.index % 3;
            String jugador = (j.simbol == turnoPC) ? "PC (" + j.simbol + ")" : "Tú (" + j.simbol + ")";

            sb.append(paso).append(". ").append(jugador)
                    .append(" -> Fila ").append(fila).append(", Columna ").append(col).append("\n");
            paso++;
        }
        tvHistorial.setText(sb.toString());
    }

    private void agregarJugadaAlHistorial(int fila, int col, char simboloJugador) {
        String jugador = (simboloJugador == turnoPC) ? "PC (" + simboloJugador + ")" : "Tú (" + simboloJugador + ")";
        int paso = tablero.getHistorialReal().size();

        if (paso == 1) {
            tvHistorial.setText("Historial de Jugadas:\n");
        }

        tvHistorial.append(paso + ". " + jugador + " -> Fila " + fila + ", Columna " + col + "\n");
    }
}