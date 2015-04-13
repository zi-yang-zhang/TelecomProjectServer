package telecom.server;

import telecom.protocol.RequestType;
import telecom.server.leakyBucket.Bucket;
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

/**
 * Wrapper class that encapsulates leaky bucket function into socket, activates as a worker thread.
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


    /**
     * Wraps the socket with client connection with leaky bucket functionality, with defined bucket size and leak rate.
     * Activate receiver thread that listens to command from client.
     * @param socket Socket with client connection
     * @param bucketSize Bucket size defined by the server.
     * @param leakRate Leak rate of leaky bucket.
     */
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

    /**
     * Starts a worker thread with specific source defined by the command from client.
     * @param clientID client ID received from the client
     */
    public void start(int clientID) {
        System.out.println("Start sending data to client "+ clientID);
        sourceThread = new Thread(source);

        sourceThread.start();

    }

    /**
     * Stops the worker thread and receiver thread.
     * @param clientID client ID received from the client
     * @throws IOException
     * @throws InterruptedException
     */
    public void stop(int clientID) throws IOException, InterruptedException {
        System.out.println("Instance terminated by client " + clientID);
        serverReceiverWorker.terminate();
        source.terminate();



        MultiThreadedServer.cancelThread();
        MultiThreadedServer.displayThreadCount();


    }

    /**
     * Decide which traffic source to use based on the command received from client.
     * @param type Request type received.
     * @param clientID client ID received from the client
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void onCommandReceived(RequestType type, int clientID) throws IOException, InterruptedException {
        System.out.println("\nCommand Received: " + type.toString());
        System.out.println("Command Code: " + type.getCommand());
        System.out.println("Client ID: " +clientID);

        switch (type){
            case ConstantBitRate:
                this.source = new ConstantBitRate(socket, clientID);
                start(clientID);
                break;
            case ConstantBitRateWithLeakyBucket:
                bucket = new Bucket(bucketSize,leakRate);
                this.source = new ConstantBitRate(this,clientID);
                start(clientID);
                break;
            case Bursty:
                this.source = new Bursty(socket, clientID);
                start(clientID);
                break;
            case BurstyWithLeakyBucket:
                bucket = new Bucket(bucketSize,leakRate);
                this.source = new Bursty(this,clientID);
                start(clientID);
                break;
            case Close:
                stop(clientID);
                break;

        }


    }

    /**
     * Send data packets from leaky bucket.
     * @throws IOException
     */
    public void sendFromLeakyBucket() throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.write(bucket.leak());
    }
}
