package ut.beacondisseminationapp.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TcpReceiver extends ObjectInputStream {

    InputStream is;

    public TcpReceiver(InputStream is) throws IOException {
        super(is);
        this.is = is;
    }

    public TcpReceiver(Socket s) throws IOException {
        this(s.getInputStream());
    }

    public Object receive() {
        try {
            return super.readObject();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return null;
        }
    }

    public void close() throws IOException {
        super.close();
    }
}