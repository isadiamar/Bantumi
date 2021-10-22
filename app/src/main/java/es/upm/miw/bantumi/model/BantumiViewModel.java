package es.upm.miw.bantumi.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import es.upm.miw.bantumi.JuegoBantumi;

public class BantumiViewModel extends ViewModel {

    private ArrayList<MutableLiveData<Integer>> tablero;

    private MutableLiveData<JuegoBantumi.Turno> turno;

    public BantumiViewModel() {
        turno = new MutableLiveData<>(JuegoBantumi.Turno.turnoJ1);
        tablero = new ArrayList<>(JuegoBantumi.NUM_POSICIONES);
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            tablero.add(i, new MutableLiveData<>(0));
        }
    }

    public LiveData<JuegoBantumi.Turno> getTurno() {
        return turno;
    }

    public void setTurno(JuegoBantumi.Turno turno) {
        this.turno.setValue(turno);
    }

    @NonNull
    public LiveData<Integer> getNumSemillas(int pos) {
        if (pos < 0 || pos >= JuegoBantumi.NUM_POSICIONES) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return tablero.get(pos);
    }

    public void setNumSemillas(int pos, int v) {
        if (pos < 0 || pos >= JuegoBantumi.NUM_POSICIONES) {
            throw new ArrayIndexOutOfBoundsException();
        }
        tablero.get(pos).setValue(v);
    }
}