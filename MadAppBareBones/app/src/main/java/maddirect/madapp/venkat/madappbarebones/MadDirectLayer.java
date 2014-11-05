package maddirect.madapp.venkat.madappbarebones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Venkat on 11/4/14.
 */

public class MadDirectLayer extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private MadBroadcastActivity mActivity;

    PeerListListener myPeerListListener;
    private ArrayList<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
    private WifiP2pDevice groupOwner;
    public MadDirectLayer(WifiP2pManager manager, Channel channel,
                                       MadBroadcastActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }
    public Context getAppContext(){
        return mActivity;
    }
    public WifiP2pDevice getGroupOwner(){
        return groupOwner;
    }
    public ArrayList<WifiP2pDevice> getPeers(){
        return peersList;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        //Start the next intent.
                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        //Something went wrong.
                        Toast.makeText(mActivity, "Discovery failed for some reason.", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                Toast.makeText(mActivity, "Error: WiFi Direct is not enabled.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (mManager != null) {
                    mManager.requestPeers(mChannel, new PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            peersList.clear();  //Remove the previous list of peers and repopulate it
                            peersList.addAll(peers.getDeviceList());
                            Log.d("MadApp", String.format("PeerListListener: %d peers available, updating device list", peers.getDeviceList().size()));

                            for(WifiP2pDevice k: peersList){
                                if(k.isGroupOwner()){
                                    groupOwner = k;
                                    Log.d("MadApp", "Group Owner Identified: "+k.deviceName);
                                }
                            }

                            if (peersList.size() == 0) {
                                Log.d("MadApp", "No devices found");
                                return;
                            }


                        }
                    });
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
            }
        }
    }
}
