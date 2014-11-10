package ut.beacondisseminationapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class MadBroadcastActivity extends Activity {   //the main activity class for a MadApp Application
    WifiP2pManager mManager;   //the required infrastructure for the madapp app
    Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiManager nManager;
    Container txrxfifo = new Container();
    public static int data_length;
    public static String dataString = "sendthis";
    @Override
    protected void onCreate(Bundle savedInstanceState) {  //function called on app init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mad_broadcast);

        //Initalize the items needed. Copy, paste this into every madapp application
        //required by WiFi direct conventions

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new MadDirectLayer(mManager, mChannel, this, txrxfifo);

        //Assign the handlers for the intents
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



        data_length = dataString.length();
        //Thread tRecv = new Thread(new PacketReceiver());
        //tRecv.start();

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

    private byte[] getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address) { // fix for Galaxy Nexus. IPv4 is easy to use :-)
                            return inetAddress.getAddress();
                        }
                        //return inetAddress.getHostAddress().toString(); // Galaxy Nexus returns IPv6
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        } catch (NullPointerException ex) {
            //Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        }
        return null;
    }




    //Context thisCont = getApplicationContext();
    public void broadcastClickHand(View v){
        //Toast.makeText(getApplicationContext(), "Handler executed.", Toast.LENGTH_LONG).show();
        Log.d("MadApp", "Handler executed.");

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
    /*
    //Broadcaster tools
    InetAddress getBroadcastAddress() throws IOException {
        Log.d("MadAppASync","Getting broadcast address");
        WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        String localIp = getDottedDecimalIP(getLocalIPAddress());
        Log.d("LocalIp", localIp);
        //WifiInfo mWifiInfo = wifi.getConnectionInfo();
        //Log.d("IP in Mask Integer", mWifiInfo.getIpAddress()+"");
        //Log.e("IP Address", intToIP(mWifiInfo.getIpAddress())+"");
        //WifiP2pInfo w = new WifiP2pInfo();
        //InetAddress addr = w.groupOwnerAddress;
        //if (addr != null) {
        //    Log.d("Group Owner address: ", addr.getHostAddress());
        //}
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow
        if (dhcp == null) {
            Log.d("MadAppASync", "DHCP is null!");
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        Log.d("MadAppASync",InetAddress.getByAddress(quads).toString());
        InetAddress addr = InetAddress.getByName("192.168.49.255");
        return addr;
    }
   private class Broadcaster implements Runnable {

       @Override
       public void run() {
           try {
               android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
               byte[] buf = new byte[256];

               buf = dataString.getBytes();

               DatagramSocket socket = new DatagramSocket(15267);
               socket.setReuseAddress(true);
               //MadDirectLayer insideRec = (MadDirectLayer) mReceiver;
               //ArrayList<WifiP2pDevice> peergroup = insideRec.getPeers();
               InetAddress group;
               if(((MadDirectLayer) mReceiver).getGroupOwner() == null){
                   //group = InetAddress.getByName(getIPFromMac(((MadDirectLayer) mReceiver).getGroupOwner().deviceAddress));
                   Log.d("MadApp", "This is group owner");
               }
               else {
                   //group = InetAddress.getByName(Utils.getIPAddress(true));
                   //socket.joinGroup(group);
               }
               //InetAddress broadcastAddr = getBroadcastAddress();
               socket.setBroadcast(true);
               while (true) {
                   DatagramPacket packet = new DatagramPacket(buf, buf.length, getBroadcastAddress(), 15270);
                   socket.send(packet);
                   //Thread.sleep(1000); //TODO: don't sleep and close
                   //socket.close();
                   Log.d("Async", "Packet sent");
               }

           } catch (Exception e) {
               e.printStackTrace();
               Log.d("Error", "Error initalizing the push.");
           }
       }

   }

    private class PacketReceiver implements Runnable {
        //String data;

        @Override
        public void run (){
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            
            try {
                Thread.sleep(5000);
                WifiInfo connectionInfo = nManager.getConnectionInfo();
                String ssid = connectionInfo.getSSID();
                //nManager.isP2pSupported();
                Log.d("SSID", ssid);
                WifiManager.MulticastLock lock = nManager.createMulticastLock("dk.aboaya.pingpong");
                lock.acquire();
                //DatagramSocket serverSocket = new DatagramSocket(15270, InetAddress.getByName(getDottedDecimalIP(getLocalIPAddress())));
                DatagramSocket serverSocket = new DatagramSocket(15270);
                //serverSocket.setSoTimeout(15000);
                //byte[] data = new byte[UDPBatPositionUpdater.secretWord.length()];
                while(true) {
                    byte[] data = new byte[data_length];
                    DatagramPacket packet = new DatagramPacket(data, data_length);
                    serverSocket.receive(packet);
                    String s = new String(packet.getData());
                    if (s != null) {
                        Log.v("Receive", s);
                    }
                }
            }
            catch(Exception e){
                Log.d("Error.", "Exception");
            }

            //return null;
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
    */
}