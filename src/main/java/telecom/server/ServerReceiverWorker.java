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
public class ServerReceiverWorker implements Runnable {
    private Socket socket;
    private boolean running;
    private CommandListener commandListener;
    public ServerReceiverWorker(Socket socket){
        this.socket = socket;
    }


    public void registerCommandListener(CommandListener commandListener){
        this.commandListener = commandListener;
    }
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
