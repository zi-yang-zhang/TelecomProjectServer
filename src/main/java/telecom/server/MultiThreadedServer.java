package telecom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by robertzhang on 2015-03-17.
 */

/**
 * Main server class which listens to the port and initiates new worker threads to send data to client.
 */
public class MultiThreadedServer{
    private static final int PORT = 5000;
    public MultiThreadedServer(Socket socket, int bucketSize, int leakRate) throws IOException {
        new LeakyBucketSocket(socket,bucketSize,leakRate);

    }


    /**
     * Listens to a port number and initiates new leaky bucket socket worker thread.
     * @param args command line arguments: args[0] bucket size, args[1] leak rate.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Listening");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Connected");
            try {
                new MultiThreadedServer(socket,Integer.valueOf(args[0]),Integer.valueOf(args[1]));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
                System.out.println("Please specify bucket size and leak rate");
                System.exit(1);
            }

        }
    }
}
