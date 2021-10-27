package es.upm.miw.bantumi.model.Room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GameViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private LiveData<List<Game>> allGames;

    public GameViewModel(@NonNull Application application) {
        super(application);
        gameRepository = new GameRepository(application);
        allGames = gameRepository.getAllGames();
    }

    public LiveData<List<Game>> getAllGames() {
        return allGames;
    }

    public void insert(Game game) {
        gameRepository.insert(game);
    }

    public void deleteAll() {
        gameRepository.deleteAll();
    }

}
