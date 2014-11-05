package maddirect.madapp.venkat.madappbarebones;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.ArrayList;


public class MadBroadcastActivity extends Activity {
    WifiP2pManager mManager;
    Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiManager nManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mad_broadcast);

        //Initalize the items needed.
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new MadDirectLayer(mManager, mChannel, this);

        //Assign the handlers for the intents
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        nManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        recieveSocket recsock = new recieveSocket();
        recsock.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mad_broadcast, menu);
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
            Toast.makeText(getApplicationContext(), "No item assigned.", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    //Broadcaster tools
    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        Log.d("MadAppASync",InetAddress.getByAddress(quads).toString());
        return InetAddress.getByAddress(quads);
    }
    //Context thisCont = getApplicationContext();
    public void broadcastClickHand(View v){
        //Toast.makeText(getApplicationContext(), "Handler executed.", Toast.LENGTH_LONG).show();
        Log.d("MadApp", "Handler executed.");
        Broadcaster newSocket = new Broadcaster();
        newSocket.execute();
    }

   public void refreshHand(View v){
       mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
           @Override
           public void onSuccess() {
               //Start the next intent.
           }
           @Override
           public void onFailure(int reasonCode) {
               //Something went wrong.
               Toast.makeText(getApplicationContext(), "Discovery failed for some reason.", Toast.LENGTH_SHORT).show();
           }
       });
   }
   private class Broadcaster extends AsyncTask<String, Void, Void> {
       String dataString = "sendthis";

       public Broadcaster() {

       }

       @Override
       protected Void doInBackground(String... urls) {
           boolean keepPushing = true;
           try {
               while (keepPushing) {
                   byte[] buf = new byte[256];

                   buf = dataString.getBytes();

                   MulticastSocket socket = new MulticastSocket(8888);
                   MadDirectLayer insideRec = (MadDirectLayer) mReceiver;
                   ArrayList<WifiP2pDevice> peergroup = insideRec.getPeers();
                   InetAddress group;
                   if(((MadDirectLayer) mReceiver).getGroupOwner() == null){
                       group = InetAddress.getByName(getIPFromMac(((MadDirectLayer) mReceiver).getGroupOwner().deviceAddress));
                       Log.d("MadApp", "This is group owner");
                   }
                   else {
                       group = InetAddress.getByName(Utils.getIPAddress(true));
                       //socket.joinGroup(group);
                   }
                   InetAddress broadcastAddr = getBroadcastAddress();
                   socket.setBroadcast(true);
                   DatagramPacket packet;
                   packet = new DatagramPacket(buf, buf.length, group, 8888);
                   socket.send(packet);
                   Thread.sleep(1000);
                   socket.close();
                   Log.d("Async", "Finished.");
               }

           } catch (Exception e) {
               e.printStackTrace();
               keepPushing = false;
               Log.d("Error", "Error initalizing the push.");
           }
           return null;
       }

   }

    private class recieveSocket extends AsyncTask<String, Void, Void> {
        String data;

        public recieveSocket(){

        }

        @Override
        protected Void doInBackground(String...urls){

            WifiManager.MulticastLock lock = nManager.createMulticastLock("dk.aboaya.pingpong");
            lock.acquire();
            try {

            }
            catch(Exception e){
                Log.d("Error.", "Exception");
            }
            return null;
        }
    }
    //Asynchronous broadcaster
    private class broadcasterSocket extends AsyncTask<URL, Integer, Long> {
        String data = "1";
        @Override
        protected Long doInBackground(URL... urls) {
            String message = "Hello";
            int port = 8888;
            byte[] buffer = message.getBytes();


            MulticastSocket socket = null;
            try {
                socket = new MulticastSocket(port);
                socket.setBroadcast(true);
                socket.connect(getBroadcastAddress(), port);
            }
            catch(Exception e) {
                Log.d("ConnectionError", "Here");
            }
            try{

                DatagramPacket packet = new DatagramPacket(
                        buffer, buffer.length, getBroadcastAddress(), port);

                socket.send(packet);
            } catch (IOException e) {
                Log.d("MadAppASync", e.getMessage());
            }
           catch(Exception e){

               Log.d("MadAppASync", e.getMessage());
           }
               return new Long(1);
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
}
    public static String getIPFromMac(String MAC) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {

                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    // Basic sanity check
                    String device = splitted[5];
                    if (device.matches(".*p2p-p2p0.*")){
                        String mac = splitted[3];
                        if (mac.matches(MAC)) {
                            return splitted[0];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}