package pt.ulisboa.tecnico.basa.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class DialogEditText {

    Context ctx;
    String title;
    private String hint;
    private TextSelected textSelected;

    public DialogEditText(){

    }

    public DialogEditText(Context ctx, String title, String hint, TextSelected textSelected) {
        this.ctx = ctx;
        this.title = title;
        this.hint = hint;
        this.textSelected = textSelected;
    }

    public void show(){

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ctx);
        alertDialogBuilder.setCancelable(false);
        final EditText editText = new EditText(ctx);

        alertDialogBuilder.setView(editText);
        alertDialogBuilder.setTitle(title);
        editText.setHint(hint);
        editText.setSelection(editText.getText().length());

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Select", null)
                .setNegativeButton("Cancel", null);
        final android.support.v7.app.AlertDialog alert = alertDialogBuilder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {

                Button okButton = alert.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        String name = editText.getText().toString().trim();

                        if(name.length() >= 0){

                            textSelected.onTextSelected(name);
                            alert.dismiss();
                        }else{
                            Snackbar snack = Snackbar.make(editText, "Name too short", Snackbar.LENGTH_SHORT);
                            View view = snack.getView();
                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.RED);
                            snack.show();
                        }
                    }
                });
            }
        });
        alert.show();

    }

    public interface TextSelected{
        void onTextSelected(String text);
    }

}
