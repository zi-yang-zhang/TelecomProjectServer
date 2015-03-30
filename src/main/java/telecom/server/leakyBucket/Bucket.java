package telecom.server.leakyBucket;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by robertzhang on 2015-03-29.
 */
public class Bucket extends LinkedList<Byte> {
    private int bucketSize;
    private int leakRate;


    public Bucket(int size, int leakRate){
        this.bucketSize = size;
        this.leakRate = leakRate;
        System.out.println("Created bucket of size: "+ size);

    }


    public boolean fill(byte[] packet){
        System.out.print("\r" + "Bucket capacity :" + ((double) size() / (double) bucketSize) * 100 + "%");
        for(byte p: packet){
            if(size() == bucketSize){
                System.out.println("\n"+"Bucket full, discard further packets");
                return false;
            }
            add(p);
        }
        return true;

    }

    public byte[] leak(){
        //System.out.print("Bucket leaking");
        byte[] packet = new byte[leakRate];
        for(int i = 0; i < leakRate;i++){
            try {
                packet[i] = this.pop();
            } catch (NoSuchElementException e) {
                //System.out.println("Bucket empty, leaked " + i + " bytes");
                return packet;
            }
        }
        //System.out.print("\r" + "Leaked: " + leakRate + " bytes");
        return packet;

    }



}
