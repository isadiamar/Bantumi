package es.upm.miw.bantumi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.upm.miw.bantumi.model.Bantumi.BantumiViewModel;
import es.upm.miw.bantumi.model.Room.Game;
import es.upm.miw.bantumi.model.Room.GameViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String FILE_NAME = "BantumiGameData.txt";
    protected final String LOG_TAG = "MiW";

    JuegoBantumi juegoBantumi;
    BantumiViewModel bantumiVM;

    int initialNumberSeeds;
    Button buttonReset;

    GameViewModel gameViewModel;

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

        initialNumberSeeds = getResources().getInteger(R.integer.intNumInicialSemillas);
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, JuegoBantumi.Turn.turnJ1, initialNumberSeeds);

        gameViewModel = new GameViewModel(getApplication());

        createObservers();
        reset();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String playerName = sharedPref.getString(
                "name",
                getString(R.string.prefTituloNombreJugador)
        );

        TextView player = findViewById(R.id.tvPlayer1);
        player.setText(playerName);

    }

    public void editPreferences(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void createObservers() {
        for (int i = 0; i < JuegoBantumi.NUM_POS; i++) {
            int finalI = i;
            bantumiVM.getNumSeeds(i).observe(    // Huecos y almacenes
                    this,
                    numSeeds -> showValue(finalI, juegoBantumi.getSeeds(finalI)));
        }
        bantumiVM.getTurn().observe(   // Turno
                this,
                turn -> highlightTurn(juegoBantumi.currentTurn())
        );
    }

    private void highlightTurn(@NonNull JuegoBantumi.Turn currentTurn) {
        TextView tvPlayer1 = findViewById(R.id.tvPlayer1);
        TextView tvPlayer2 = findViewById(R.id.tvPlayer2);
        switch (currentTurn) {
            case turnJ1:
                tvPlayer1.setTextColor(getColor(R.color.blue_violet));
                tvPlayer2.setTextColor(getColor(R.color.black));
                break;
            case turnJ2:
                tvPlayer1.setTextColor(getColor(R.color.black));
                tvPlayer2.setTextColor(getColor(R.color.pink_200));
                break;
            default:
                tvPlayer1.setTextColor(getColor(R.color.black));
                tvPlayer2.setTextColor(getColor(R.color.black));
        }
    }

    private void showValue(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);

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
//
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

            case R.id.opcAjustes:
                editPreferences(findViewById(R.id.opcAjustes));
                return true;

            case R.id.opcReiniciarPartida:
                new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
                return true;

            case R.id.opcMejoresResultados:
                startActivity(new Intent(this, BestGameResultsActivity.class));
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
        String bantumiSerialized = this.juegoBantumi.serializeGame();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(bantumiSerialized.getBytes());

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

        //si se hubiera modificado la partida actual, se solicitará confirmación
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

    private void reset() {
        buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(this);
    }

    public void pressedButton(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.currentTurn()) {
            case turnJ1:
                juegoBantumi.play(num);
                break;
            case turnJ2:
                playComputer();
                break;
            default:    // JUEGO TERMINADO
                endOfGame();
        }
        if (juegoBantumi.gameIsEnded()) {
            endOfGame();
        }
    }

    void playComputer() {
        while (juegoBantumi.currentTurn() == JuegoBantumi.Turn.turnJ2) {
            int pos = 7 + (int) (Math.random() * Constants.PLAYER_STORE);    // pos random
            Log.i(LOG_TAG, "playComputer(), pos=" + pos);
            if (juegoBantumi.getSeeds(pos) != 0 && (pos < Constants.CPU_STORE)) {
                juegoBantumi.play(pos);
            } else {
                Log.i(LOG_TAG, "\t empty position");
            }
        }
    }

    private void endOfGame() {
        String texto = (juegoBantumi.getSeeds(Constants.PLAYER_STORE) > 6 * Constants.PLAYER_STORE)
                ? "Gana Jugador 1"
                : "Gana Jugador 2";
        Snackbar.make(
                findViewById(android.R.id.content),
                texto,
                Snackbar.LENGTH_LONG
        )
                .show();

        saveDataGame();
        new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

    void saveDataGame() {

        int storeWinner;

        if (juegoBantumi.getSeeds(Constants.PLAYER_STORE) > 6 * Constants.PLAYER_STORE) {
            storeWinner = this.juegoBantumi.getSeeds(Constants.PLAYER_STORE);
        } else {
            storeWinner = this.juegoBantumi.getSeeds(Constants.CPU_STORE);
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String namePlayer = sharedPref.getString(
                "name",
                getString(R.string.prefTituloNombreJugador)
        );

        int numberTokenLeft = this.juegoBantumi.numTokensLeft();
        int storePlayer = this.juegoBantumi.getSeeds(Constants.PLAYER_STORE);
        int storeCPU = this.juegoBantumi.getSeeds(Constants.CPU_STORE);

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(today);

        this.gameViewModel.insertGame(
                new Game(namePlayer, formattedDate,
                        numberTokenLeft, storeCPU, storePlayer, storeWinner)
        );

    }

}

