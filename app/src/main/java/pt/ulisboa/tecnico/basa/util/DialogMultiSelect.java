package pt.ulisboa.tecnico.basa.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

/**
 * Created by joaosampaio on 28-03-2016.
 */
public class DialogMultiSelect {

    Context ctx;
    String[] valuesList;
    boolean[] checkedValues;
    String title;
    DialogMultiSelectResponse listener;

    public DialogMultiSelect(){

    }

    public DialogMultiSelect(Context ctx, String[] valuesList, boolean[] checkedValues, String title, DialogMultiSelectResponse listener) {
        this.ctx = ctx;
        this.valuesList = valuesList;
        this.checkedValues = checkedValues;
        this.title = title;
        this.listener = listener;
    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);





        // Set multiple choice items for alert dialog

        builder.setMultiChoiceItems(valuesList, checkedValues, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos, boolean isChecked) {

                // Update the current focused item's checked status
                checkedValues[pos] = isChecked;

                // Get the current focused item
                String currentItem = valuesList[pos];

            }
        });

        // Specify the dialog is not cancelable
        builder.setCancelable(false);

        // Set a title for alert dialog
        builder.setTitle(title);

        // Set the positive/yes button click listener
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click positive button

            }
        });

        // Set the neutral/cancel button click listener
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the neutral button
            }
        });

        final AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = false;
                for (int i = 0; i < checkedValues.length; i++) {
                    if (checkedValues[i]) {
                        isChecked = true;
                        break;
                    }
                }
                if (isChecked) {
                    listener.onSucess(checkedValues);
                    dialog.dismiss();
                }
                else
                    Toast.makeText(ctx, "Select at least one value.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface DialogMultiSelectResponse{
        void onSucess(boolean[] checkedValues);
    }


}
