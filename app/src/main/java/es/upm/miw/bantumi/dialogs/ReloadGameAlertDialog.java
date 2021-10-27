package es.upm.miw.bantumi.dialogs;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import es.upm.miw.bantumi.JuegoBantumi;
import es.upm.miw.bantumi.MainActivity;
import es.upm.miw.bantumi.R;

public class ReloadGameAlertDialog  extends AppCompatDialogFragment {
    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) getActivity();

        assert main != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder
                .setTitle(R.string.txtDialogoReloadGamesTitle)
                .setMessage(R.string.txtDialogoReloadGamesPregunta)
                .setPositiveButton(
                        getString(R.string.txtDialogoFinalAfirmativo),
                        (dialog, which) -> main.load())
                .setNegativeButton(
                        getString(R.string.txtDialogoFinalNegativo),
                        (dialog, which) -> main.finish()
                );

        return builder.create();
    }

}
