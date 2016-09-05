package pt.ulisboa.tecnico.mybasaclient.util;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.ui.AccountFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.AddZonePart1Fragment;
import pt.ulisboa.tecnico.mybasaclient.ui.AddZonePart2Fragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceCameraFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceLightsFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceSettingsFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.DeviceTemperatureFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.InfoFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.ScanNetworkFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.ScanQRCodeFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.ZoneSettingsFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.ZoneSettingsInfoFragment;

/**
 * Created by Sampaio on 05/09/2016.
 */
public class CreateDialogHelper {



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


    public static void openPage(int id, AppCompatActivity activity){
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
        }else if(id == Global.DIALOG_DEVICE_SCAN_WIFI){
            newFragment = ScanNetworkFragment.newInstance();
            tag = "ScanNetworkFragment";
        }

        if(newFragment != null) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            Fragment prev = activity.getSupportFragmentManager().findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            newFragment.show(ft, tag);
        }

    }


}
