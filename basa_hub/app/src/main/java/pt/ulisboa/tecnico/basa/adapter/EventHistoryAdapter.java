package pt.ulisboa.tecnico.basa.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.model.EventHistory;

public class EventHistoryAdapter extends RecyclerView.Adapter<EventHistoryAdapter.HistoryItemHolder>{

    private Context context;
    private List<EventHistory> data = new ArrayList<>();

    public EventHistoryAdapter(Context context, List<EventHistory> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public HistoryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HistoryItemHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_event_history, parent, false);
        viewHolder = new HistoryItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryItemHolder holder, int position) {

        EventHistory history = data.get(position);
        holder.itemTitle.setText(history.getEvent());
        holder.textViewTime.setText(history.getTime());
        holder.textViewDate.setText(history.getDate());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class HistoryItemHolder extends RecyclerView.ViewHolder{

        TextView itemTitle, textViewTime, textViewDate;

        public HistoryItemHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView)itemView.findViewById(R.id.textViewEvent);
            textViewTime = (TextView)itemView.findViewById(R.id.textViewTime);
            textViewDate = (TextView)itemView.findViewById(R.id.textViewDate);
        }


    }
}
