package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.exceptions.UserRegistrationException;
import pt.ulisboa.tecnico.basa.model.registration.RegisterAndroidQRCode;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistrationToken;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.SendEmailService;
import pt.ulisboa.tecnico.basa.rest.mail.WelcomeTemplate;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;
import pt.ulisboa.tecnico.basa.util.QRCodeGenerator;

public class RegisterUserDialogFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    private View rootView;
    private Button save;
    private EditText editUsername, editEmail;
    private ImageView imageViewQRCode, qrcode_android;


    private static final int[] CLICKABLE = {R.id.btn_android, R.id.btn_email};

    public RegisterUserDialogFragment() {
        // Required empty public constructor
    }



    public static RegisterUserDialogFragment newInstance() {
        RegisterUserDialogFragment fragment = new RegisterUserDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_registration, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){

        Log.d("register", "register");
        showScreen(R.id.layout_pre);

//        rootView.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getDialog().dismiss();
//            }
//        });

        for (int id : CLICKABLE)
            rootView.findViewById(id).setOnClickListener(this);

        editUsername = (EditText)rootView.findViewById(R.id.edit_username);
        editEmail = (EditText)rootView.findViewById(R.id.edit_email);
        imageViewQRCode = (ImageView)rootView.findViewById(R.id.imageViewQRCode);
        qrcode_android = (ImageView)rootView.findViewById(R.id.qrcode_android);

        save = (Button)rootView.findViewById(R.id.save_user);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                String email = editEmail.getText().toString();

                String uuid = null;
                try {
                    uuid = ((Launch2Activity)getActivity()).getBasaManager().getUserManager()
                            .registerNewUser(username, email, null);

                    Bitmap image = QRCodeGenerator.encodeAsBitmap(uuid);
                    imageViewQRCode.setImageBitmap(image);
                    editUsername.setError(null);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    new SendEmailService(new CallbackMultiple() {
                        @Override
                        public void success(Object response) {

                        }

                        @Override
                        public void failed(Object error) {

                        }
                    }, email, "tema", WelcomeTemplate.getTemplate(), byteArray).execute();

                } catch (UserRegistrationException e) {
                    editUsername.setError(e.getMessage());
                } catch (WriterException e) {
                    e.printStackTrace();
                }



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


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_android:
                showScreen(R.id.layout_register_android);

                updateAndroidQRcode();

                break;
            case R.id.btn_email:
                showScreen(R.id.layout_register_email);
                break;



        }
    }

    private void updateAndroidQRcode(){
        try {
            WifiManager wm = (WifiManager) AppController.getAppContext().getSystemService(Activity.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress()) + ":" + Global.PORT;
//            RegisterAndroidQRCode code = new RegisterAndroidQRCode("1234", ip);
            ((TextView)rootView.findViewById(R.id.textViewIP)).setText(ip);
            RegisterAndroidQRCode device = new RegisterAndroidQRCode(ip, UserRegistrationToken.generateToken());
//            BasaDeviceInfo device = new BasaDeviceInfo("WzGE9m7AKgYUmgbW1sE8fzgVDQB2", ip, "Tagus 2N.11.5", "descrição", UserRegistrationToken.generateToken());

            Gson gson = new Gson();
            qrcode_android.setImageBitmap(QRCodeGenerator.encodeAsBitmap(gson.toJson(device)));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void showScreen(int id){
        rootView.findViewById(R.id.layout_pre).setVisibility(id == R.id.layout_pre? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.layout_register_android).setVisibility(id == R.id.layout_register_android? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.layout_register_email).setVisibility(id == R.id.layout_register_email? View.VISIBLE : View.GONE);

    }

}
