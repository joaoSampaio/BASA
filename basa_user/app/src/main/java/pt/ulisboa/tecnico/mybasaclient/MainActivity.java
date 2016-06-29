package pt.ulisboa.tecnico.mybasaclient;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.adapter.PagerAdapter;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.ui.AddZonePart1Fragment;
import pt.ulisboa.tecnico.mybasaclient.ui.AddZonePart2Fragment;
import pt.ulisboa.tecnico.mybasaclient.ui.ScanQRCodeFragment;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;
import pt.ulisboa.tecnico.mybasaclient.util.VerticalViewPager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private VerticalViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);



        viewPager = (VerticalViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        openViewpagerPage(Global.HOME);
        //openPage(Global.HOME);
    }


    private void init(){

        if(!isZoneCreated()){
            //popup to create new zone



        }

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            viewPager.setCurrentItem(Global.HOME);
        }else {
            viewPager.setCurrentItem(Global.USER);
        }
    }


    public void openPage(int id){
        DialogFragment newFragment = null;
        String tag = "";

        if (id == Global.QRCODE) {
            newFragment = ScanQRCodeFragment.newInstance();
            tag = "ScanQRCodeFragment";
        } else if(id == Global.DIALOG_ADD_ZONE_PART1){
            newFragment = AddZonePart1Fragment.newInstance();
            tag = "AddZonePart1Fragment";
        }else if(id == Global.DIALOG_ADD_ZONE_PART2){
            newFragment = AddZonePart2Fragment.newInstance();
            tag = "AddZonePart2Fragment";
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




//
//        if (id == Global.HOME) {
//            // Handle the camera action
//            fragment = HomeFragment.newInstance();
//        } else if (id == Global.QRCODE) {
//            fragment = ScanQRCodeFragment.newInstance();
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        if(fragment != null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.frame, fragment).addToBackStack("id"+id).commit();
//
//
//        }
////        // Highlight the selected item has been done by NavigationView
////        menuItem.setChecked(true);
//        // Set action bar title
//        setTitle("ola");
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
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
}
