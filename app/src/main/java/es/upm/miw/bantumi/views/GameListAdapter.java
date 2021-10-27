package es.upm.miw.bantumi.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.model.Room.Game;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    static class GameViewHolder extends RecyclerView.ViewHolder {

        private final TextView namePlayer;
        private final TextView date;
        private final TextView numberTokenLeft;
        private final TextView storePlayer;
        private final TextView storeCPU;

        private GameViewHolder(View itemView) {
            super(itemView);
            namePlayer = itemView.findViewById(R.id.etNamePlayer) ;
            date =  itemView.findViewById(R.id.etDateGame);
            numberTokenLeft = itemView.findViewById(R.id.etTokensLeft);
            storePlayer = itemView.findViewById(R.id.etScorePlayer);
            storeCPU = itemView.findViewById(R.id.etScoreCPU);
        }

    }


    private final LayoutInflater inflater;
    private List<Game> games;

    public GameListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = this.inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {

        if (this.games != null) {
            Game current = this.games.get(position);
            holder.namePlayer.setText(current.getNamePlayer());
            holder.numberTokenLeft.setText(current.getNumberTokenLeft().toString());
            holder.date.setText((CharSequence) current.getDate());
            holder.storePlayer.setText(current.getStorePlayer().toString());
            holder.storeCPU.setText(current.getStoreCPU().toString());

        } else {
            // Covers the case of data not being ready yet.
            Date today = Calendar.getInstance().getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(today);

            holder.namePlayer.setText("Player");
            holder.numberTokenLeft.setText(0);
            holder.date.setText(formattedDate);
            holder.storePlayer.setText(0);
            holder.storeCPU.setText(0);

        }
    }

    public void setGames(List<Game> games){
        this.games = games;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (this.games == null)
                ? 0
                : this.games.size();
    }

    public Game getGamePosition (int position) {
        return this.games.get(position);
    }

}
