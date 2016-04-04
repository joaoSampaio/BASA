package pt.ulisboa.tecnico.basa.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.basa.ui.secondary.TriggerIFTTTDialogFragment;

/**
 * Created by joaosampaio on 28-03-2016.
 */
public class DialogOneChoiceSelect {

    Context ctx;
    List<String> valuesList;
    boolean[] checkedValues;
    String title;
    DialogOneSelectResponse listener;
    private int type;

    public DialogOneChoiceSelect(){

    }

    public DialogOneChoiceSelect(Context ctx, List<String> valuesList, String title, DialogOneSelectResponse listener, int type) {
        this.ctx = ctx;
        this.valuesList = valuesList;
        this.title = title;
        this.listener = listener;
        this.type = type;
    }

    public void show(){


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ctx);
        builderSingle.setTitle("Select Condition");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                ctx,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(valuesList);


        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        listener.onSucess(pos, type);
                    }
                });
        builderSingle.show();
    }


    public interface DialogOneSelectResponse{
        void onSucess(int id, int type);
    }


}
