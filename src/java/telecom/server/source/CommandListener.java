package telecom.server.source;

import telecom.protocol.RequestType;

import java.io.IOException;

/**
 * Created by robertzhang on 2015-03-30.
 */

/**
 * Command listener where react while command is received.
 */
public interface CommandListener {
    /**
     * Called when a command is received.
     * @param type Request type received.
     * @param clientID
     * @throws IOException
     * @throws InterruptedException
     */
    void onCommandReceived(RequestType type, int clientID) throws IOException, InterruptedException;
}
