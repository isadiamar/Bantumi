package es.upm.miw.bantumi;

import android.util.Log;

import es.upm.miw.bantumi.model.Bantumi.BantumiViewModel;


public class JuegoBantumi {

    public static final int NUM_POS = 14;

    private final BantumiViewModel bantumiVM;
    private final int initialNumberSeeds;

    public JuegoBantumi(BantumiViewModel bantumiVM, Turn turn, int initialNumberSeeds) {
        this.bantumiVM = bantumiVM;
        this.initialNumberSeeds = initialNumberSeeds;
        if (emptyBox(Turn.turnJ1) && emptyBox(Turn.turnJ2)) { // Inicializa sólo si está vacío!!!
            initialize(turn);
        }
    }

    public int getSeeds(int pos) {
        return bantumiVM.getNumSeeds(pos).getValue();
    }

    public void setSeeds(int pos, int value) {
        bantumiVM.setNumSeeds(pos, value);
    }

    public void initialize(Turn turn) {
        setTurn(turn);
        for (int i = 0; i < NUM_POS; i++)
            setSeeds(
                    i,
                    (i == 6 || i == 13) // Almacén??
                            ? 0
                            : initialNumberSeeds
            );
    }

    public void play(int pos) {
        if (pos < 0 || pos >= NUM_POS)
            throw new IndexOutOfBoundsException(String.format("Posición (%d) fuera de límites", pos));
        if (getSeeds(pos) == 0
                || (pos < 6 && currentTurn() != Turn.turnJ1)
                || (pos > 6 && currentTurn() != Turn.turnJ2)
        )
            return;
        Log.i("MiW", String.format("play(%02d)", pos));

        // Collect seeds in pos position
        int numSeedsInBox, numSeeds = getSeeds(pos);
        setSeeds(pos, 0);

        // Do the seeding
        int nextPos = pos;
        while (numSeeds > 0) {
            nextPos = (nextPos + 1) % NUM_POS;
            if (currentTurn() == Turn.turnJ1 && nextPos == 13) // J1 salta depósito jugador 2
                nextPos = 0;
            if (currentTurn() == Turn.turnJ2 && nextPos == 6) // J2 salta depósito jugador 1
                nextPos = 7;
            numSeedsInBox = getSeeds(nextPos);
            setSeeds(nextPos, numSeedsInBox + 1);
            numSeeds--;
        }

        // Si acaba en hueco vacío en propio campo -> recoger propio + contrario
        if (getSeeds(nextPos) == 1
                && ((currentTurn() == Turn.turnJ1 && nextPos < 6)
                || (currentTurn() == Turn.turnJ2 && nextPos > 6 && nextPos < 13))
        ) {
            int posContrario = 12 - nextPos;
            Log.i("MiW", "\trecoger: turno=" + currentTurn() + ", pos=" + nextPos + ", contrario=" + posContrario);
            int miAlmacen = (currentTurn() == Turn.turnJ1) ? 6 : 13;
            setSeeds(
                    miAlmacen,
                    1 + getSeeds(miAlmacen) + getSeeds(posContrario)
            );
            setSeeds(nextPos, 0);
            setSeeds(posContrario, 0);
        }

        // if end -> recolect
        if (emptyBox(Turn.turnJ1) || emptyBox(Turn.turnJ2)) {
            recolect(0);
            recolect(7);
            setTurn(Turn.Turn_Ended);
        }

        // // Determine next turn (if it is your own warehouse -> repeat turn)
        if (currentTurn() == Turn.turnJ1 && nextPos != 6)
            setTurn(Turn.turnJ2);
        else if (currentTurn() == Turn.turnJ2 && nextPos != 13)
            setTurn(Turn.turnJ1);
        Log.i("MiW", "\t turno = " + currentTurn());
    }

    public boolean gameIsEnded() {
        return (currentTurn() == Turn.Turn_Ended);
    }

    public int numTokensLeft() {
        int res = 0;
        int pos;
        for (int i = 0; i < JuegoBantumi.NUM_POS; i++) {
            pos = i;
            if (pos == 6 || pos == 13) {
                res = res + 0;
            } else {
                res = res + this.getSeeds(i);
            }
        }
        return res;
    }

    private boolean emptyBox(Turn turn) {
        boolean empty = true;
        int initBox = (turn == Turn.turnJ1) ? 0 : 7;
        for (int i = initBox; i < initBox + 6; i++)
            empty = empty && (getSeeds(i) == 0);

        return empty;
    }

    private void recolect(int pos) {
        int seedsStore = getSeeds(pos + 6);
        for (int i = pos; i < pos + 6; i++) {
            seedsStore += getSeeds(i);
            setSeeds(i, 0);
        }
        setSeeds(pos + 6, seedsStore);
        Log.i("MiW", "\tRecolect - " + pos);
    }

    public Turn currentTurn() {
        return bantumiVM.getTurn().getValue();
    }

    public void setTurn(Turn turn) {
        bantumiVM.setTurn(turn);
    }

    public String serializeGame() {
        String board = bantumiVM.boardToString();
        int turn = currentTurn().ordinal();

        return turn + ";" + board;
    }

    public void deserializeGame(String serializedGame) {
        int turnOrdinal = Integer.parseInt(serializedGame.substring(0, 1));
        String board = serializedGame.substring(2);
        Turn turn = deserializeTurn(turnOrdinal);
        deserializeBoard(board);
        bantumiVM.setTurn(turn);
    }

    public Turn deserializeTurn(int turnOrdinal) {
        Turn turn = Turn.Turn_Ended;

        for (Turn t : Turn.values()) {
            if (t.ordinal() == turnOrdinal) {
                turn = t;
            }
        }
        return turn;
    }

    public void deserializeBoard(String board) {
        int value;
        int pos = 0;

        for (int i = 0; i < board.length(); i++) {
            if (board.charAt(i) != ',' && board.charAt(i) != '\n') {
                value = Integer.parseInt(board.substring(i, i + 1));
                bantumiVM.setNumSeeds(pos, value);
                pos++;
            }
        }
    }

    public enum Turn {
        turnJ1, turnJ2, Turn_Ended
    }

}

