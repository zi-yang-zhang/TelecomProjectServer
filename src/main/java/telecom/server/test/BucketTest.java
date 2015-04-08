package telecom.server.test;

import telecom.server.leakyBucket.Bucket;
import telecom.server.source.TrafficSource;

import static org.junit.Assert.*;

public class BucketTest {

    int constLeak = 7500;
    int constSize = 10000;
//    int burstyLeak = 7000;
//    int burstySize = 150000;
    Bucket bucket;
    @org.junit.Before
    public void setUp() throws Exception {

        bucket = new Bucket(constSize, constLeak);

    }

    @org.junit.After
    public void tearDown() throws Exception {
        // empty the bucket
        while(bucket.size()>0){
            bucket.leak();
        }

    }

    @org.junit.Test
    public void testFill() throws Exception {
        // fill constant packet
        System.out.println("Fill: " + TrafficSource.CONSTANT_RATE_PACKET.length);
        //check if it fills
        assertTrue(bucket.fill(TrafficSource.CONSTANT_RATE_PACKET));
        // Expect the bucket has constant packet size
        System.out.println("Filled: " + bucket.size());
        assertEquals("size not match:", bucket.size(), TrafficSource.CONSTANT_RATE_PACKET.length);
        // Fill bursty packet, which is larger than the total bucket size
        System.out.println("Fill: " + TrafficSource.BURSTY_PACKET.length);
        // discard overflow
        assertTrue(!bucket.fill(TrafficSource.BURSTY_PACKET));
        System.out.println("Filled: " + bucket.size());

    }

    @org.junit.Test
    public void testLeak() throws Exception {
        // fill the bucket fully
        System.out.println("Fill: " + TrafficSource.BURSTY_PACKET.length);
        assertTrue(!bucket.fill(TrafficSource.BURSTY_PACKET));
        System.out.println("Filled: " + bucket.size());
        // leak once
        bucket.leak();
        System.out.println("bucket size: " + bucket.getBucketSize());
        // check if the difference is the same as the set leak value
        int leak = 10*(bucket.getBucketSize()-bucket.size());
        System.out.println("leak: " + leak );
        assertEquals(leak, constLeak);
    }


}