package espol.poo.ed_p3_grupo06;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rgSimbolo, rgTurno;
    private Button btnJugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgSimbolo = findViewById(R.id.rgSimbolo);
        rgTurno = findViewById(R.id.rgTurno);
        btnJugar = findViewById(R.id.btnJugar);

        btnJugar.setOnClickListener(v -> {
            // 1. Obtener símbolo elegido
            char simboloHumano = rgSimbolo.getCheckedRadioButtonId() == R.id.rbX ? 'X' : 'O';

            // 2. Obtener quién inicia
            boolean iniciaHumano = rgTurno.getCheckedRadioButtonId() == R.id.rbYo;

            // 3. Enviar datos a la siguiente pantalla
            Intent intent = new Intent(MainActivity.this, JuegoActivity.class);
            intent.putExtra("SIMBOLO_HUMANO", simboloHumano);
            intent.putExtra("INICIA_HUMANO", iniciaHumano);
            startActivity(intent);
        });
    }
}