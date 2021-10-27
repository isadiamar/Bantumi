package es.upm.miw.bantumi.model.Room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Game.class}, version = 2, exportSchema = false)
public abstract class GameRoomDatabase extends RoomDatabase {

    public static final String DATABASE = Game.TABLA + ".db";
    private static final int NUMBER_OF_THREADS = 4;

    public abstract GameDAO gameDAO();

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile GameRoomDatabase INSTANCE;
    // Delete all content and repopulate the database whenever the app is started
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    databaseWriteExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            GameDAO dao = INSTANCE.gameDAO();
                        }
                    });
                }
            };

    static GameRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GameRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GameRoomDatabase.class, DATABASE)
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
