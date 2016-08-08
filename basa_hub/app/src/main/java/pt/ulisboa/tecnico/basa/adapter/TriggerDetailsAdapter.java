package pt.ulisboa.tecnico.basa.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

public class TriggerDetailsAdapter extends RecyclerView.Adapter<TriggerDetailsAdapter.RecipeItemHolder>{

    private Context context;
    private View.OnClickListener mListener;
    private final ArrayList mData;
    private TriggerAction trigger;
    private TriggerActionParameterSelected triggerActionParameterSelected;

    public TriggerDetailsAdapter(Context context, LinkedHashMap<String, Object> data, TriggerAction trigger, TriggerActionParameterSelected triggerActionParameterSelected) {
        this.context = context;
        this.trigger = trigger;
        this.triggerActionParameterSelected = triggerActionParameterSelected;
        this.mListener = trigger.getListener(context, triggerActionParameterSelected);
        mData = new ArrayList();

        for(Map.Entry<String, Object> entry : data.entrySet()){
            Log.d("trigger", "map:" + entry.getKey());
        }

        mData.addAll(data.entrySet());
        for( Object entry : mData){
            Log.d("trigger", "list:" + ((Map.Entry)entry).getKey());
        }
    }


    @Override
    public RecipeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecipeItemHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_trigger_choice, parent, false);
        viewHolder = new RecipeItemHolder(v);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(RecipeItemHolder holder, int position) {


//        int triggerId = data.get(position).getTriggerId();
//        int actionId = data.get(position).getActionId();
        Map.Entry<String, Object> data = (Map.Entry)mData.get(position);


        holder.buttonChoice.setText(data.getKey());
        holder.buttonChoice.setTag(data.getValue());
        holder.buttonChoice.setOnClickListener(mListener);
        holder.buttonChoice.setBackgroundColor(trigger.getColor());
//        holder.buttonChoice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(v.getTag() != null && v.getTag() instanceof String ){
//                    String description = (String)v.getTag();
//                    new DialogSimple(context, "Info", description).show();
//                }
//
//
//            }
//        });


    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class RecipeItemHolder extends RecyclerView.ViewHolder{

        Button buttonChoice;
        int id;

        public RecipeItemHolder(View itemView) {
            super(itemView);

            buttonChoice = (Button) itemView.findViewById(R.id.buttonChoice);
            //switch1.setOnClickListener(this);
        }
    }

}
