package pt.ulisboa.tecnico.mybasaclient.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.IPNetwork;

public class IPNetworkAdapter extends RecyclerView.Adapter<IPNetworkAdapter.MyItemHolder>{


    MainActivity activity;
    private List<IPNetwork> devices;

    public IPNetworkAdapter(MainActivity context, List<IPNetwork> devices) {
        this.activity = context;
        this.devices = devices;
    }

    @Override
    public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyItemHolder viewHolder;


        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_ip_network, parent, false);
        viewHolder = new MyItemHolder(v);
        return viewHolder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final MyItemHolder holder, final int position) {

        IPNetwork device = devices.get(position);
        holder.mac.setText(device.getMac());
        holder.ssid.setText(device.getSsid());

    }


    public String getBssidList(){
        String macs = "";

        for(IPNetwork ipNetwork : devices){
            macs += ipNetwork.getMac()+",";
        }
        if(macs.length()>0){
            macs = macs.substring(0, macs.length()-1);
        }


        return macs;

    }



    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        TextView mac;
        TextView ssid;

        public MyItemHolder(View itemView) {
            super(itemView);
            mac = (TextView) itemView.findViewById(R.id.textMac);
            ssid = (TextView) itemView.findViewById(R.id.textSsid);

        }

    }


    public interface OnLightChange{
        void onChange(List<Boolean> bulbList);
    }





}
