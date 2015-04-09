package telecom.client.gui;

/**
 * Created by robertzhang on 2015-04-03.
 */

/**
 * Data receiver listener to provide callback to GUI.
 */
public interface DataReceiverListener {

    void onDataReceive(double dataLength);
    void onDataReceive(byte[] data);
    void onClientClosed();
}
