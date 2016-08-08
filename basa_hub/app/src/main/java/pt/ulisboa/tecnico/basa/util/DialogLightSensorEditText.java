package pt.ulisboa.tecnico.basa.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.ulisboa.tecnico.basa.R;


public class DialogLightSensorEditText {

    Context ctx;
    String title;
    private String hint;
    private TextSelected textSelected;
    private int inputType;

    public DialogLightSensorEditText(){

    }

    public DialogLightSensorEditText(Context ctx, String title, String hint, TextSelected textSelected) {
        this.ctx = ctx;
        this.title = title;
        this.hint = hint;
        this.textSelected = textSelected;
        inputType = InputType.TYPE_CLASS_NUMBER;
    }

    public DialogLightSensorEditText(Context ctx, String title, String hint, TextSelected textSelected, int inputType) {
        this.ctx = ctx;
        this.title = title;
        this.hint = hint;
        this.textSelected = textSelected;
        this.inputType = inputType;
    }

    public TextView show(){

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ctx);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(ctx);
        View dialogView = inflater.inflate(R.layout.dialog_edittext, null);

        alertDialogBuilder.setView(dialogView);
        final EditText editText = (EditText)dialogView.findViewById(R.id.edit1);
        final TextView textViewLux = (TextView) dialogView.findViewById(R.id.textViewLux);

        alertDialogBuilder.setTitle(title);
        editText.setHint(hint);
        editText.setSelection(editText.getText().length());

        editText.setInputType(inputType);

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
        return textViewLux;
    }

    public interface TextSelected{
        void onTextSelected(String text);
    }

}
