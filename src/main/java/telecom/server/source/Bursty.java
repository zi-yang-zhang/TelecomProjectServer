package telecom.server.source;

import telecom.server.LeakyBucketSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by robertzhang on 2015-03-29.
 */

/**
 * Bursty type traffic source where the server sends data packet of 120000 bytes every 15 seconds
 */
public class Bursty extends AbstractServerThread{

    public Bursty(Socket socket){
        this.socket = socket;
        setTimeType(BURSTY_RATE_TIME);
    }

    /**
     * Sends data packet as Bursty type, if leaky bucket is enabled, fill the bucket, otherwise send packet directly to client.
     * @throws IOException
     */
    @Override
    public synchronized void send() throws IOException {
        if(socket instanceof LeakyBucketSocket){
            if(!((LeakyBucketSocket) socket).getBucket().fill(BURSTY_PACKET)){

                System.out.println("\nThread " +hashCode()+": Bucket full, discard further packets");
            }

        }else{
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(BURSTY_PACKET);
        }
    }

}
