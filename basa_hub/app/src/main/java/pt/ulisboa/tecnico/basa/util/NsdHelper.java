package pt.ulisboa.tecnico.basa.util;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NsdHelper {
    Context mContext;

    NsdManager mNsdManager;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;


    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    public static final String SERVICE_TYPE = "urn:schemas-basa-pt:service:climate:1";

    public static final String TAG = "NsdHelper";
    public String mServiceName = "NsdChat";

    NsdServiceInfo mService;

    public NsdHelper(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);


        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);


    }

    public void initializeNsd() {
//        initializeResolveListener();
//        initializeDiscoveryListener();
//        initializeRegistrationListener();

        mChannel = mManager.initialize(mContext, mContext.getMainLooper(), null);
//        mManager.connect(mChannel, new WifiP2pConfig(), new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//                Log.d(TAG, "onSuccess3");
//            }
//
//            @Override
//            public void onFailure(int reason) {
//                Log.d(TAG, "onFailure3");
//            }
//        });
        mManager.setUpnpServiceResponseListener(mChannel, new WifiP2pManager.UpnpServiceResponseListener() {
            @Override
            public void onUpnpServiceAvailable(List<String> uniqueServiceNames, WifiP2pDevice srcDevice) {
                Log.d(TAG, "onUpnpServiceAvailable:" + uniqueServiceNames.size());
                for (String s : uniqueServiceNames)
                    Log.d(TAG, "s:" + s);

                Log.d(TAG, "describeContents:" + srcDevice.describeContents());
            }
        });
        mManager.setServiceResponseListener(mChannel, new WifiP2pManager.ServiceResponseListener() {
            @Override
            public void onServiceAvailable(int protocolType, byte[] responseData, WifiP2pDevice srcDevice) {
                Log.d(TAG, "onServiceAvailable:" + srcDevice.describeContents());
                Log.d(TAG, "protocolType:" + protocolType);
                Log.d(TAG, "responseData:" + new String(responseData));
            }
        });
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess4");

            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure4:"+reason);
            }
        });
        mManager.addLocalService(mChannel, WifiP2pUpnpServiceInfo.newInstance("11111111-fca6-4070-85f4-1fbfb9add62c", "urn:schemas-basa-pt:service:climate:1", new ArrayList<String>()), new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess5");

            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure5:"+reason);
            }
        });
        mManager.addServiceRequest(mChannel, WifiP2pUpnpServiceRequest.newInstance("ssdp:all"), new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess");

            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure:"+reason);
            }
        });
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess2");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure:"+reason);
            }
        });
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same machine: " + mServiceName);
                } else if (service.getServiceName().contains(mServiceName)){
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                if (mService == service) {
                    mService = null;
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
            }
        };
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                mServiceName = NsdServiceInfo.getServiceName();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

        };
    }

    public void registerService(int port) {
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

    }

    public void discoverServices() {
//        mNsdManager.discoverServices(
//                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Log.d(TAG, "onPeersAvailable:" + peers.getDeviceList().size());
                        for (WifiP2pDevice device : peers.getDeviceList())
                            Log.d(TAG, "device:" + device.toString());

                        Log.d(TAG, "describeContents:" + peers.describeContents());
                    }
                });
            }
        },2000);
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }

    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
    }
}

