package pt.ulisboa.tecnico.basa.ui.setup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.SetupPagerAdapter;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.model.registration.BasaDeviceLoad;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.GetConfigService;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;
import pt.ulisboa.tecnico.basa.util.FirebaseHelper;


public class MainSetupActivity extends FragmentActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private ViewPager mPager;
    private Button config;
    private View indicator, action_prev, action_next;
    private PagerAdapter mPagerAdapter;
    private BasaDeviceConfig basaDeviceConfig;
    private SetupDataInterface basicListener;
    private SetupDatabaseFragment.SignInReady signInReady;
    private final static int[] VIEWPAGER_INDICATOR = {R.id.goto_first, R.id.goto_second, R.id.goto_third, R.id.goto_fourth, R.id.goto_fifth, R.id.goto_six};
    private int SIZE = SetupPagerAdapter.SIZE;

    private FirebaseAuth.AuthStateListener mAuthListener;
    String TAG = "main";
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;
    private FirebaseHelper helper = null;
    private BasaDeviceLoad basaDeviceLoad = null;

    public MainSetupActivity() {
        // Required empty public constructor
    }



    public static MainSetupActivity newInstance() {
        MainSetupActivity fragment = new MainSetupActivity();
        return fragment;
    }
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        loadUI();

    }

    public void loadUI(){

        basaDeviceConfig = new BasaDeviceConfig();
        AppController.getInstance().setDeviceConfig(basaDeviceConfig, false);
        config = (Button) findViewById(R.id.action_load_config);
        indicator = findViewById(R.id.indicator);
        action_next = findViewById(R.id.action_next);
        action_prev = findViewById(R.id.action_prev);
        action_next.setOnClickListener(this);
        action_prev.setOnClickListener(this);
        findViewById(R.id.action_load_config).setOnClickListener(this);
        mPager = (ViewPager) findViewById(R.id.pager_login);
        mPagerAdapter = new SetupPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        showPage(Global.PAGE_SETUP_WELCOME);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                showPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        for (int id : VIEWPAGER_INDICATOR)
            findViewById(id).setOnClickListener(this);

        String styledText = "<big> SKIP </big>" + "<br />"
                + "<small>" + "(load config)" + "</small>";
        config.setText(Html.fromHtml(styledText));
        initFirebase();


        mPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });

    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void enableViewPager(boolean enable){
        if(enable) {
            mPager.setOnTouchListener(null);
        } else {
            mPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

        }
    }

    public void saveConfig(){
        boolean error = false;
        Gson gson = new Gson();
        Log.d("main", " " + gson.toJson(basaDeviceConfig));

        if(basaDeviceConfig.getName() == null || basaDeviceConfig.getName().length() < 4) {
            showPage(Global.PAGE_SETUP_BASIC);
            if (basicListener != null && !basicListener.onBasicDataReady(basaDeviceConfig)) {

                return;

            }
        }

        if(basaDeviceConfig.isFirebaseEnabled() && ( basaDeviceConfig.getUuid() == null || basaDeviceConfig.getUuid().length() < 4)){
            Toast.makeText(AppController.getAppContext(), "Please sign in with google.", Toast.LENGTH_SHORT).show();
            showPage(Global.PAGE_SETUP_DATABASE);
            return;
        }

        if(!basaDeviceConfig.isFirebaseEnabled() && ( basaDeviceConfig.getUuid() == null || basaDeviceConfig.getUuid().length() < 4)){
            Toast.makeText(AppController.getAppContext(), "Please select an Uuid.", Toast.LENGTH_SHORT).show();
            showPage(Global.PAGE_SETUP_DATABASE);
            return;
        }

        if(!basaDeviceConfig.isPinDefined()){
            Toast.makeText(AppController.getAppContext(), "Please select a PIN.", Toast.LENGTH_SHORT).show();
            showPage(Global.PAGE_SETUP_SECURITY);
        }


        AppController.getInstance().setDeviceConfig(basaDeviceConfig, true);
            Intent intent = new Intent(this, Launch2Activity.class);
            startActivity(intent);
            finish();
    }


    private void showPage(int page){
        resetBackgroundBtns();
        ImageView view = (ImageView)findViewById(VIEWPAGER_INDICATOR[page]);
        view.setBackground(getResources().getDrawable(R.drawable.viewpager_indicator_on));
//        view.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


        if(page == 0) {
            config.setVisibility(View.VISIBLE);
            action_prev.setVisibility(View.GONE);
        }
        if(page == (SIZE-1)) {
            action_next.setVisibility(View.GONE);
        }

        mPager.setCurrentItem(page, true);
    }

    private void resetBackgroundBtns(){
        config.setVisibility(View.GONE);
        action_prev.setVisibility(View.VISIBLE);
        action_next.setVisibility(View.VISIBLE);
        View view;
        for(int id : VIEWPAGER_INDICATOR) {
            view = findViewById(id);
            view.setBackground(getResources().getDrawable(R.drawable.viewpager_indicator));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onClick(View v) {
        int page = mPager.getCurrentItem();
        switch (v.getId()){
            case R.id.goto_first:
                showPage(Global.PAGE_SETUP_WELCOME);
                break;
            case R.id.goto_second:
                showPage(Global.PAGE_SETUP_BASIC);
                break;
            case R.id.goto_third:
                showPage(Global.PAGE_SETUP_DATABASE);
                break;
            case R.id.goto_fourth:
                showPage(Global.PAGE_SETUP_LIGHT_TEMP);
                break;
            case R.id.goto_fifth:
                showPage(Global.PAGE_SETUP_LOCATION);
                break;
            case R.id.goto_six:
                showPage(Global.PAGE_SETUP_SECURITY);
                break;

            case R.id.action_next:


                showPage(page < (SIZE - 1)? (page+1) : (SIZE - 1));
                break;
            case R.id.action_prev:
                showPage(page > 0? (page-1) : 0);
                break;

            case R.id.action_load_config:
                showConfigDialog();
                break;
        }
    }

    public void showConfigDialog() {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_load_setup, null);
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);


        final EditText editTextUrl = (EditText) promptView.findViewById(R.id.editTextUrl);
        final EditText editTextConfig = (EditText) promptView.findViewById(R.id.editTextConfig);


        BasaDeviceLoad load = new BasaDeviceLoad();
        load.setName("Office Tagus 2.N.11.5");
        load.setDescription("uma descrição");
        load.setEdupLightId("ZH037CC7097B7CA91");
        load.setEdupNumLight(3);
        load.setPin(1234);
        load.setBeaconList("");
        load.setMacList("");

        load.setTemperatureChoice(1);
        load.setBeaconOrIp("2a11a5a1111111111111");
        Gson gson = new Gson();
        Log.d("conf", " " + gson.toJson(load));

        editTextConfig.setText(gson.toJson(load));


//        editTextConfig.setText("{\"BeaconList\":\"beacccccccc\",\"beaconOrIp\":\"2a11a5a1111111111111\",\"description\":\"uma descrição\",\"edupLightId\":\"ZH037CC7097B7CA91\",\"edupNumLight\":3,\"macList\":\"\",\"name\":\"Office Tagus 2.N.11.5\",\"pin\":1234,\"temperatureChoice\":1}");

        editTextUrl.setText("https://dl.dropboxusercontent.com/u/68830630/config.txt");


        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Load", null)
                .setNegativeButton("Cancel", null);

        // create an alert dialog
        final android.support.v7.app.AlertDialog alert = alertDialogBuilder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {

                Button okButton = alert.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        String url = editTextUrl.getText().toString().trim();
                        String config = editTextConfig.getText().toString().trim();

                        if(url.isEmpty() && config.isEmpty()) {
                            Snackbar snack = Snackbar.make(promptView, "You already have a zone with that name!", Snackbar.LENGTH_SHORT);
                            View view = snack.getView();
                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.RED);
                            snack.show();

                        }else{

                            if(config.length()> 0){

                                Gson gson = new Gson();
                                basaDeviceLoad = gson.fromJson(config, BasaDeviceLoad.class);

                                basaDeviceConfig.setEdupLightId(basaDeviceLoad.getEdupLightId());
                                basaDeviceConfig.setName(basaDeviceLoad.getName());

                                basaDeviceConfig.setTemperatureChoice(basaDeviceLoad.getTemperatureChoice());
                                basaDeviceConfig.setDescription(basaDeviceLoad.getDescription());
                                basaDeviceConfig.setEdupNumLight(basaDeviceLoad.getEdupNumLight());
                                basaDeviceConfig.setArduinoIP(basaDeviceLoad.getBeaconOrIp());
                                basaDeviceConfig.setBeaconUuidTemperature(basaDeviceLoad.getBeaconOrIp());
                                mPager.setAdapter(mPagerAdapter);
//                                mPagerAdapter.notifyDataSetChanged();

                            }else {

                                new GetConfigService(url, new CallbackMultiple<BasaDeviceLoad, Object>() {
                                    @Override
                                    public void success(BasaDeviceLoad response) {
                                        basaDeviceLoad = response;
                                        basaDeviceConfig.setEdupLightId(basaDeviceLoad.getEdupLightId());
                                        basaDeviceConfig.setName(basaDeviceLoad.getName());

                                        basaDeviceConfig.setTemperatureChoice(basaDeviceLoad.getTemperatureChoice());
                                        basaDeviceConfig.setDescription(basaDeviceLoad.getDescription());
                                        basaDeviceConfig.setEdupNumLight(basaDeviceLoad.getEdupNumLight());
                                        basaDeviceConfig.setArduinoIP(basaDeviceLoad.getBeaconOrIp());
                                        basaDeviceConfig.setBeaconUuidTemperature(basaDeviceLoad.getBeaconOrIp());
                                        mPager.setAdapter(mPagerAdapter);


                                    }

                                    @Override
                                    public void failed(Object error) {

                                    }
                                }).execute();

                            }

                            alert.dismiss();
//
                        }
                    }
                });
            }
        });
        alert.show();


    }


    private void initFirebase(){


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mAuth = FirebaseAuth.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in

                        AppController.getInstance().getDeviceConfig().setUuid(user.getUid());
                        if(getSignInReady() != null)
                            getSignInReady().onSignIn(user);


                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                    // ...
                }
            };
//            if(mAuth.getCurrentUser() == null) {
//                Log.d(TAG, "mAuth.getCurrentUser() == null");
//                signIn();
//
//            }

    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d(TAG, "Google Sign In was successful, authenticate with Firebase");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d(TAG, "Google Sign In failed, update UI appropriately:");

                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainSetupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }


    public SetupDatabaseFragment.SignInReady getSignInReady() {
        return signInReady;
    }

    public void setSignInReady(SetupDatabaseFragment.SignInReady signInReady) {
        this.signInReady = signInReady;
    }

    public SetupDataInterface getBasicListener() {
        return basicListener;
    }

    public void setBasicListener(SetupDataInterface basicListener) {
        this.basicListener = basicListener;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public BasaDeviceLoad getBasaDeviceLoad() {
        return basaDeviceLoad;
    }

    public interface SetupDataInterface{
        boolean onBasicDataReady(BasaDeviceConfig deviceConfig);
        void onAdvancedDataReady(BasaDeviceConfig deviceConfig);




    }

}
