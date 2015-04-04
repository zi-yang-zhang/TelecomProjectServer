package telecom.client.core;

import telecom.client.gui.DataReceiverListener;
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
    private double totalBytes;
    private DataReceiverListener dataReceiverListener;

    public void setServer(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }
    public void registerListener(DataReceiverListener dataReceiverListener){
        this.dataReceiverListener = dataReceiverListener;
    }
    public void setMode(RequestType requestType){
        this.requestType = requestType;
    }

    public void connect() throws IOException {
        totalBytes = 0;
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
        byte[] data = receiveSize();
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int byteRead = dataInputStream.read(data);
        totalBytes = totalBytes + byteRead;
        dataReceiverListener.onDataReceive(data);
        return  byteRead;
    }

    public void notifyListener(double time){
        dataReceiverListener.onDataReceive(totalBytes/time);

    }
    public double getTotalBytes(){return totalBytes;}
    public void disconnect() throws IOException {
        totalBytes = 0;
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write(RequestType.Close.getCommand());
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

    public void closeConnection() throws IOException {this.socket.close();}
    public boolean isConnected(){return socket.isConnected();}

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
