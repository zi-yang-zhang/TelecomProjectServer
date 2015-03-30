package telecom.client.core;

import telecom.server.protocol.RequestType;

import java.io.*;
import java.net.Socket;

/**
 * Created by robertzhang on 2015-03-30.
 */
public class Client {
    private String hostname;
    private int port;
    private Socket socket;
    private RequestType requestType;

    public void setServer(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    public void setMode(RequestType requestType){
        this.requestType = requestType;
    }

    public void connect() throws IOException {
        System.out.println("Attempting to connect to "+hostname+":"+port);
        socket = new Socket(hostname,port);
        System.out.println("Connection Established");
    }

    public void sendCommand() throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write(requestType.getCommand());
    }
    public int receiveMessage() throws IOException {
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        dataInputStream.readFully(receiveSize());
        return  receiveSize().length;
    }

    private byte[] receiveSize(){
        switch (requestType){
            case ConstantBitRate:
                return new byte[800];
            case ConstantBitRateWithLeakyBucket:
                return new byte[100];
            case Bursty:
                return new byte[120000];
            case BurstyWithLeakyBucket:
                return new byte[100];

        }
        return new byte[1];
    }


    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.setServer("localhost",5000);
        client.setMode(RequestType.BurstyWithLeakyBucket);
        client.connect();

        ClientReceiverWorker clientReceiverWorker = new ClientReceiverWorker(client);
        new Thread(clientReceiverWorker).start();
        client.sendCommand();
    }

}
