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

    private void limpiarPartidaLocal() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
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
                tvEstado.postDelayed(this::procesarJugadaPC, 300); //Pendiente a la creacion del metodo procesarJugadaPC
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
