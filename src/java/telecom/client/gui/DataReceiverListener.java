package telecom.client.gui;

/**
 * Created by robertzhang on 2015-04-03.
 */

/**
 * Data receiver listener to provide callback to GUI.
 */
public interface DataReceiverListener {
    /**
     * Callback function when received data from server
     * @param dataLength
     */
    void onDataReceive(double dataLength);

    /**
     * Callback function when client closes the connection
     */
    void onClientClosed();
}
