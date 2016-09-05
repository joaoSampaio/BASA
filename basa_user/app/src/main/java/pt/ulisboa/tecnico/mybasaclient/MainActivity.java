package pt.ulisboa.tecnico.mybasaclient;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.adapter.PagerAdapter;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.manager.DeviceManager;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.User;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.ui.HomeFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.LoginActivity;
import pt.ulisboa.tecnico.mybasaclient.ui.ScanQRCodeFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.UserFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.ZoneSettingsFragment;
import pt.ulisboa.tecnico.mybasaclient.util.CreateDialogHelper;
import pt.ulisboa.tecnico.mybasaclient.util.FirebaseHelper;
import pt.ulisboa.tecnico.mybasaclient.util.GenericCommunicationToFragment;
import pt.ulisboa.tecnico.mybasaclient.util.VerticalViewPager;
import pt.ulisboa.tecnico.mybasaclient.util.ViewPagerPageScroll;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity
        implements
        GoogleApiClient.OnConnectionFailedListener {

    private List<ViewPagerPageScroll> pageListener;
    private UserFragment.CommunicationUserFragment communicationUserFragment;
    private HomeFragment.CommunicationHomeFragment communicationHomeFragment;
    private ScanQRCodeFragment.CommunicationScanFragment communicationScanFragment;
    private ZoneSettingsFragment.CommunicationSettings communicationSettings;
    private List<GenericCommunicationToFragment> toFragmentList;
    private VerticalViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;
    private Bundle extras;
    private boolean isRunning = false;

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseHelper mHelper;
    private DeviceManager mManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(AppController.getInstance().getLoggedUser() == null || FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        pageListener = new ArrayList<>();
        toFragmentList = new ArrayList<>();
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        viewPager = (VerticalViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d("viewpager", "onPageScrolled position: "+position + " || positionOffset: "+positionOffset);
                for (ViewPagerPageScroll scroll: pageListener){
                    scroll.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("viewpager", "onPageSelected: "+position);
                for (ViewPagerPageScroll scroll: pageListener){
                    scroll.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        openViewpagerPage(Global.HOME);
        init();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        String token = FirebaseInstanceId.getInstance().getToken();
//        AppController.getInstance().resetData();
        subscribeToDevices();
    }

    @Override
    protected void onPause() {
        super.onPause();

        AppController.getInstance().saveData();
        if(mManager != null)
            mManager.clearAllListeners();
        toFragmentList.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    private void init(){

        if(!Zone.isZoneCreated()){
            //popup to create new zone
            openPage(Global.DIALOG_ADD_ZONE_PART1);
        }

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        //AppController.getInstance().beaconStart();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("main", "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d("main", "onAuthStateChanged:signed_in:" + user.getDisplayName());


                    if(mDatabase == null) {
                        User userBasa = AppController.getInstance().getLoggedUser();
                        userBasa.setUuid(user.getUid());
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mHelper = new FirebaseHelper(mDatabase);
                        mHelper.setActivity(MainActivity.this);
                        mHelper.registerUser(user.getUid(), AppController.getInstance().getLoggedUser().getUserName(), user.getEmail());

                        mManager = new DeviceManager(mHelper);
                        if (AppController.getInstance().getCurrentZone() != null)
                            mManager.setCurrentZone(AppController.getInstance().getCurrentZone());
                    }

//
//                    helper.registerUser(user.getUid());

                } else {
                    // User is signed out
                    Log.d("main", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        if(mAuth.getCurrentUser() == null)
            signIn();

        isRunning = true;

        try {
            extras = getIntent().getExtras();
            Log.d("main", "getCommunicationHomeFragment2():" + (getCommunicationHomeFragment() != null));
            Log.d("main", "getCommunicationUserFragment2():" + (getCommunicationUserFragment() != null));
            Log.d("main", "oncreate extras:" + (extras != null) );
            if(extras != null && !extras.isEmpty()) {
                int action = extras.getInt("code", -1);

                switch (action){
                    case 0:
                        //alert movement
                        openSecurity(extras.getString("senderId", ""));
                        break;

                }
            }
//            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel(Global.NOTIFICATION_ID);
//            NotificationCount.resetCount(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void openViewpagerPage(int position){
        if(position == Global.HOME){
            viewPager.setCurrentItem(Global.HOME, true);
        }else {
            viewPager.setCurrentItem(Global.USER, true);
        }
    }


    public void openPage(int id){
        CreateDialogHelper.openPage(id, this);
        Log.d("main", "openPage():" + id);
    }

    public void dismissAllDialogs(FragmentManager manager) {
        CreateDialogHelper.dismissAllDialogs(manager);
    }

    public void dismissAllDialogs() {
        CreateDialogHelper.dismissAllDialogs(getSupportFragmentManager());
    }


    private static final int REQUEST_CAMERA = 20;

    public boolean mayRequestCamera() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(CAMERA)) {
            Snackbar.make(coordinatorLayout, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                        }
                    });
        } else {
            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(getCommunicationScanFragment() != null)
                    getCommunicationScanFragment().enableCamera();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("main", "onActivityResult:" + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d("main", "Google Sign In was successful, authenticate with Firebase");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d("main", "Google Sign In failed, update UI appropriately:");

                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("main", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("main", "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("main", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    public void showMessage(String text){
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).show();
    }


    public void signOut(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        Bundle b = new Bundle();
        b.putBoolean("signout", true);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }


    public void enableSwipe(boolean enable){

        if(enable){
            viewPager.setOnTouchListener(null);
        }else{
            viewPager.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return true;
                }
            });
        }
    }

    public void subscribeToDevices(){
        List<BasaDevice> basaDevices = BasaDevice.getAllDevicesAllZones();
        for (BasaDevice device : basaDevices)
            FirebaseMessaging.getInstance().subscribeToTopic(device.getId());

    }


    @Override
    protected void onNewIntent(Intent intent) {
        try {
            super.onNewIntent(intent);
            extras = intent.getExtras();
            Log.d("main", "onNewIntent:" + extras.size() + " isRunning:"+isRunning);
            if(isRunning) {
                int action = intent.getExtras().getInt("code", -1);

                switch (action){
                    case 0:
                        //alert movement
                        openSecurity(intent.getExtras().getString("senderId", ""));
                        break;

                }
            }
//            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel(Global.NOTIFICATION_ID);
//            NotificationCount.resetCount(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //find device zone, open zone, open device camera
    private void openSecurity(String deviceId){
        Log.d("main", "openSecurity:" + deviceId);
        if(!deviceId.isEmpty()){

            Zone zone = BasaDevice.getDeviceZone(deviceId);
            Log.d("main", "zone:" + (zone != null));
            Log.d("main", "getCommunicationHomeFragment():" + (getCommunicationHomeFragment() != null));
            Log.d("main", "getCommunicationUserFragment():" + (getCommunicationUserFragment() != null));
            if(zone != null){
                Log.d("main", "zone:" + zone.getName());
                if(getCommunicationHomeFragment() != null && getCommunicationUserFragment() != null) {
                    getCommunicationHomeFragment().changeZone(zone.getName());
                    getCommunicationUserFragment().refreshZones();
                }
                AppController.getInstance().saveCurrentDevice(BasaDevice.getCurrentDevice().getDeviceById(deviceId));
                openPage(Global.DIALOG_DEVICE_CAMERA);
            }
        }
    }



    public void addPageListener(ViewPagerPageScroll listener){
        pageListener.add(listener);
    }
    public void removePageListener(ViewPagerPageScroll listener){
        pageListener.remove(listener);
    }

    public UserFragment.CommunicationUserFragment getCommunicationUserFragment() {
        return communicationUserFragment;
    }

    public void setCommunicationUserFragment(UserFragment.CommunicationUserFragment communicationUserFragment) {
        this.communicationUserFragment = communicationUserFragment;
    }

    public HomeFragment.CommunicationHomeFragment getCommunicationHomeFragment() {
        return communicationHomeFragment;
    }

    public void setCommunicationHomeFragment(HomeFragment.CommunicationHomeFragment communicationHomeFragment) {
        this.communicationHomeFragment = communicationHomeFragment;
    }

    public ScanQRCodeFragment.CommunicationScanFragment getCommunicationScanFragment() {
        return communicationScanFragment;
    }

    public void setCommunicationScanFragment(ScanQRCodeFragment.CommunicationScanFragment communicationScanFragment) {
        this.communicationScanFragment = communicationScanFragment;
    }

    public ZoneSettingsFragment.CommunicationSettings getCommunicationSettings() {
        return communicationSettings;
    }

    public void setCommunicationSettings(ZoneSettingsFragment.CommunicationSettings communicationSettings) {
        this.communicationSettings = communicationSettings;
    }

    public DeviceManager getmManager() {
        return mManager;
    }

    public void addGenericCommunication(GenericCommunicationToFragment listener){
        toFragmentList.add(listener);
    }

    public void removeGenericCommunication(GenericCommunicationToFragment listener){
        toFragmentList.remove(listener);
    }

    public List<GenericCommunicationToFragment> getGenericCommunicationList() {
        return toFragmentList;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
