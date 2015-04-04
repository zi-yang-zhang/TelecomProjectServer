package telecom.server.source;

import telecom.server.protocol.RequestType;

import java.io.IOException;

/**
 * Created by robertzhang on 2015-03-30.
 */
public interface CommandListener {

    void onCommandReceived(RequestType type) throws IOException, InterruptedException;
}
