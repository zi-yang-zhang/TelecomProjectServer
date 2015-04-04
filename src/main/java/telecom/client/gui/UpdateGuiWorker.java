package telecom.client.gui;

import telecom.client.core.Client;

import javax.swing.*;

/**
 * Created by robertzhang on 2015-04-03.
 */
public class UpdateGuiWorker extends SwingWorker<Void,Void> {

    private Client client;
    private volatile boolean running;
    public UpdateGuiWorker(Client client){this.client = client;}
    @Override
    protected Void doInBackground() throws Exception {
        running = true;
        long time = System.currentTimeMillis();

        while(running){
            long timedif = System.currentTimeMillis() - time;
            if(timedif>1000){
                client.notifyListener(timedif/1000);
            }
        }

        return null;
    }

    public void terminate(){this.running = false;}
}
