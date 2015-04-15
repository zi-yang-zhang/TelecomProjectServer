package telecom.test;

import junit.framework.TestCase;
import org.junit.Test;
import telecom.server.leakyBucket.Bucket;
import telecom.server.source.TrafficSource;

public class BucketTest extends TestCase{

    /**
     * Test for fill with overflow
     * @throws Exception
     */
    @Test
    public void testFillForOverflow() throws Exception {
        Bucket bucket = new Bucket(10000, 10000);
        assertTrue(!bucket.fill(TrafficSource.BURSTY_PACKET));
    }

    /**
     * Test with fill with no overflow
     * @throws Exception
     */
    @Test
    public void testFillNotOverflow() throws Exception {
        Bucket bucket = new Bucket(10000, 10000);
        assertTrue(bucket.fill(TrafficSource.CONSTANT_RATE_PACKET));
        assertEquals("size not match:", bucket.size(), TrafficSource.CONSTANT_RATE_PACKET.length);
    }

    /**
     * Test with fill and discard packets
     * @throws Exception
     */
    @Test
    public void testFillDiscard() throws Exception {
        Bucket bucket = new Bucket(10000, 10000);
        assertFalse(bucket.fill(TrafficSource.BURSTY_PACKET));
        assertEquals("size not match:", bucket.size(), bucket.leak().length*10);
    }

    /**
     * Test with normal leak
     * @throws Exception
     */
    @Test
    public void testLeak() throws Exception {
        Bucket bucket = new Bucket(10000, 10000);
        bucket.fill(TrafficSource.BURSTY_PACKET);
        assertEquals(bucket.leak().length,10000/10);
    }

    /**
     * Test with leak till empty
     * @throws Exception
     */
    @Test
    public void testLeakEmpty() throws Exception {
        Bucket bucket = new Bucket(10000, 10000);
        bucket.fill(TrafficSource.CONSTANT_RATE_PACKET);
        bucket.leak();
        assertEquals(bucket.leak().length,0);
    }

    /**
     * Test with leak till empty with small capacity
     * @throws Exception
     */
    @Test
    public void testLeakEmptyWithSmallCapacity() throws Exception {
        Bucket bucket = new Bucket(10, 100);
        bucket.fill(new byte[10]);
        assertEquals(bucket.leak().length,10);
    }
}