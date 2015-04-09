package telecom.client.core;

import telecom.client.gui.DataReceiverListener;
import telecom.server.protocol.RequestType;

import java.io.*;
import java.net.Socket;

/**
 * Created by robertzhang on 2015-03-30.
 */

/**
 * Client side implementation to provide connection to the server and notify gui when data is received.
 */
public class Client {
    private String hostname;
    private int port;
    private Socket socket;
    private RequestType requestType;
    private double totalBytes;
    private DataReceiverListener dataReceiverListener;

    /**
     * Set server parameters about the server to connect to.
     * @param hostname Hostname of the server.
     * @param port Port number of the server.
     */
    public void setServer(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Register dataReceiverListener to provide callbacks to GUI.
     * @param dataReceiverListener
     */
    public void registerListener(DataReceiverListener dataReceiverListener){
        this.dataReceiverListener = dataReceiverListener;
    }

    /**
     * Set Request type mode when to initiate connection with specific request type.
     * @param requestType
     */
    public void setMode(RequestType requestType){
        this.requestType = requestType;
    }

    /**
     * Connect to server.
     * @throws IOException
     */
    public void connect() throws IOException {
        totalBytes = 0;
        System.out.println("Attempting to connect to "+hostname+":"+port);
        socket = new Socket(hostname,port);
        System.out.println("Connection Established");
    }

    /**
     * Send Specific request type command to the server.
     * @throws IOException
     */
    public void sendCommand() throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write(requestType.getCommand());
    }

    /**
     * Receive message from the server and notify the listener to update connection metrics.
     * @return Data packet length received from the server.
     * @throws IOException
     */
    public int receiveMessage() throws IOException {
        byte[] data = receiveSize();
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int byteRead = dataInputStream.read(data);
        if(byteRead == -1){
            dataReceiverListener.onClientClosed();
        }
        totalBytes = totalBytes + byteRead;
        dataReceiverListener.onDataReceive(data);
        return  byteRead;
    }

    /**
     * Notify the GUI to update metrics.
     * @param time
     */
    public void notifyListener(double time){
        dataReceiverListener.onDataReceive(totalBytes/time);

    }
    public double getTotalBytes(){return totalBytes;}

    /**
     * Disconnect from the server, send close command to the server.
     * @throws IOException
     */
    public void disconnect() throws IOException {
        totalBytes = 0;
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write(RequestType.Close.getCommand());
    }

    /**
     *
     * @return Byte array with size defined in specific request type.
     */
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

    /**
     * Close socket connection.
     * @throws IOException
     */
    public void closeConnection() throws IOException {this.socket.close();}
    public boolean isConnected(){return socket.isConnected();}

    /**
     * Command line version of client side implementation.
     * @param args
     * @throws IOException
     */
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
