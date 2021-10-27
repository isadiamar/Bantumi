package es.upm.miw.bantumi.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import es.upm.miw.bantumi.activities.BestGameResultsActivity;
import es.upm.miw.bantumi.R;

public class DeleteAllAlertDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final BestGameResultsActivity bestResults = (BestGameResultsActivity) getActivity();

        assert bestResults != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(bestResults);
        builder
                .setTitle(R.string.txtDialogoDeleteGamesTitle)
                .setMessage(R.string.txtDialogoDeleteGamesPregunta)
                .setPositiveButton(
                        getString(R.string.txtDialogoFinalAfirmativo),
                        (dialog, which) -> bestResults.deleteAll()

                )
                .setNegativeButton(
                        getString(R.string.txtDialogoFinalNegativo),
                        (dialog, which) -> bestResults.finish()
                );

        return builder.create();
    }
}
