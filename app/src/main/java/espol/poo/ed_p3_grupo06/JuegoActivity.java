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
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

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
    
    //LOGICA DE PERSISTENCIA (REPOSITORIO LOCAL)
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
            }
        }
        actualizarInterfaz();
        verificarTurno();
    }
    
    private void limpiarPartidaLocal() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

//LOGICA DEL JUEGO 
    private void procesarJugadaHumano(int indice) {
        if (turnoActual != turnoHumano || juegoTerminado) return;

        int fila = indice / 3;
        int col = indice % 3;

        if (tablero.getCasilla(fila, col) == '-') {
            tablero.hacerJugada(fila, col, turnoHumano);
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
            tablero.hacerJugada(mejorJugada.getFilaJugada(), mejorJugada.getColJugada(), turnoPC);
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
                // El delay en este caso es para dar un pequeño instante de tiempo y que la pc no juegue al instante.
                tvEstado.postDelayed(this::procesarJugadaPC, 300);
            }
        }
    }

    private void finalizarPartida() {
        juegoTerminado = true;
        limpiarPartidaLocal(); // Borramos el registro para que la próxima vez partida la comience desde 0
    }

    private void actualizarInterfaz() {
        List<Character> celdas = tablero.getCeldas();
        for (int i = 0; i < 9; i++) {
            char simbolo = celdas.get(i);
            botonesTablero.get(i).setText(simbolo == '-' ? "" : String.valueOf(simbolo));
        }
    }
}
