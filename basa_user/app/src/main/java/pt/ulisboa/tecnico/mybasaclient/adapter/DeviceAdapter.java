package pt.ulisboa.tecnico.mybasaclient.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyItemHolder>{

    public final static int TYPE_TEMPERATURE = 0;
    public final static int TYPE_LIGHT = 1;
    public final static int TYPE_CAMERA  = 2;
    public static final int PRIMARY = Color.parseColor("#2196f3");
    public static final int COLOR_LIGHT = Color.parseColor("#FF737373");
    public static final int COLOR_LIGHT_ON = Color.parseColor("#FFEB3B");
    MainActivity context;
    List<BasaDevice> data = new ArrayList<>();

    public DeviceAdapter(MainActivity context, List<BasaDevice> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyItemHolder viewHolder;

//        GridView grid = (GridView)parent;
//        int size = grid.getColumnWidth();

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_device, parent, false);
//        v.setLayoutParams(new GridView.LayoutParams(size, size));
        viewHolder = new MyItemHolder(v);
        return viewHolder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final MyItemHolder holder, final int position) {


        holder.iconImg.setVisibility(View.GONE);
        holder.addDevice.setVisibility(View.GONE);
        holder.icon.setVisibility(View.GONE);
        if(position == (data.size()*3)){
            //add device
            holder.addDevice.setVisibility(View.VISIBLE);
            holder.name.setText("Add device");
            holder.addDevice.setPadding(20,20,20,20);
            changeBackgroundColor(holder.addDevice, PRIMARY);
            Glide.with(context).load(R.drawable.ic_add_white_48dp).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.addDevice) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.addDevice.setImageDrawable(circularBitmapDrawable);
                }
            });
//            Glide.with(context).load(R.drawable.ic_add).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.iconImg) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    RoundedBitmapDrawable circularBitmapDrawable =
//                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    holder.iconImg.setImageDrawable(circularBitmapDrawable);
//                }
//            });


//            Bitmap theBitmap = BitmapTransform.getBitmapFromVectorDrawable(context, R.drawable.ic_add);
//
//            Bitmap round = BitmapTransform.round(theBitmap);
//            Drawable circularBitmapDrawable = new BitmapDrawable(context.getResources(), round);
//
//            holder.iconImg.setImageDrawable(circularBitmapDrawable);
            Log.d("color", " antes DIALOG_ADD_DEVICE");
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("color", "DIALOG_ADD_DEVICE");
                    context.openPage(Global.DIALOG_ADD_DEVICE);
                }
            });
        }else {

            int type = getType(position);
            holder.icon.setVisibility(View.GONE);
            holder.iconImg.setVisibility(View.GONE);

            int realPosition = position / 3;
            BasaDevice device = data.get(realPosition);

            switch (type){

                case TYPE_TEMPERATURE:
                    holder.icon.setVisibility(View.VISIBLE);

                    holder.icon.setText(((int)device.getLatestTemperature())+"");

                    changeBackgroundColor(holder.icon, (device.getLatestTemperature() >= 18)? Global.COLOR_HEAT : Global.COLOR_COLD);

                    holder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppController.getInstance().saveCurrentDevice(data.get(position / 3));
                            context.openPage(Global.DIALOG_DEVICE_TEMPERATURE);
                        }
                    });
                    break;
                case TYPE_LIGHT:


                    holder.addDevice.setVisibility(View.VISIBLE);
                    holder.addDevice.setPadding(5,5,5,5);


                    if(device.isAnyLightOn()){
                        changeBackgroundColor(holder.addDevice, COLOR_LIGHT_ON);
                        holder.addDevice.setColorFilter(Color.rgb(0, 0, 0));
                    }else{
                        changeBackgroundColor(holder.addDevice, COLOR_LIGHT);
                        holder.addDevice.setColorFilter(Color.argb(255, 255, 255, 255));
                    }



                    Glide.with(context).load(R.drawable.ic_device_light_large).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.addDevice) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.addDevice.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                    holder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppController.getInstance().saveCurrentDevice(data.get(position / 3));
                            context.openPage(Global.DIALOG_DEVICE_LIGHT);
                        }
                    });
                    break;
                case TYPE_CAMERA:
                    holder.iconImg.setVisibility(View.VISIBLE);


                    Glide.with(context).load(R.drawable.ic_device_camera).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.iconImg) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.iconImg.setImageDrawable(circularBitmapDrawable);
                        }
                    });


                    holder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppController.getInstance().saveCurrentDevice(data.get(position / 3));
                            context.openPage(Global.DIALOG_DEVICE_CAMERA);
                        }
                    });
                    break;

            }


            Log.d("color", "normal");

            holder.name.setText(device.getName());



        }
    }

    private int getType(int position){

        return position % 3;

    }

    private void changeBackgroundColor(View v, int color){
            Drawable background = v.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable)background).getPaint().setColor(color);
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable)background).setColor(color);
            } else if (background instanceof ColorDrawable) {
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
        return (data.size()*3)+1;
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        Button icon;
        TextView name;
        ImageView iconImg;
        ImageButton addDevice;
        View container;

        public MyItemHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.device_name);
            icon = (Button) itemView.findViewById(R.id.device_icon);
            container = itemView.findViewById(R.id.container2);
            iconImg = (ImageView) itemView.findViewById(R.id.device_icon_img);
            addDevice = (ImageButton) itemView.findViewById(R.id.device_add);
        }

    }







}
