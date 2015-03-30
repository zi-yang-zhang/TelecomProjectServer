package telecom.server;

import telecom.server.leakyBucket.Bucket;
import telecom.server.protocol.RequestType;
import telecom.server.source.Bursty;
import telecom.server.source.CommandListener;
import telecom.server.source.ConstantBitRate;
import telecom.server.source.TrafficSource;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by robertzhang on 2015-03-27.
 */
public class LeakyBucketSocket extends Socket implements CommandListener{


    private TrafficSource source;
    private Bucket bucket;
    private Socket socket;
    private int bucketSize;
    private int leakRate;


    public LeakyBucketSocket(Socket socket,int bucketSize, int leakRate){
        this.socket = socket;
        this.bucketSize = bucketSize;
        this.leakRate = leakRate;
    }


    public Bucket getBucket(){
        return  bucket;
    }


    public void start() {
        System.out.println("Start sending data");
        source.init();
    }

    public void stop() throws IOException {
        source.terminate();
        socket.close();
    }


    @Override
    public void onCommandReceived(RequestType type) throws IOException {
        System.out.println("Command Received: " + type.toString());
        System.out.println("Command Code: " + type.getCommand());
        switch (type){
            case ConstantBitRate:
                this.source = new ConstantBitRate(socket);
                break;
            case ConstantBitRateWithLeakyBucket:
                bucket = new Bucket(bucketSize,leakRate);
                this.source = new ConstantBitRate(this);
                break;
            case Bursty:
                this.source = new Bursty(socket);
                break;
            case BurstyWithLeakyBucket:
                bucket = new Bucket(bucketSize,leakRate);
                this.source = new Bursty(this);
                break;
            case Close:
                stop();
                break;

        }

        start();
    }

    public void sendFromLeakyBucket() throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.write(bucket.leak());
    }
}
