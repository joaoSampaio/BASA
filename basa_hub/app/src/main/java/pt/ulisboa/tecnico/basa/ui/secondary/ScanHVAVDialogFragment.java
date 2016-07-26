package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.DiscoveryServiceAdapter;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.manager.DeviceDiscoveryManager;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.model.SSDP;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.Pojo.ServerLocation;
import pt.ulisboa.tecnico.basa.rest.PostServerLocationService;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;
import pt.ulisboa.tecnico.basa.util.ModelCache;


public class ScanHVAVDialogFragment extends DialogFragment {

    private View rootView;
    RecyclerView mRecyclerView;
    DiscoveryServiceAdapter mAdapter;
    List<SSDP> data;
    private Button buttonScan, action_accept;
    private EditText editTextUrl;
    private RelativeLayout scanContainer;
    private ProgressBar mProgressBar;
    private int progress = 0;

    public ScanHVAVDialogFragment() {
        // Required empty public constructor
    }



    public static ScanHVAVDialogFragment newInstance() {
        ScanHVAVDialogFragment fragment = new ScanHVAVDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scan_hvac, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){



        rootView.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        scanContainer = (RelativeLayout)rootView.findViewById(R.id.scanContainer);
        editTextUrl = (EditText)rootView.findViewById(R.id.editTextUrl);

        String locationIp = new ModelCache<String>().loadModel(new TypeToken<String>(){}.getType(), Global.OFFLINE_IP_TEMPERATURE, "");
        editTextUrl.setText(locationIp);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        data = new ArrayList<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new DiscoveryServiceAdapter(getActivity(), data, new DiscoveryServiceAdapter.SelectSSDP() {
            @Override
            public void onSSDPSelected(String location) {
                location = location.trim();

                if(!location.startsWith("http")) {
                    Log.d("ssdp", "!location.startsWith(\"http\") :" + location);
                    location = "http://" + location;

                }


//                location = location.replace(":80/data/", "");
//                if(!location.endsWith("/")){
//                    location = location + "/";
//                }


                editTextUrl.setText(location);
                //new ModelCache<String>().saveModel(location, Global.OFFLINE_IP_TEMPERATURE);
                //((Launch2Activity)getActivity()).getBasaManager().getTemperatureManager().requestUpdateTemperature();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        buttonScan = (Button)rootView.findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    Log.d("ssdp", "onClick2: ");
                    buttonScan.setEnabled(false);
                    progress = 0;
                    mProgressBar.setVisibility(View.VISIBLE);

                    mProgressBar.setMax(5);
                    mProgressBar.setProgress(progress);
                    CountDownTimer mCountDownTimer=new CountDownTimer(5000,1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.v("Log_tag", "Tick of Progress"+ progress+ millisUntilFinished);
                            progress++;
                            mProgressBar.setProgress(progress);

                        }

                        @Override
                        public void onFinish() {
                            //Do what you want
                            progress++;
                            mProgressBar.setProgress(progress);
                        }
                    };
                    mCountDownTimer.start();

                    ((Launch2Activity)getActivity()).getBasaManager().getDeviceDiscoveryManager().startDiscovery(new DeviceDiscoveryManager.DevicesDiscovery() {
                        @Override
                        public void onDevicesDiscovered(List<SSDP> endpoints) {
                            data.clear();
                            data.addAll(endpoints);
                            if(endpoints.size() > 0){
                                scanContainer.setVisibility(View.GONE);
                            }else {
                                Toast.makeText(AppController.getAppContext(), "No devices found", Toast.LENGTH_SHORT).show();
                            }
                            buttonScan.setEnabled(true);
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        action_accept = (Button)rootView.findViewById(R.id.action_accept);
        action_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ssdp", "action_accept");
                String location = editTextUrl.getText().toString();
                new ModelCache<String>().saveModel(location, Global.OFFLINE_IP_TEMPERATURE);

                AppController.getInstance().getDeviceConfig().setArduinoIP(location);
                AppController.getInstance().getDeviceConfig().setTemperatureChoice(BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_CONTROL_ARDUINO);
                AppController.getInstance().setDeviceConfig(AppController.getInstance().getDeviceConfig());


                ((Launch2Activity)getActivity()).getBasaManager().getTemperatureManager().requestUpdateTemperature();

                WifiManager wm = (WifiManager) AppController.getAppContext().getSystemService(Activity.WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                String server =  "http://" + ip + ":5001/broadcast";

                new PostServerLocationService(location.replace("data", "setserver"), new ServerLocation(server), new CallbackMultiple() {
                    @Override
                    public void success(Object response) {

                    }

                    @Override
                    public void failed(Object error) {

                    }
                }).execute();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
