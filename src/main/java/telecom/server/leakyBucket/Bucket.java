package telecom.server.leakyBucket;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by robertzhang on 2015-03-29.
 */

/**
 * Implement Bucket data structure using LinkedList of Bytes,
 * represented as a Queue of Bytes where fills from end of queue and leaks from the head of queue(FIFO).
 */
public class Bucket extends LinkedList<Byte> {
    private int bucketSize;
    private int leakRate;

    /**
     * Initialize the bucket with bucket size and leak rate.
     * @param size Size of the bucket in Byte.
     * @param leakRate Rate at which this bucket leaks, in Bytes/s.
     */
    public Bucket(int size, int leakRate){
        this.bucketSize = size;
        this.leakRate = leakRate;
        System.out.println("Created bucket of size: "+ size);
    }

    /**
     * Fills the bucket with a data packet, and will discard additional packet if bucket is full during filling.
     * @param packet Data packet to be added to the bucket.
     * @return true if all packet is added to the bucket, false otherwise.
     */
    public boolean fill(byte[] packet){
        for(byte p: packet){
            if(size() == bucketSize){
                System.out.println("\n"+"Bucket full, discard further packets");

                return false;
            }
            add(p);
        }
        return true;
    }

    /**
     * Leaks the data inside the bucket at 1/10 of constant rate defined as leakRate when the bucket is created.
     * @return data packet leaked from the bucket.
     */
    public byte[] leak(){
        byte[] packet = new byte[leakRate/10];
        for(int i = 0; i < leakRate/10;i++){
            try {
                packet[i] = this.pop();
            } catch (NoSuchElementException e) {
                System.out.println("Bucket empty");
                return packet;
            }
        }
        return packet;
    }


    public int getBucketSize(){return bucketSize;}



}
