package pt.ulisboa.tecnico.mybasaclient.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import pt.ulisboa.tecnico.mybasaclient.camera.CallbackCameraAction;
import pt.ulisboa.tecnico.mybasaclient.camera.CallbackQRcode;
import pt.ulisboa.tecnico.mybasaclient.camera.CameraPreview4;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;

/**
 * Created by Sampaio on 27/06/2016.
 */
public class ScanQRCodeFragment extends DialogFragment {

    View rootView;
    CameraPreview4 previewView;
    FrameLayout frame;
    Spinner spinner;
    EditText editTextName;
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
        frame = (FrameLayout)rootView.findViewById(R.id.frame);

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
        list.add("Casa");
        list.add("Tagus");
        list.add("Alameda");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        showLayoutStart(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {


            previewView = new CameraPreview4(((MainActivity) getActivity()), new CallbackCameraAction() {
                @Override
                public void onSuccess() {

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
        Log.d("qrcode", "onQrCodeDetected:");
        showLayoutStart(false);
        previewView.enableQRCode(false);


        Gson gson = new Gson();
        try {
            Log.d("qrcode", "entrou:");
            BasaDevice device = gson.fromJson(value, new TypeToken<BasaDevice>() {
            }.getType());

            if(device.getToken() != null && !device.getToken().isEmpty()){
                Log.d("qrcode", "name:"+device.getName());
                editTextName.setText(device.getName());
            }else{
                Toast.makeText(getActivity(), "Invalid QrCode", Toast.LENGTH_SHORT).show();
                showLayoutStart(true);
                rootView.findViewById(R.id.re_scan).setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            e.printStackTrace();
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

}
