package es.upm.miw.bantumi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import es.upm.miw.bantumi.model.BantumiViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String FILE_NAME = "BantumiGameData.txt";
    protected final String LOG_TAG = "MiW";
    Button buttonReset;
    JuegoBantumi juegoBantumi;
    BantumiViewModel bantumiVM;
    int numInicialSemillas;

    @Override
    public void onClick(View v) {
        if (v.equals(buttonReset)) {
            new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numInicialSemillas = getResources().getInteger(R.integer.intNumInicialSemillas);
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, JuegoBantumi.Turn.turnJ1, numInicialSemillas);

        crearObservadores();
        reset();
    }

    private void reset() {
        buttonReset = findViewById(R.id.buttonReset);

        buttonReset.setOnClickListener(this);

    }

    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POS; i++) {
            int finalI = i;
            bantumiVM.getNumSeeds(i).observe(    // Huecos y almacenes
                    this,
                    numSeeds -> mostrarValor(finalI, juegoBantumi.getSeeds(finalI)));
        }
        bantumiVM.getTurn().observe(   // Turno
                this,
                turn -> marcarTurno(juegoBantumi.currentTurn())
        );
    }

    private void marcarTurno(@NonNull JuegoBantumi.Turn turnoActual) {
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnJ1:
                tvJugador1.setTextColor(getColor(R.color.blue_violet));
                tvJugador2.setTextColor(getColor(R.color.black));
                break;
            case turnJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.pink_200));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
//            case R.id.opcAjustes: // @todo Preferencias
//                startActivity(new Intent(this, BantumiPrefs.class));
//                return true;
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;

            case R.id.opcGuardarPartida:
                save(findViewById(R.id.opcGuardarPartida));
                return true;

            case R.id.opcRecuperarPartida:
                load(findViewById(R.id.opcRecuperarPartida));
                return true;

            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }

    private void save(View v) {
        String batumiSerialized = this.juegoBantumi.serializeGame();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(batumiSerialized.getBytes());

            Toast.makeText(MainActivity.this, "Saved ", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void load(View v) {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            juegoBantumi.deserializeGame(sb.toString());
            Toast.makeText(MainActivity.this, "Load", Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fis != null;
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.currentTurn()) {
            case turnJ1:
                juegoBantumi.play(num);
                break;
            case turnJ2:
                juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
        }
        if (juegoBantumi.gameIsEnded()) {
            finJuego();
        }
    }

    /**
     * Elige una posición aleatoria del campo del jugador2 y realiza la siembra
     * Si mantiene turno -> vuelve a jugar
     */
    void juegaComputador() {
        while (juegoBantumi.currentTurn() == JuegoBantumi.Turn.turnJ2) {
            int pos = 7 + (int) (Math.random() * 6);    // posición aleatoria
            Log.i(LOG_TAG, "juegaComputador(), pos=" + pos);
            if (juegoBantumi.getSeeds(pos) != 0 && (pos < 13)) {
                juegoBantumi.play(pos);
            } else {
                Log.i(LOG_TAG, "\t posición vacía");
            }
        }
    }

    private void finJuego() {
        String texto = (juegoBantumi.getSeeds(6) > 6 * numInicialSemillas)
                ? "Gana Jugador 1"
                : "Gana Jugador 2";
        Snackbar.make(
                findViewById(android.R.id.content),
                texto,
                Snackbar.LENGTH_LONG
        )
                .show();

        // @TODO guardar puntuación
        new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

}

