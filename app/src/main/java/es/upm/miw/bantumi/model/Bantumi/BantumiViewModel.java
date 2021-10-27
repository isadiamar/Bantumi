package es.upm.miw.bantumi.model.Bantumi;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import es.upm.miw.bantumi.JuegoBantumi;

public class BantumiViewModel extends ViewModel {

    private ArrayList<MutableLiveData<Integer>> board;

    private MutableLiveData<JuegoBantumi.Turn> turn;

    public BantumiViewModel() {
        turn = new MutableLiveData<>(JuegoBantumi.Turn.turnJ1);
        board = new ArrayList<>(JuegoBantumi.NUM_POS);
        for (int i = 0; i < JuegoBantumi.NUM_POS; i++) {
            board.add(i, new MutableLiveData<>(0));
        }
    }

    public LiveData<JuegoBantumi.Turn> getTurn() {
        return turn;
    }

    public void setTurn(JuegoBantumi.Turn turn) {
        this.turn.setValue(turn);
    }

    @NonNull
    public LiveData<Integer> getNumSeeds(int pos) {
        if (pos < 0 || pos >= JuegoBantumi.NUM_POS) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return board.get(pos);
    }

    public void setNumSeeds(int pos, int v) {
        if (pos < 0 || pos >= JuegoBantumi.NUM_POS) {
            throw new ArrayIndexOutOfBoundsException();
        }
        board.get(pos).setValue(v);
    }


    @NonNull
    public String boardToString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < board.size(); i++) {
            res.append(board.get(i).getValue()).append(",");
        }
        return res.toString();
    }
}