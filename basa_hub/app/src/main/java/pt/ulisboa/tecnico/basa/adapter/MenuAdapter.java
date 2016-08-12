package pt.ulisboa.tecnico.basa.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import pt.ulisboa.tecnico.basa.model.GeneralMenuItem;
import pt.ulisboa.tecnico.basa.util.ViewClicked;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ItemHolder>{

    private Context context;
    private List<GeneralMenuItem> data = new ArrayList<>();
    private ViewClicked mListener;

    public MenuAdapter(Context context, List<GeneralMenuItem> data, ViewClicked mListener) {
        this.context = context;
        this.data = data;
        this.mListener = mListener;
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_menu_item, parent, false);
        viewHolder = new ItemHolder(v, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {


        Glide.with(context).load(data.get(position).getResId())
                .thumbnail(0.5f)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImg);

        holder.itemTitle.setText(data.get(position).getTitle());

        Log.d("menu", "id: " + data.get(position).getId() + " title:"+data.get(position).getTitle());

        holder.id = data.get(position).getId();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mImg;
        View colorLayout;
        TextView itemTitle;
        ViewClicked listener;
        int id;

        public ItemHolder(View itemView, ViewClicked listener) {
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
