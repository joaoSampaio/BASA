package pt.ulisboa.tecnico.mybasaclient;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.estimote.sdk.SystemRequirementsChecker;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.adapter.PagerAdapter;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.ui.AccountFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.AddZonePart1Fragment;
import pt.ulisboa.tecnico.mybasaclient.ui.AddZonePart2Fragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceCameraFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceLightsFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceSettingsFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceTemperatureFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.HomeFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.InfoFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.LoginActivity;
import pt.ulisboa.tecnico.mybasaclient.ui.ScanQRCodeFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.UserFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.ZoneSettingsFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.ZoneSettingsInfoFragment;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;
import pt.ulisboa.tecnico.mybasaclient.util.VerticalViewPager;
import pt.ulisboa.tecnico.mybasaclient.util.ViewPagerPageScroll;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<ViewPagerPageScroll> pageListener;
    private UserFragment.CommunicationUserFragment communicationUserFragment;
    private HomeFragment.CommunicationHomeFragment communicationHomeFragment;
    private ScanQRCodeFragment.CommunicationScanFragment communicationScanFragment;
    private ZoneSettingsFragment.CommunicationSettings communicationSettings;
    private VerticalViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pageListener = new ArrayList<>();

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
                Log.d("viewpager", "onPageScrollStateChanged: "+state);
            }
        });


        openViewpagerPage(Global.HOME);
        init();
    }


    private void init(){

        if(!isZoneCreated()){
            //popup to create new zone
            openPage(Global.DIALOG_ADD_ZONE_PART1);
        }


        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        AppController.getInstance().beaconStart();


    }

    private boolean isZoneCreated(){
        try {
            List<Zone> zones = new ModelCache<List<Zone>>().loadModel(new TypeToken<List<Zone>>() {
            }.getType(), Global.DATA_ZONE);
            return zones != null && !zones.isEmpty();
        }catch (Exception e){
            //if no user is saved an exception my the thrown
            return false;
        }
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openViewpagerPage(int position){
        if(position == Global.HOME){
            viewPager.setCurrentItem(Global.HOME, true);
        }else {
            viewPager.setCurrentItem(Global.USER, true);
        }
    }


    public void openPage(int id){
        DialogFragment newFragment = null;
        String tag = "";

        if (id == Global.DIALOG_ADD_DEVICE) {
            newFragment = ScanQRCodeFragment.newInstance();
            tag = "ScanQRCodeFragment";
        } else if(id == Global.DIALOG_ADD_ZONE_PART1){
            newFragment = AddZonePart1Fragment.newInstance();
            tag = "AddZonePart1Fragment";
        } else if(id == Global.DIALOG_ADD_ZONE_PART2){
            newFragment = AddZonePart2Fragment.newInstance();
            tag = "AddZonePart2Fragment";
        } else if(id == Global.DIALOG_SETTINGS_ZONE){
            newFragment = ZoneSettingsFragment.newInstance();
            tag = "ZoneSettingsFragment";
        } else if(id == Global.DIALOG_SETTINGS_ZONE_INFO){
            newFragment = ZoneSettingsInfoFragment.newInstance();
            tag = "ZoneSettingsInfoFragment";
        } else if(id == Global.DIALOG_DEVICE){
            newFragment = DeviceFragment.newInstance();
            tag = "DeviceFragment";
        } else if(id == Global.DIALOG_INFO){
            newFragment = InfoFragment.newInstance();
            tag = "InfoFragment";
        } else if(id == Global.DIALOG_ACCOUNT){
            newFragment = AccountFragment.newInstance();
            tag = "AccountFragment";
        } else if(id == Global.DIALOG_DEVICE_TEMPERATURE){
            newFragment = DeviceTemperatureFragment.newInstance();
            tag = "DeviceTemperatureFragment";
        } else if(id == Global.DIALOG_DEVICE_SETTINGS){
            newFragment = DeviceSettingsFragment.newInstance();
            tag = "DeviceSettingsFragment";
        } else if(id == Global.DIALOG_DEVICE_LIGHT){
            newFragment = DeviceLightsFragment.newInstance();
            tag = "DeviceLightsFragment";
        } else if(id == Global.DIALOG_DEVICE_CAMERA){
            newFragment = DeviceCameraFragment.newInstance();
            tag = "DeviceCameraFragment";
        }



        if(newFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            newFragment.show(ft, tag);
        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // Handle the camera action
            id= Global.HOME;
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        openPage(id);
        return true;
    }

    public void dismissAllDialogs() {

        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        if (fragments == null)
            return;

        for (Fragment fragment : fragments) {
            if (fragment instanceof DialogFragment) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismissAllowingStateLoss();
            }

            FragmentManager childFragmentManager = fragment.getChildFragmentManager();
            if (childFragmentManager != null)
                dismissAllDialogs(childFragmentManager);
        }
    }

    public static void dismissAllDialogs(FragmentManager manager) {

        List<Fragment> fragments = manager.getFragments();

        if (fragments == null)
            return;

        for (Fragment fragment : fragments) {
            if (fragment instanceof DialogFragment) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismissAllowingStateLoss();
            }

            FragmentManager childFragmentManager = fragment.getChildFragmentManager();
            if (childFragmentManager != null)
                dismissAllDialogs(childFragmentManager);
        }
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
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(getCommunicationScanFragment() != null)
                    getCommunicationScanFragment().enableCamera();
            }
        }
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
}
