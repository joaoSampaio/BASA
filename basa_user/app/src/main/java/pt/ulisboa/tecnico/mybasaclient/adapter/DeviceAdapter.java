package pt.ulisboa.tecnico.mybasaclient.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.util.BitmapTransform;
import pt.ulisboa.tecnico.mybasaclient.util.NiceColor;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyItemHolder>{

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


        holder.iconImg.setVisibility((position == data.size())? View.VISIBLE : View.GONE);
        holder.icon.setVisibility((position == data.size())? View.GONE : View.VISIBLE);
        if(position == data.size()){
            //add device

            holder.name.setText("Add device");

//            Glide.with(context).load(R.drawable.ic_add).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.iconImg) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    RoundedBitmapDrawable circularBitmapDrawable =
//                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    holder.iconImg.setImageDrawable(circularBitmapDrawable);
//                }
//            });


            Bitmap theBitmap = BitmapTransform.getBitmapFromVectorDrawable(context, R.drawable.ic_add);

//                Bitmap theBitmap = BitmapFactory.decodeResource(AppController.getAppContext().getResources(), R.drawable.room1);


            Bitmap round = BitmapTransform.round(theBitmap);
                Drawable circularBitmapDrawable = new BitmapDrawable(context.getResources(), round);

                holder.iconImg.setImageDrawable(circularBitmapDrawable);





//            holder.iconImg.setImageResource(R.drawable.ic_add);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.openPage(Global.DIALOG_ADD_DEVICE);
                }
            });
        }else {

            Log.d("color", "normal");
            Drawable background = holder.icon.getBackground();
            int color = NiceColor.randomColor();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable)background).getPaint().setColor(color);
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable)background).setColor(color);
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable)background).setColor(color);
            } else if (background instanceof RippleDrawable) {
                Log.d("color", "RippleDrawable:"+ ((RippleDrawable)background).getNumberOfLayers());
//                ColorStateList colorStateList = new ColorStateList(
//                        new int[][]
//                                {
//                                        new int[]{}
//                                },
//                        new int[]
//                                {
//                                        color
//                                }
//                );
//                ((RippleDrawable)background).setColor(colorStateList);


                ((RippleDrawable)background).getDrawable(0).setTint(color);


            }





            BasaDevice device = data.get(position);
            holder.name.setText(device.getName());
//            holder.icon.setImageResource(R.drawable.ic_thumb_office);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BasaDevice.saveCurrentDevice(data.get(position));
                    context.openPage(Global.DIALOG_DEVICE);
                }
            });


        }




    }



    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        Button icon;
        TextView name;
        ImageView iconImg;
        View container;

        public MyItemHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.device_name);
            icon = (Button) itemView.findViewById(R.id.device_icon);
            container = itemView.findViewById(R.id.container);
            iconImg = (ImageView) itemView.findViewById(R.id.device_icon_img);
        }

    }







}
