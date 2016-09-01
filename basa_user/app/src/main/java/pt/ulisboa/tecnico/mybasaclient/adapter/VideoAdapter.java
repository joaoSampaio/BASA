package pt.ulisboa.tecnico.mybasaclient.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.firebase.FirebaseFileLink;
import pt.ulisboa.tecnico.mybasaclient.util.DateHelper;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyItemHolder>{


    DialogFragment context;
    List<FirebaseFileLink> videos;
    public static final int COLOR_LIGHT_ON = Color.parseColor("#FFFFD321");
    public static final int COLOR_LIGHT_OFF = Color.parseColor("#cccccc");
    private VideoSelected videoSelected;

    public VideoAdapter(DialogFragment context, List<FirebaseFileLink> videos) {
        this.context = context;
        this.videos = videos;
    }

    public void setVideoSelected(VideoSelected videoSelected) {
        this.videoSelected = videoSelected;
    }

    @Override
    public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyItemHolder viewHolder;


        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_video_history, parent, false);
        viewHolder = new MyItemHolder(v);
        return viewHolder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final MyItemHolder holder, final int position) {


        FirebaseFileLink file = videos.get(position);
        holder.duration.setText(file.getDuration()+"s");


        long created = file.getCreatedAt()*1000;
        Date date=new Date(created);
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        String timeText = df2.format(date);

        holder.time.setText(timeText);
        holder.date.setText(DateHelper.getTimeAgo(created));
        Glide.with(context)
                .load(file.getThumbnail())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);




        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(videoSelected != null)
                    videoSelected.onVideoSelected(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        TextView time, duration, date;
        ImageView thumbnail;

        View container;

        public MyItemHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.textViewTime);
            duration = (TextView) itemView.findViewById(R.id.textViewDuration);
            date = (TextView) itemView.findViewById(R.id.textViewDate);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            container = itemView.findViewById(R.id.container);

        }

    }


    public interface VideoSelected{
        void onVideoSelected(int position);
    }





}
