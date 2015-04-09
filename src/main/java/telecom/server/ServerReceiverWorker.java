package telecom.server;

import telecom.server.protocol.DecodeRequest;
import telecom.server.protocol.RequestType;
import telecom.server.source.CommandListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by robertzhang on 2015-03-30.
 */

/**
 * Receiver thread to receive command from client and calls back to the specific server thread that initiated it.
 */
public class ServerReceiverWorker implements Runnable {
    private Socket socket;
    private volatile boolean running;
    private CommandListener commandListener;
    public ServerReceiverWorker(Socket socket){
        this.socket = socket;
    }

    /**
     * Register the command listener to provide callback.
     * @param commandListener Command listener that reacts while command is received.
     */
    public void registerCommandListener(CommandListener commandListener){
        this.commandListener = commandListener;
    }

    /**
     * Listen and receive command from client, and calls back while command is received.
     */
    @Override
    public void run() {
        running = true;
        while (running){

            try {
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                RequestType requestType = DecodeRequest.decode(dataInputStream.readByte());
                commandListener.onCommandReceived(requestType);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void terminate(){running = false;}
}
