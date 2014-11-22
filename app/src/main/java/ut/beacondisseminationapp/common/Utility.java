package ut.beacondisseminationapp.common;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Utility {
	
	public static InetAddress broadcastAddr = null;
	
	public final static int NUM_CHUNKS = 64;
	
	public final static int BUF_SIZE = 1024*101;
	//public final static int CHK_SIZE = 1024*32;
	
	public final static int BEACON_SIZE = 1024*2;
	//public final static int SUB_SIZE = 1024*1;
	
	public final static int NUM_ITEMS_CAP = 10;
	public final static int NUM_HOSTS = 4;
	
	public static final int CHUNK_PORT = 9000;
	public static final int BEACON_PORT = 9001;
	public static final int SUBSCRIPTION_PORT = 9002;

    public static final int RECEIVER_PORT = 15270;
    public static final int BROADCASTER_PORT = 15268;

    public static final int BEACON_INTERVAL =  200;

	public final static Random rng = new Random(System.currentTimeMillis());
	
	public static void init() {
		try {
			broadcastAddr = InetAddress.getByName("192.168.49.255");
		} catch (UnknownHostException e) {
            e.printStackTrace();
        }
	}

	public static int sizeOfSerial(Object obj) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj); // write object to bytearray
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray().length;
	}

	public static byte[] serialize(Object obj, int size) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.close();
			// get the byte array of the object
			byte[] objByteArray = baos.toByteArray();
			baos.close();
			return objByteArray;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Object deserialize(byte[] recvByteArray, int size) {
		try {
			//byte [] objByteArray = recvByteArray;
			/*byte [] objByteArray = new byte[size];
			
			for (int i = 0; i < size; ++i) {
				objByteArray[i] = recvByteArray[i];
			}*/

			ByteArrayInputStream bais = new ByteArrayInputStream(recvByteArray);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object deserializedObj = ois.readObject();
			ois.close();
			bais.close();

			return deserializedObj;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
