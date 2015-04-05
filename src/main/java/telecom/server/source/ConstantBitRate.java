package telecom.server.source;

import telecom.server.LeakyBucketSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by robertzhang on 2015-03-29.
 */

/**
 * Constant bit rate type traffic source where the server sends data packet of 800 bytes every 100 milliseconds
 */
public class ConstantBitRate extends AbstractServerThread{
    public ConstantBitRate(Socket socket){
        this.socket = socket;
        setTimeType(CONSTANT_RATE_TIME);
    }
    /**
     * Sends data packet as Constant bit rate type,
     * if leaky bucket is enabled, fill the bucket, otherwise send packet directly to client.
     * @throws IOException
     */
    @Override
    public synchronized void send() throws IOException {
        if(socket instanceof LeakyBucketSocket){
            ((LeakyBucketSocket) socket).getBucket().fill(CONSTANT_RATE_PACKET);
        }else{
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(CONSTANT_RATE_PACKET);
        }
    }

}
