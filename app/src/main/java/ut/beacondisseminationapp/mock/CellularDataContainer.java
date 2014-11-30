package ut.beacondisseminationapp.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ut.beacondisseminationapp.common.Chunk;

/**
 * Created by Venkat on 11/29/14.
 */
public class CellularDataContainer {
    private ArrayList<Chunk> serverData = new ArrayList<Chunk>();
    private ArrayList<Chunk> downloadedData = new ArrayList<Chunk>();

    private int RxIndex; //Index for the downloaded Data (client simulator)
    private int TxIndex; //Index for the transmitted Data (server simulator)

    private int itemsDownloaded = 0; //used only by the random download process

    HashMap<Integer, Integer> filledValues;
    Random indexFinder;

    public CellularDataContainer(ArrayList<Chunk> serverContent, int dir){
        serverData = serverContent;
        if(dir == 0){
            RxIndex = TxIndex = 0;
        }
        else if(dir == 1){
            RxIndex = 0;
            TxIndex = serverData.size()-1;
        }
        else{
            filledValues = new HashMap<Integer, Integer>(serverData.size());
            for(int i=0; i<serverData.size(); i++){
                downloadedData.add(null);
            }

            RxIndex = TxIndex = indexFinder.nextInt()%serverData.size();  //find the next index to "download"
            itemsDownloaded = 0;
        }


    }
    public int bytesInNextChunk(){
        return serverData.get(TxIndex).size;

    }
    public boolean forwardDownload(){
        if(TxIndex == serverData.size()){
            return false; //transfer failed since the server has no more new data
        }
        downloadedData.add(serverData.get(TxIndex));
        TxIndex++;
        RxIndex++;
        return true; //transfer is successful.
    }
    public boolean backwardDownload(){
        if(TxIndex == -1){
            return false; //out of data to download
        }
        downloadedData.add(serverData.get(TxIndex));
        TxIndex--;
        RxIndex++;
        return true; //successful download
    }

    public boolean nextRandDownload(){
        if(itemsDownloaded == serverData.size()){
            return false; //transfer failed since the server has no more new data
        }
        downloadedData.set(RxIndex, serverData.get(TxIndex));
        itemsDownloaded++;
        filledValues.put(TxIndex, TxIndex);
        while(filledValues.containsKey(TxIndex)){
            TxIndex=RxIndex=indexFinder.nextInt()%serverData.size();
        }
        return true;

    }

    public void reInitSystem(){
        RxIndex=0;
        TxIndex=0;
        downloadedData.clear();
    }

    public void changeServerData(ArrayList<Chunk> newServer){
        serverData = newServer;
        reInitSystem();
    }


}
