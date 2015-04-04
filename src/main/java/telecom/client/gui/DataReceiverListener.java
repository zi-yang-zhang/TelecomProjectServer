package telecom.client.gui;

/**
 * Created by robertzhang on 2015-04-03.
 */
public interface DataReceiverListener {

    void onDataReceive(double dataLength);
    void onDataReceive(byte[] data);
}
