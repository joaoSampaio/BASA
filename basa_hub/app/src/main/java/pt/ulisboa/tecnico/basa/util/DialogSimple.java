package pt.ulisboa.tecnico.basa.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import java.util.List;


public class DialogSimple {

    Context ctx;
    String title;
    private String description;

    public DialogSimple(){

    }

    public DialogSimple(Context ctx, String title, String description) {
        this.ctx = ctx;
        this.title = title;
        this.description = description;
    }

    public void show(){

        AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(description);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

}
