package telecom.client.gui;

import telecom.client.core.Client;

import javax.swing.*;

/**
 * Created by robertzhang on 2015-04-03.
 */

/**
 * GUI thread worker to update GUI.
 */
public class UpdateGuiWorker extends SwingWorker<Void,Void> {

    private Client client;
    private volatile boolean running;
    public UpdateGuiWorker(Client client){this.client = client;}

    /**
     * Update GUI with receive rate metrics every second.
     * @return
     * @throws Exception
     */
    @Override
    protected Void doInBackground() throws Exception {
        running = true;
        long start = System.currentTimeMillis();
        long time = start;
        client.notifyListener(0);

        while(running){
            long timedif = System.currentTimeMillis() - time;
            long tottimedif = System.currentTimeMillis()-start;
            if(timedif> 1000){
                client.notifyListener(tottimedif/1000);
                time = System.currentTimeMillis();
            }
        }

        return null;
    }

    public void terminate(){this.running = false;}
}
