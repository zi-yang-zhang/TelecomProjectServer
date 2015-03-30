package telecom.server.protocol;

/**
 * Created by robertzhang on 2015-03-29.
 */
public class DecodeRequest {

    public static RequestType decode(Byte request){

        switch (request.intValue()){
            case 0:
                return RequestType.ConstantBitRate;
            case 1:
                return RequestType.ConstantBitRateWithLeakyBucket;
            case 2:
                return RequestType.Bursty;
            case 3:
                return RequestType.BurstyWithLeakyBucket;
            default:
                return RequestType.Close;
        }

    }
}
