package es.upm.miw.bantumi.model.Room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = Game.TABLA)
public class Game {

    static public final String TABLA = "game";

    @PrimaryKey(autoGenerate = true)
    protected int id;
    private String namePlayer;
    private String date;
    private Integer numberTokenLeft;
    private Integer storePlayer;
    private Integer storeCPU;
    private Integer storeWinner;

    public Game(String namePlayer, String date, Integer numberTokenLeft, Integer storeCPU, Integer storePlayer, Integer storeWinner) {
        this.namePlayer = namePlayer;
        this.date = date;
        this.numberTokenLeft = numberTokenLeft;
        this.storeCPU = storeCPU;
        this.storePlayer = storePlayer;
        this.storeWinner = storeWinner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamePlayer() {
        return namePlayer;
    }

    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNumberTokenLeft() {
        return numberTokenLeft;
    }

    public void setNumberTokenLeft(Integer numberTokenLeft) {
        this.numberTokenLeft = numberTokenLeft;
    }

    public Integer getStorePlayer() {
        return storePlayer;
    }

    public void setStorePlayer(Integer storePlayer) {
        this.storePlayer = storePlayer;
    }

    public Integer getStoreCPU() {
        return storeCPU;
    }

    public void setStoreCPU(Integer storeCPU) {
        this.storeCPU = storeCPU;
    }

    public Integer getStoreWinner() {
        return storeWinner;
    }

    public void setStoreWinner(Integer storeWinner) {
        this.storeWinner = storeWinner;
    }


}
