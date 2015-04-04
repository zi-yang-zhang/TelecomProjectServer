package telecom.server.source;

import telecom.server.LeakyBucketSocket;

import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;

/**
 * Created by robertzhang on 2015-04-03.
 */
public abstract class AbstractServerThread implements TrafficSource{
    protected Socket socket;
    protected volatile boolean running;
    protected long timeType;

    protected void setTimeType(long timeType){this.timeType = timeType;}
    @Override
    public void run() {
        running = true;
        long sendTime = System.currentTimeMillis();
        long leakTime = System.currentTimeMillis();
        long refreshTime = System.currentTimeMillis();
        try {
            send();
            if(socket instanceof LeakyBucketSocket){
                System.out.println("Bucket capacity :");
                ((LeakyBucketSocket) socket).sendFromLeakyBucket();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (running){
            long sendTimeDif = System.currentTimeMillis() - sendTime;
            long leakyTimeDif  = System.currentTimeMillis() - leakTime;
            long refreshTimeDif = System.currentTimeMillis() - refreshTime;

            if(sendTimeDif > timeType){
                try {
                    send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendTime = System.currentTimeMillis();
            }

            if(socket instanceof LeakyBucketSocket ){
                if(leakyTimeDif > LEAKY_BUCKET_TIME){
                    try {
                        ((LeakyBucketSocket) socket).sendFromLeakyBucket();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    leakTime = System.currentTimeMillis();
                }
                if(refreshTimeDif > 1000){
                    printBucketCapacity();
                    refreshTime = System.currentTimeMillis();
                }

            }

        }
        System.out.println("Connection closed");
    }


    public void terminate() throws IOException {
        this.running = false;
        socket.close();
    }

    public void printBucketCapacity(){
        double percentageCapacity = ((double) ((LeakyBucketSocket) socket).getBucket().size()
                / (double) ((LeakyBucketSocket) socket).getBucket().getBucketSize());
        updateProgress(percentageCapacity);
    }

    private void updateProgress(double progressPercentage) {
        final int width = 50; // progress bar width in chars

        System.out.print("\r[");
        int i = 0;
        for (; i <= (int)(progressPercentage*width); i++) {
            System.out.print("=");
        }
        for (; i < width; i++) {
            System.out.print(" ");
        }

        DecimalFormat df = new DecimalFormat("#.00");
        System.out.print("]"+df.format(progressPercentage*100)+"%");
    }
}
