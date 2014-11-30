package ut.beacondisseminationapp.common;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;


/**
* Downloader
*
*Rafi Rashid
*/
public class Downloader {
//	long datarate; //in bits per second
//	int downloadStrategy; //0,1,2
//	ArrayList<Chunk> masterFile;
//	ArrayList<Chunk> downloadedFile;
//	/*constructor*/
//	public Downloader(long rate, int strategy, ArrayList<Chunk> file) {
//        datarate = rate;
//        downloadStrategy = strategy;
//        masterFile = file;
//    }
//	/*datarate setter*/
//	public void set_datarate(long rate){
//		datarate = rate;
//	}
//	/*strategy setter*/
//	public void set_strategy(int strategy){
//		downloadStrategy = strategy;
//	}
//
//	/*datarate getter*/
//	public long get_datarate(){
//		return datarate;
//	}
//	/*strategy getter*/
//	public int get_strategy(){
//		return downloadStrategy;
//	}
//	/*download method*/
//	public void	download(){
//		int new_chunk = 0;
//		int current_chunk = 0;
//		int bit_size = 0;
//		long length = masterFile.size();
//		if (downloadStrategy == 0){ //from front to back
//			while (new_chunk < length){
//				while (bit_size < datarate){ //how many bits to move in a second
//					bit_size += masterFile.get(current_chunk).size*8;
//					current_chunk++;
//				}
//				bit_size = 0;
//
//				Thread.sleep(1000); //waits 1 second
//				while (new_chunk < current_chunk && new_chunk < length){ //move bits to move in one second
//					downloadedFile.get(new_chunk) = masterFile.get(new_chunk);
//					new_chunk++;
//				}
//			}
//		}else if(downloadStrategy == 1){ //from back to front
//			current_chunk = length;
//			new_chunk = length;
//			while (new_chunk > 0){
//				while (bit_size < datarate && current_chunk > 0){ //how many bits to move in a second
//					current_chunk--;
//					bit_size += masterFile.get(current_chunk).size*8;
//				}
//				bit_size = 0;
//				Thread.sleep(1000); //waits 1 second
//				while (new_chunk > current_chunk && new_chunk > 0){ //move bits for 1 sec
//					new_chunk--;
//					downloadedFile.get(new_chunk) = masterFile.get(new_chunk);
//
//				}
//			}
//		}else if(downloadStrategy == 2){ //randomly  move
//			Random rand = new Random();
//			int val = rand.nextInt(length);
//			Hashtable check = Hashtable(length);
//			Hashtable current = Hashtable(length);
//			while (new_chunk > 0){
//				while (bit_size < datarate){ //how many bits to move in a second
//					bit_size += masterFile.get(current_chunk).size*8;
//					val = rand.nextInt(length);
//					check.remove(val);
//				}
//				bit_size = 0;
//                try{
//                    Thread.sleep(1000); //waits 1 second
//                    while (check.get(val)!=current.get(val)){ //move bits for 1 sec
//                        val = rand.nextInt(length);
//                        downloadedFile.get(val) = masterFile.get(val);
//                    }
//                }
//                catch(InterruptedException e){
//
//                }
//			}
//		}
//	}
//    */
}

