package pt.ulisboa.tecnico.mybasaclient.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.camera.CallbackCameraAction;
import pt.ulisboa.tecnico.mybasaclient.camera.CallbackQRcode;
import pt.ulisboa.tecnico.mybasaclient.camera.CameraPreview4;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.User;
import pt.ulisboa.tecnico.mybasaclient.model.UserRegistration;
import pt.ulisboa.tecnico.mybasaclient.model.UserRegistrationAnswer;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.model.registration.BasaDeviceInfo;
import pt.ulisboa.tecnico.mybasaclient.model.registration.SimpleBasaDevice;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.GetRegistrationInfoService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.RegisterUserService;

/**
 * Created by Sampaio on 27/06/2016.
 */
public class ScanQRCodeFragment extends DialogFragment {

    View rootView;
    CameraPreview4 previewView;
    FrameLayout frame;
    Spinner spinner;
    EditText editTextName;
    Toolbar toolbar;
    View camera_bg;
    Button save_device;
    View progressBarRegister;
    private BasaDevice device;
    private SimpleBasaDevice simpleBasaDevice;
    public static ScanQRCodeFragment newInstance() {
        ScanQRCodeFragment fragment = new ScanQRCodeFragment();
        return fragment;
    }


    public ScanQRCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scan_qr_code, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        camera_bg = rootView.findViewById(R.id.camera_bg);
        if (toolbar!=null) {
            toolbar.setTitle("Add Zone");
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    getDialog().dismiss();
                }
            });
        }
        frame = (FrameLayout)rootView.findViewById(R.id.frame);
        progressBarRegister = rootView.findViewById(R.id.progressBarRegister);

        editTextName = (EditText)rootView.findViewById(R.id.editTextName);
        rootView.findViewById(R.id.re_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLayoutStart(true);
                previewView.enableQRCode(true);
            }
        });

        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        Zone current = AppController.getInstance().getCurrentZone();
        int selectedPos= 0;
        for(Zone z : AppController.getInstance().loadZones()){
            if(z.getName().equals(current.getName()))
                selectedPos = list.size();
            list.add(z.getName());
        }

//        list.add("Casa");
//        list.add("Tagus");
//        list.add("Alameda");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(selectedPos);
        showLayoutStart(true);

        save_device = (Button)rootView.findViewById(R.id.save_device);
        save_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        device = new BasaDevice();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBarRegister.setVisibility(View.GONE);
        if(((MainActivity)getActivity()).mayRequestCamera()) {
            startCapture();

        }else{
            ((MainActivity)getActivity()).setCommunicationScanFragment(new CommunicationScanFragment() {
                @Override
                public void enableCamera() {
                    startCapture();
                    ((MainActivity)getActivity()).setCommunicationScanFragment(null);
                }
            });
        }

    }

    private void startCapture(){
        try {
            previewView = new CameraPreview4(((MainActivity) getActivity()), new CallbackCameraAction() {
                @Override
                public void onSuccess() {
                    camera_bg.setVisibility(View.GONE);
                }

                @Override
                public void onFailure() {

                }
            });
            frame.addView(previewView);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    previewView.enableQRCode(true);
                }
            }, 600);
            previewView.setCallbackQRcode(new CallbackQRcode() {
                @Override
                public void qrCodeDetected(String value) {
                    onQrCodeDetected(value);
                }
            });

        } catch (Exception exception) {
            Log.e("erro camera", "Can't open camera with id ", exception);

            return;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        frame.removeAllViews();
    }

    private void onQrCodeDetected(String value){
        Log.d("qrcode", "onQrCodeDetected:" + value);
        showLayoutStart(false);
        previewView.enableQRCode(false);


        Gson gson = new Gson();
        try {
            Log.d("qrcode", "entrou:");
            simpleBasaDevice = gson.fromJson(value, new TypeToken<SimpleBasaDevice>() {
            }.getType());

            if(simpleBasaDevice.getToken() != null && !simpleBasaDevice.getToken().isEmpty()){

                if(!simpleBasaDevice.getUrl().startsWith("http")) {
                    simpleBasaDevice.setUrl("http://" + simpleBasaDevice.getUrl());
                }

                device.setUrl(simpleBasaDevice.getUrl());
                save_device.setEnabled(false);
                //contact server
                new GetRegistrationInfoService(simpleBasaDevice.getUrl(), new CallbackFromService<BasaDeviceInfo, Object>() {
                    @Override
                    public void success(BasaDeviceInfo response) {

                        if(getDialog() != null){
                            save_device.setEnabled(true);
                            Log.d("qrcode", "name:"+response.getName());
                            editTextName.setText(response.getName());
                            device.setDescription(response.getDescription());
                            device.setId(response.getId());
                            device.setName(response.getName());
                        }


                    }

                    @Override
                    public void failed(Object error) {
                        Toast.makeText(getActivity(), "Could not contact device.", Toast.LENGTH_SHORT).show();
                    }
                }).execute();






            }else{
                Toast.makeText(getActivity(), "Invalid QrCode", Toast.LENGTH_SHORT).show();
                showLayoutStart(true);
                rootView.findViewById(R.id.re_scan).setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "Invalid QrCode", Toast.LENGTH_SHORT).show();
            showLayoutStart(true);
            rootView.findViewById(R.id.re_scan).setVisibility(View.VISIBLE);
        }
    }

    private void registerUser(){

        editTextName.setError(null);
        if(editTextName.getText().toString().isEmpty()){
            editTextName.setError("Name cannot be empty");
            return;
        }

        if(simpleBasaDevice != null && simpleBasaDevice.getToken() != null && !simpleBasaDevice.getToken().isEmpty()){
            save_device.setEnabled(false);
            progressBarRegister.setVisibility(View.VISIBLE);

            if(!device.getUrl().startsWith("http")) {
                device.setUrl("http://" + device.getUrl());
            }
            device.setName(editTextName.getText().toString());
            String registrationUrl = device.getUrl();
            User user = AppController.getInstance().getLoggedUser();
            UserRegistration userRegistration = new UserRegistration(user.getEmail(), user.getUserName(), user.getUuid(), simpleBasaDevice.getToken());
            new RegisterUserService(registrationUrl, userRegistration, new CallbackFromService<UserRegistrationAnswer, String>() {
                @Override
                public void success(UserRegistrationAnswer response) {

                    device.setBeaconUuids(response.getUuids());
                    device.setMacAddress(response.getMacAddress());
                    device.setLatestTemperature(response.getTemperature());



                    int selectedPos= spinner.getSelectedItemPosition();
                    Zone zone = AppController.getInstance().loadZones().get(selectedPos);

//                    Zone zone = AppController.getInstance().getCurrentZone();


                    List<Zone> zones = AppController.getInstance().loadZones();
                    zone.addDevice(device);
                    AppController.getInstance().saveZones(zones);
                    if(getActivity() != null && ((MainActivity)getActivity()).getCommunicationHomeFragment() != null)
                        ((MainActivity)getActivity()).getCommunicationHomeFragment().updateZone(true);
                    if(getDialog() != null)
                        getDialog().dismiss();

                }

                @Override
                public void failed(String error) {
                    progressBarRegister.setVisibility(View.GONE);

                }
            }).execute();



        }


    }


    private void showLayoutStart(boolean start){
        rootView.findViewById(R.id.layout_before).setVisibility(start? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.layout_after).setVisibility(!start? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.re_scan).setVisibility(!start? View.VISIBLE : View.GONE);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public interface CommunicationScanFragment{
        void enableCamera();
    }


}
