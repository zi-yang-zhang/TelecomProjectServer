package telecom.client.gui;

import telecom.client.core.Client;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by robertzhang on 2015-04-03.
 */

/**
 * GUI receiver worker of client to receive data stream from server.
 */
public class ClientReceiverGuiWorker extends SwingWorker<Void,Void> {
    private Client client;
    private volatile boolean running;
    public ClientReceiverGuiWorker(Client client){this.client = client;}
    /**
     * Constantly receive data stream from the server.
     */
    @Override
    protected Void doInBackground() throws Exception {

        running = true;
        while (running){
            try {
                client.receiveMessage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        client.closeConnection();
        return  null;
    }
    public void terminate() throws IOException {
        client.disconnect();
        this.running = false;
    }
}
