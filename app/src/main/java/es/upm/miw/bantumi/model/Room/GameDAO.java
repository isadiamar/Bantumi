package es.upm.miw.bantumi.model.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDAO {
    @Query("SELECT * FROM " + Game.TABLA + " ORDER BY storeWinner DESC LIMIT 10")
    LiveData<List<Game>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Game game);

    @Query("DELETE FROM " + Game.TABLA)
    void deleteAll();


}
