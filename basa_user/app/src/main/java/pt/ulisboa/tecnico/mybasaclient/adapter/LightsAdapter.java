package pt.ulisboa.tecnico.mybasaclient.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.LightBulb;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.ChangeTemperatureLights;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.ChangeTemperatureLightsService;

public class LightsAdapter extends RecyclerView.Adapter<LightsAdapter.MyItemHolder>{


    MainActivity context;
    List<LightBulb> data = new ArrayList<>();
    private BasaDevice device;
    public static final int COLOR_LIGHT_ON = Color.parseColor("#FFFFD321");
    public static final int COLOR_LIGHT_OFF = Color.parseColor("#cccccc");
    private OnLightChange onLightChange;

    public LightsAdapter(MainActivity context, List<LightBulb> data, BasaDevice device) {
        this.context = context;
        this.data = data;
        this.device = device;
    }

    public void setOnLightChange(OnLightChange onLightChange) {
        this.onLightChange = onLightChange;
    }

    @Override
    public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyItemHolder viewHolder;

//        GridView grid = (GridView)parent;
//        int size = grid.getColumnWidth();

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_light_bulb, parent, false);
//        v.setLayoutParams(new GridView.LayoutParams(size, size));
        viewHolder = new MyItemHolder(v);
        return viewHolder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final MyItemHolder holder, final int position) {

        LightBulb bulb = data.get(position);
        holder.name.setText("light " + (position+1));
        changeBackgroundColor(holder.icon, (bulb.isOn())? COLOR_LIGHT_ON : COLOR_LIGHT_OFF);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                data.get(position).setState(!data.get(position).isOn());

                if(onLightChange != null)
                    onLightChange.onChange(data);



                ChangeTemperatureLights changeTemperatureLights = new ChangeTemperatureLights(LightBulb.getArray(data), -80);
                new ChangeTemperatureLightsService(device.getUrl(), changeTemperatureLights, new CallbackFromService() {
                    @Override
                    public void success(Object response) {

                    }

                    @Override
                    public void failed(Object error) {

                    }
                }).execute();
                notifyDataSetChanged();

            }
        });

    }



    private void changeBackgroundColor(View v, int color){
        Log.d("color", "changeBackgroundColor:");
            Drawable background = v.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable)background).getPaint().setColor(color);
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable)background).setColor(color);
            } else if (background instanceof ColorDrawable) {
                Log.d("color", "ColorDrawable:");
                ((ColorDrawable)background).setColor(color);
            } else if (background instanceof RippleDrawable) {
                Log.d("color", "RippleDrawable:"+ ((RippleDrawable)background).getNumberOfLayers());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((RippleDrawable)background).getDrawable(0).setTint(color);
                }


            }
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;

        View container;

        public MyItemHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            container = itemView.findViewById(R.id.light);

        }

    }


    public interface OnLightChange{
        void onChange(List<LightBulb> bulbList);
    }





}
