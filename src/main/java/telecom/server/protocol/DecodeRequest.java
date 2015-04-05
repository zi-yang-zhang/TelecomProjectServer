package telecom.server.protocol;

/**
 * Created by robertzhang on 2015-03-29.
 */

/**
 * Request Type decoder to decode request from client
 */
public class DecodeRequest {
    /**
     * Decode the command received from client in byte into RequestType.
     * @param request Request packet from Client.
     * @return The specific RequestType represented by the request packet.
     */
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
            case 4:
                return RequestType.Close;
            default:
                return RequestType.Close;
        }

    }
}
