package pt.ulisboa.tecnico.basa.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.model.Recipe;
import pt.ulisboa.tecnico.basa.model.SSDP;
import pt.ulisboa.tecnico.basa.util.DialogSimple;
import pt.ulisboa.tecnico.basa.util.ViewClicked;

public class DiscoveryServiceAdapter extends RecyclerView.Adapter<DiscoveryServiceAdapter.RecipeItemHolder>{

    private Context context;
    private List<SSDP> data = new ArrayList<>();
    private SelectSSDP mListener;
    private String selected = "";

    public DiscoveryServiceAdapter(Context context, List<SSDP> data, SelectSSDP mListener) {
        this.context = context;
        this.data = data;
        this.mListener = mListener;
    }


    @Override
    public RecipeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecipeItemHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_ssdp, parent, false);
        viewHolder = new RecipeItemHolder(v, null);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeItemHolder holder, int position) {

        SSDP ssdp = data.get(position);
        holder.container.setTag(ssdp.getLocation());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() != null && v.getTag() instanceof String ){
                    String location = (String)v.getTag();

                    selected = location;
                    mListener.onSSDPSelected(location);
                }


            }
        });

        holder.textServer.setText(ssdp.getServer());
        holder.textLocation.setText(ssdp.getLocation());
        holder.textUsn.setText(ssdp.getUsn());

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class RecipeItemHolder extends RecyclerView.ViewHolder{
        TextView textServer;
        TextView textLocation;
        TextView textUsn;
        RelativeLayout container;
        int id;

        public RecipeItemHolder(View itemView, ViewClicked listener) {
            super(itemView);
            textServer = (TextView)itemView.findViewById(R.id.textServer);
            textLocation = (TextView)itemView.findViewById(R.id.textLocation);
            textUsn = (TextView)itemView.findViewById(R.id.textUsn);
            container = (RelativeLayout)itemView.findViewById(R.id.container);
        }

    }


    public interface SelectSSDP{
        void onSSDPSelected(String location);
    }

}
