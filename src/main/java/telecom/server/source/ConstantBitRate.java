package telecom.server.source;

import telecom.server.LeakyBucketSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by robertzhang on 2015-03-29.
 */
public class ConstantBitRate extends AbstractServerThread{
    public ConstantBitRate(Socket socket){
        this.socket = socket;
        setTimeType(CONSTANT_RATE_TIME);
    }

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
