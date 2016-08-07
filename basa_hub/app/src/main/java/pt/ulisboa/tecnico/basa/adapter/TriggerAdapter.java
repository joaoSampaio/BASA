package pt.ulisboa.tecnico.basa.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.ViewClicked;

public class TriggerAdapter extends RecyclerView.Adapter<TriggerAdapter.TriggerItemHolder>{

    private Context context;
    private List<TriggerAction> data = new ArrayList<>();
    private ViewClicked mListener;

    public TriggerAdapter(Context context, List<TriggerAction> data, ViewClicked mListener) {
        this.context = context;
        this.data = data;
        this.mListener = mListener;
    }


    @Override
    public TriggerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TriggerItemHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_single_ifttt_trigger, parent, false);
        viewHolder = new TriggerItemHolder(v, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TriggerItemHolder holder, int position) {


        Glide.with(context).load(data.get(position).getResId())
                .thumbnail(0.5f)
                .override(200,200)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImg);
        holder.itemTitle.setText(data.get(position).getTitle());
        holder.id = data.get(position).getTriggerActionId();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class TriggerItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mImg;
        TextView itemTitle;
        ViewClicked listener;
        int id;

        public TriggerItemHolder(View itemView, ViewClicked listener) {
            super(itemView);
            this.listener = listener;
            itemTitle = (TextView)itemView.findViewById(R.id.item_title);
            mImg = (ImageView) itemView.findViewById(R.id.item_img);
            mImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(id);
        }

    }
}
