package telecom.client.core;

import java.io.IOException;

/**
 * Created by robertzhang on 2015-03-30.
 */
public class ClientReceiverWorker implements Runnable{

    private Client client;
    private boolean running;
    public ClientReceiverWorker(Client client){
        this.client = client;
    }
    @Override
    public void run() {
        running = true;
        long time = System.currentTimeMillis();
        int rate = 0;
        while (running){
            try {
                long timedif = System.currentTimeMillis() - time;
                if(timedif>1000){
                    System.out.println(rate + "bytes/s");
                    rate = 0;
                    time = System.currentTimeMillis();
                }
                rate = rate +client.receiveMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void terminate(){
        this.running = false;
    }
}
