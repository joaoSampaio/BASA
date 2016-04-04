package pt.ulisboa.tecnico.basa.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.model.Recipe;
import pt.ulisboa.tecnico.basa.model.RecipeEvent;
import pt.ulisboa.tecnico.basa.model.Trigger;
import pt.ulisboa.tecnico.basa.model.TriggerAction;
import pt.ulisboa.tecnico.basa.util.ViewClicked;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeItemHolder>{

    private Context context;
    private List<Recipe> data = new ArrayList<>();
    private ViewClicked mListener;

    public RecipeAdapter(Context context, List<Recipe> data) {
        this.context = context;
        this.data = data;
        this.mListener = null;
    }


    @Override
    public RecipeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecipeItemHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_single_ifttt, parent, false);
        viewHolder = new RecipeItemHolder(v, null);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeItemHolder holder, int position) {


        int triggerId = data.get(position).getTriggerId();
        int actionId = data.get(position).getActionId();
        Glide.with(context).load(Trigger.getResId(triggerId))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.action_trigger);
        Glide.with(context).load(TriggerAction.getResId(actionId))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.action_event);

        holder.action_condition.setVisibility(View.GONE);
        holder.action_condition_value.setVisibility(View.GONE);
        holder.action_event_condition.setVisibility(View.GONE);
        holder.action_event_condition_value.setVisibility(View.GONE);

        if(Trigger.isTriggerComplex(triggerId)){
            //temperature
            Log.d("onBindViewHolder", "Trigger.isTriggerComplex:"+position);
            holder.action_condition.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
            holder.action_condition.setVisibility(View.VISIBLE);
            holder.action_condition.setText(data.get(position).getConditionTrigger());
            holder.action_condition_value.setVisibility(View.VISIBLE);
            holder.action_condition_value.setText(data.get(position).getConditionTriggerValue());

        }else if(Trigger.isTriggerSimple(triggerId)){
            //voice
            Log.d("onBindViewHolder", "Trigger.isTriggerSimple" + position);
            holder.action_condition.setVisibility(View.VISIBLE);

            if(triggerId == Trigger.VOICE) {
                holder.action_condition.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                holder.action_condition.setText("is" + System.getProperty("line.separator") + "\"" + data.get(position).getConditionTriggerValue());
            }else {
                holder.action_condition.setText("is");
                holder.action_condition.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
                holder.action_condition_value.setVisibility(View.VISIBLE);
                holder.action_condition_value.setText(data.get(position).getConditionTriggerValue());
            }

        }else {
            //all other
            Log.d("onBindViewHolder", "Trigger.is not " + position);

        }

        if(TriggerAction.isTriggerComplex(actionId)){
            //temperature
            holder.action_event_condition.setVisibility(View.VISIBLE);
            holder.action_event_condition.setText(data.get(position).getConditionEvent());
            holder.action_event_condition_value.setVisibility(View.VISIBLE);
            holder.action_event_condition_value.setText(data.get(position).getConditionEventValue());

        }else if(TriggerAction.isTriggerSimple(actionId)){
            //voice
            holder.action_event_condition.setVisibility(View.VISIBLE);
            if(actionId == TriggerAction.VOICE) {
                holder.action_event_condition.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                holder.action_event_condition.setText("say" + System.getProperty("line.separator") + "\"" + data.get(position).getConditionEventValue());
            }else {
                holder.action_event_condition.setText("is");
                holder.action_event_condition.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
                holder.action_event_condition_value.setVisibility(View.VISIBLE);
                holder.action_event_condition_value.setText(data.get(position).getConditionEventValue());
            }
        }else {
            //all other
            Log.d("app", "chegou");
            if(actionId == TriggerAction.LIGHT_ON && data.get(position).getSelectedMulti() != null){
                Log.d("app", "chegou1");
                holder.action_event_condition.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                holder.action_event_condition.setVisibility(View.VISIBLE);
                String str = "[";
                for (Integer i: data.get(position).getSelectedMulti())
                    str+=(i+1)+",";
                str = str.substring(0, str.length()-1);
                str+="]";
                holder.action_event_condition.setText(str);

            }
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class RecipeItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView action_trigger, action_event;
        TextView action_condition,action_condition_value, action_event_condition, action_event_condition_value;
        ViewClicked listener;
        Switch switch1;
        int id;

        public RecipeItemHolder(View itemView, ViewClicked listener) {
            super(itemView);
            this.listener = listener;
            action_condition = (TextView)itemView.findViewById(R.id.action_condition);
            action_condition_value = (TextView)itemView.findViewById(R.id.action_condition_value);
            action_event_condition = (TextView)itemView.findViewById(R.id.action_event_condition);
            action_event_condition_value = (TextView)itemView.findViewById(R.id.action_event_condition_value);
            action_trigger = (ImageView) itemView.findViewById(R.id.action_trigger);
            action_event = (ImageView) itemView.findViewById(R.id.action_event);
            switch1 = (Switch) itemView.findViewById(R.id.switch1);
            //switch1.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(id);
        }

    }
}
