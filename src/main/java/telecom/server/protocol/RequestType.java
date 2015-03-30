package telecom.server.protocol;

/**
 * Created by robertzhang on 2015-03-29.
 */
public enum RequestType {
    BurstyWithLeakyBucket(3),Bursty(2),ConstantBitRateWithLeakyBucket(1),ConstantBitRate(0), Close(4);


    private int command;
    RequestType(int command){
        this.command = command;
    }

    public int getCommand(){
        return command;
    }

}
