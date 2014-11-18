package ut.beacondisseminationapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Random;

import ut.beacondisseminationapp.common.Chunk;
import ut.beacondisseminationapp.common.Utility;
import ut.beacondisseminationapp.protocol.Protocol;

public class MyDownloads extends Activity implements ImageGridFragment.OnImageGridListener,
        Protocol.DisseminationProtocolCallback{

    private static final String TAG = MyDownloads.class.getSimpleName();

    public static String userId;
    private boolean showSubscribe;
    private String streamName;
    private String streamId;
    double latitude;
    double longitude;
    private SharedPreferences mPrefs;
    //private int buttonsStackId = -1;
    //private int gridStackId = -1;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        //SharedPreferences.Editor ed = getApplicationContext().getSharedPreferences("ViewStreamPrefs",0).edit();
        //ed.putString("userId",userId);
        //ed.commit();
        //Log.d(TAG, "UserId at Pause: "+userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");
        //outState.putString("userId", userId);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onSaveInstanceState(savedState);
        //userId = savedState.getString("userId");
    }

    ArrayList<String> imageUrls;
    ArrayList<Integer> completedChunks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_my_downloads);

        WifiP2pManager mManager;   //the required infrastructure for the madapp app
        WifiP2pManager.Channel mChannel;
        BroadcastReceiver mReceiver;
        IntentFilter mIntentFilter;
        WifiManager nManager;
        Container txrxfifo = new Container();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //userId = extras.getString(Constants.USER_ID_KEY);
        } else {
            //userId = getApplicationContext().getSharedPreferences("ViewStreamPrefs", 0).getString("userId",null);
        }

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
        // data_length = dataString.length();
        //Thread tRecv = new Thread(new PacketReceiver());
        //tRecv.start();


        // Protocol specific initialization
        Utility.init();
        ArrayList<String> desiredItems = new ArrayList<String>();
        desiredItems.add("Item0");
        Protocol.initialize(desiredItems, txrxfifo, this);

        //Log.d(TAG, "userId: " + userId);
        if (findViewById(R.id.image_grid_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            //ImageGridFragment mIGFragment = ImageGridFragment.newInstance(null,null,null);
            Log.d(TAG, "Adding grid fragment");
            //getFragmentManager().beginTransaction().replace(R.id.stream_grid_container, mIGFragment).commit();
            imageUrls = new ArrayList<String> ();
            completedChunks = new ArrayList<Integer>();
            int chunk_count = 64;
            for (int i=0; i<chunk_count; ++i) {
                imageUrls.add("assets://100px_light_blue_square.jpg");
                completedChunks.add(0);
            }

            ImageGridFragment newIGFragment = ImageGridFragment.newInstance(null, imageUrls, null);
            getFragmentManager().beginTransaction()
                    .replace(R.id.image_grid_container, newIGFragment)
                    //.addToBackStack(null)
                    .commit();
        }

        //Thread spoofChunkThread = new Thread(new SpoofChunkReceive(null,null));
        //spoofChunkThread.start();

        /*if (findViewById(R.id.stream_buttons_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            if (userId != null) {
                showSubscribe = true;
            } else {
                showSubscribe = false;
            }
            StreamButtonsFragment mSBFragment = StreamButtonsFragment.newInstance(showSubscribe);

            Log.d(TAG, "Adding buttons fragment");
            // would set arguments from intent here, but we don't have any
            getFragmentManager().beginTransaction().replace(R.id.stream_buttons_container, mSBFragment).commit();
        }*/
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //FragmentManager fm = getFragmentManager();
        //int backStackEntries = fm.getBackStackEntryCount();
        super.onBackPressed();
        /*if (backStackEntries > 0) {
            setTitle("Top Streams");
            getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public void imageClickHandler() {
        Log.d(TAG, "imageClickHandler");

        Protocol.populateDummyItem();
    }

    @Override
    public void itemComplete(String itemId, Object[] contents) {
        //setTitle("Done!");
    }

    @Override
    public void chunkComplete(Chunk completedChunk) {
        imageUrls.set(completedChunk.chunkId, "assets://100px_blue_square.png");
        ImageGridFragment newIGFragment = ImageGridFragment.newInstance(null, imageUrls, null);
        getFragmentManager().beginTransaction()
                .replace(R.id.image_grid_container, newIGFragment)
                        //.addToBackStack(null)
                .commit();
    }


    private class SpoofChunkReceive implements Runnable {   //constant retrieval process


        public SpoofChunkReceive(ArrayList<String> imageUrls, ArrayList<Integer> completedChunks) {

        }

        @Override
        public void run (){
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                int count = 0;
                while(count < 64) {
                    int select_chunk;
                    do {
                        select_chunk = (new Random()).nextInt(64);
                        //Log.d(TAG, "Check chunk: "+select_chunk);
                    } while (completedChunks.get(select_chunk) == 1);
                    count++;
                    imageUrls.set(select_chunk, "assets://100px_blue_square.png");

                    completedChunks.set(select_chunk, 1);
                    ImageGridFragment newIGFragment = ImageGridFragment.newInstance(null, imageUrls, null);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.image_grid_container, newIGFragment)
                            //.addToBackStack(null)
                            .commit();
                    Thread.sleep(250);

                }
            }
            catch(Exception e){
                //serverSocket.close();
                e.printStackTrace();
                Log.d("Error.", "Exception with the receive thread.");
            }

            //return null;
        }
    }

}
