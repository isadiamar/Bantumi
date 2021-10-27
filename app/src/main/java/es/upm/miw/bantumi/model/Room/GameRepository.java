package es.upm.miw.bantumi.model.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class GameRepository {

    private GameDAO gameDAO;
    private LiveData<List<Game>> game;

    public GameRepository(Application application) {
        GameRoomDatabase db = GameRoomDatabase.getDatabase(application);
        this.gameDAO = db.gameDAO();
        this.game = gameDAO.getAll();
    }

    public LiveData<List<Game>> getAllGames() {
        return game;
    }

    public long insert(Game game) {
        return gameDAO.insert(game);
    }

    public void deleteAll() {
        gameDAO.deleteAll();
    }

}
