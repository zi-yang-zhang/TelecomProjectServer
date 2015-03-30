package telecom.server.source;

import telecom.server.LeakyBucketSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by robertzhang on 2015-03-29.
 */
public class ConstantBitRate implements TrafficSource,Runnable{
    private Socket socket;
    private boolean running;
    public ConstantBitRate(Socket socket){
        this.socket = socket;
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

    @Override
    public void init() {
        Thread thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        running = true;
        long time = System.currentTimeMillis();
        long leakTime = System.currentTimeMillis();
        try {
            send();
            if(socket instanceof LeakyBucketSocket){
                ((LeakyBucketSocket) socket).sendFromLeakyBucket();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (running){
            long timeDif = System.currentTimeMillis() - time;
            if(timeDif > CONSTANT_RATE_TIME){
                try {
                    send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                time = System.currentTimeMillis();
            }
            long leakyTimeDif  = System.currentTimeMillis() - leakTime;
            if(socket instanceof LeakyBucketSocket && leakyTimeDif > LEAKY_BUCKET_TIME){
                try {
                   ((LeakyBucketSocket) socket).sendFromLeakyBucket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                leakTime = System.currentTimeMillis();
            }

        }
    }


    public void terminate(){
        this.running = false;
    }
}
