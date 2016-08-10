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
import pt.ulisboa.tecnico.basa.util.ColorHelper;
import pt.ulisboa.tecnico.basa.util.ViewClicked;

public class HorizontalTriggerAdapter extends RecyclerView.Adapter<HorizontalTriggerAdapter.TriggerItemHolder>{

    private Context context;
    private List<TriggerAction> data = new ArrayList<>();
    private MultiTriggerSelected mListener;

    public HorizontalTriggerAdapter(Context context, List<TriggerAction> data, MultiTriggerSelected mListener) {
        this.context = context;
        this.data = data;
        this.mListener = mListener;
    }


    @Override
    public TriggerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TriggerItemHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_single_ifttt_trigger_height, parent, false);

        viewHolder = new TriggerItemHolder(v, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TriggerItemHolder holder, final int position) {


        Glide.with(context).load(data.get(position).getResId())
                .thumbnail(0.5f)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImg);

        ColorHelper.changeBackgroundColor(holder.colorLayout, data.get(position).getColor());

        holder.id = data.get(position).getTriggerActionId();

        holder.colorLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mListener.onMultiSelected(view, position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class TriggerItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        View colorLayout;
        MultiTriggerSelected listener;
        int id;

        public TriggerItemHolder(View itemView, MultiTriggerSelected listener) {
            super(itemView);
            this.listener = listener;
            mImg = (ImageView) itemView.findViewById(R.id.item_img);
            colorLayout = itemView.findViewById(R.id.colorLayout);

        }

    }

    public interface MultiTriggerSelected{
        void onMultiSelected(View view, int position);
    }
}
