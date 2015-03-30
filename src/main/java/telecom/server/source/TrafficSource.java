package telecom.server.source;

import java.io.IOException;

/**
 * Created by robertzhang on 2015-03-29.
 */
public  interface TrafficSource {
    static final byte[] CONSTANT_RATE_PACKET = new byte[800];
    static final byte[] BURSTY_PACKET = new byte[120000];
    static final long CONSTANT_RATE_TIME = 100;
    static final long BURSTY_RATE_TIME = 15000;
    static final long LEAKY_BUCKET_TIME = 1000;
    abstract void send() throws IOException;
    abstract void init();
    abstract void terminate();
}
