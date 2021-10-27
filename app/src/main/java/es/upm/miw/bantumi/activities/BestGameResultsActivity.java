package es.upm.miw.bantumi.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.dialogs.DeleteAllAlertDialog;
import es.upm.miw.bantumi.model.Room.GameViewModel;
import es.upm.miw.bantumi.views.GameListAdapter;

public class BestGameResultsActivity extends AppCompatActivity {

    GameViewModel gameViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.best_game_results_activity);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add the RecyclerView
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final GameListAdapter adapter = new GameListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gameViewModel = new GameViewModel(getApplication());

        gameViewModel.getAllGames().observe(this, adapter::setGames);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_best_game_results, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.opcDeleteAllGames:
                new DeleteAllAlertDialog().show(getSupportFragmentManager(), "DELETE_ALL_DIALOG");
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

    public void deleteAll(){
        gameViewModel.deleteAll();
    }
}
