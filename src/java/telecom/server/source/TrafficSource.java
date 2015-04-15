package telecom.server.source;

import java.io.IOException;

/**
 * Created by robertzhang on 2015-03-29.
 */

/**
 * Traffic source type with parameters define in protocol as a runnable.
 */
public  interface TrafficSource extends Runnable {
    byte[] CONSTANT_RATE_PACKET = new byte[800];
    byte[] BURSTY_PACKET = new byte[120000];
    long CONSTANT_RATE_TIME = 100;
    long BURSTY_RATE_TIME = 15000;
    long LEAKY_BUCKET_TIME = 100;

    /**
     * Sends data from traffic source.
     * @throws IOException
     */
    void send() throws IOException;

    /**
     * Terminate the traffic source thread.
     * @throws IOException
     */
    void terminate() throws IOException;
}
