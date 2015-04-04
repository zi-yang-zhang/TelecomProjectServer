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
    private Thread sourceThread;
    private Thread receiverThread;
    private ServerReceiverWorker serverReceiverWorker;


    public LeakyBucketSocket(Socket socket,int bucketSize, int leakRate){
        this.socket = socket;
        this.bucketSize = bucketSize;
        this.leakRate = leakRate;
        serverReceiverWorker = new ServerReceiverWorker(socket);
        serverReceiverWorker.registerCommandListener(this);
        receiverThread = new Thread(serverReceiverWorker);
        receiverThread.start();

    }


    public Bucket getBucket(){
        return  bucket;
    }


    public void start() {
        System.out.println("Start sending data");
        sourceThread = new Thread(source);
        sourceThread.start();

    }

    public void stop() throws IOException, InterruptedException {
        source.terminate();
        serverReceiverWorker.terminate();
        receiverThread.join();
        sourceThread.join();
    }


    @Override
    public void onCommandReceived(RequestType type) throws IOException, InterruptedException {
        System.out.println("\nCommand Received: " + type.toString());
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
