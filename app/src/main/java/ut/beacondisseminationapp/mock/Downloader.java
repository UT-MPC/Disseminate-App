package ut.beacondisseminationapp.common;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
* Downloader
*
*Rafi Rashid
*/
public class Downloader {
	public Timer timer; //timer
	public long datarate; //in bits per second
	public int downloadStrategy; //0,1,2
	public ArrayList<Chunk> masterFile;
	public ArrayList<Chunk> downloadedFile;
	/*variables for moving data*/
	public int current_chunk; //keeps track of how much to move in a second
	public int new_chunk; //moves until moved enough for 1 second
	public long length; //length of the arraylist
	/*variables for random movement*/
	public Random rand = new Random();
	public int val;
	public Hashtable check;
	public Hashtable current;
	/*constructor*/
	public Downloader(long datarate, int downloadStrategy, ArrayList<Chunk> masterFile){
		this.datarate = datarate;
		this.downloadStrategy = downloadStrategy;
		this.masterFile = masterFile;
	/*datarate setter*/
	public void set_datarate(long datarate){
		this.datarate = datarate;
	}
	/*strategy setter*/
	public void set_strategy(int downloadStrategy){
		this.downloadStrategy = downloadStrategy;
	}
	/*datarate getter*/
	public long get_datarate(void){
		return this.datarate;
	}
	/*strategy getter*/
	public int get_strategy(void){
		return this.downloadStrategy;
	}
	//downloadtask for strategy 0
	class DownloadTask0 extends TimerTask {
		public void run(){
			while (new_chunk < current_chunk && new_chunk < length){ //move bits to move in one second
				downloadedFile.get(new_chunk) = masterFile.get(new_chunk);
				new_chunk++;
			}
			timer.cancel(); //terminate timer thread
		}

	}
	//downloadtask for strategy 1
	class DownloadTask1 extends TimerTask {
		public void run(){
			while (new_chunk > current_chunk){
				new_chunk--;
				downloadedFile.get(new_chunk) = masterFile.get(new_chunk);
			}
			timer.cancel();
		}

	}
	//download task for strategy 2
	class DownloadTask2 extends TimerTask {
		public void run(){
			while (!check.containsValue(val)){ //move bits for 1 sec
				val = rand.nextInt(length);
				if(!check.containsValue(val)&&current.containsValue(val)){
					downloadedFile.get(val) = masterFile.get(val);
				}
			}
			timer.cancel();
		}

	}
	/*download method*/
	public void	download(void){
		timer = new Timer(); //initalize timer
		int bit_size = 0; //counter of how many bits to move in a second
		length = masterFile.size(); //set length to length of the masterfile

		if (downloadStrategy == 0){ //from front to back
			current_chunk = 0;
			new_chunk = 0;
			while (new_chunk < length){
				while (bit_size < datarate){ //how many bits to move in a second
					bit_size += masterFile.get(current_chunk).size*8;
					current_chunk++; //incrementing to the chunk needed
				}
				//current chunk is now how many to move in a second
				bit_size = 0;
				timer.schedule(new DownloadTask0(),1000);
			}
		}else if(downloadStrategy == 1){ //from back to front
			current_chunk = length;
			new_chunk = length;
			while (new_chunk > 0){
				while (bit_size < datarate){ //how many bits to move in a second
					current_chunk--;
					bit_size += masterFile.get(current_chunk).size*8;
				}
				bit_size = 0;
				timer.schedule(new DownloadTask1(),1000);
			}
		}else if(downloadStrategy == 2){ //randomly move
			val = rand.nextInt(length);
			check = Hashtable(length);
			current = Hashtable(length);
			while (!check.isEmpty()){ //while check hashtable is not empty
				while (bit_size < datarate){ //how many bits to move in a second
					bit_size += masterFile.get(val).size*8; //increase bit count
					check.remove(val); //remove value from check hashtable
					while(!check.containsValue(val)){ //while val is not in check, find another val
						val = rand.nextInt(length);
					}
				}
				//all values moved in ~second have been taken out of check
				bit_size = 0;//reset bit counter size
				timer.schedule(new DownloadTask2(),1000);
			}
		}
	}
}
