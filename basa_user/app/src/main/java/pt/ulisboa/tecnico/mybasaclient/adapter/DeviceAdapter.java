package pt.ulisboa.tecnico.mybasaclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;

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

    @Override
    public void onBindViewHolder(MyItemHolder holder, final int position) {
        if(position == data.size()){
            //add device
            holder.name.setText("Add device");
            holder.icon.setImageResource(R.drawable.ic_add);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.openPage(Global.DIALOG_ADD_DEVICE);
                }
            });
        }else {

            BasaDevice device = data.get(position);
            holder.name.setText(device.getName());
            holder.icon.setImageResource(R.drawable.ic_thumb_office);
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
        RoundedImageView icon;
        TextView name;
        View container;

        public MyItemHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.device_name);
            icon = (RoundedImageView) itemView.findViewById(R.id.device_icon);
            container = itemView.findViewById(R.id.container);
        }

    }







}
