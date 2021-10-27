package es.upm.miw.bantumi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.upm.miw.bantumi.model.Room.Game;
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
}
