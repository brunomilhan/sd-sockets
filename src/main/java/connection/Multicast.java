package connection;

import model.Message;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Bruno on 07/09/2016.
 */
public class Multicast {
    private static final String DEFAULT_GROUP_ADDR = "239.0.0.1";
    private static final int DEFAULT_PORT = 4000;
    private MulticastSocket multicastSocket = null;
    private InetAddress groupAddr = null;
    private int clientPort;

    private Thread threadListener = null;

    public Multicast(int clientPort) {
        this.clientPort = clientPort;
        initMulticast();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initMulticast() {
        try {
            groupAddr = InetAddress.getByName(DEFAULT_GROUP_ADDR);
            multicastSocket = new MulticastSocket(clientPort);
            multicastSocket.joinGroup(groupAddr);
            System.out.println("Socket init with success!");
        } catch (SocketException e) {
            System.out.println("Socket init: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String request(Message message) {

        // Mount message body

        DatagramPacket request = new DatagramPacket(message.getBody(), message.getLength(), groupAddr, clientPort);
        try {
            multicastSocket.send(request);
            return "OK";
        } catch (SocketException e) {
            System.out.println("Socket request: " + e.getMessage());
            return "SocketException";
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            return "IOException";
        }
    }

    public void listenHandler(ResHandlerInterface resHandler) {
        threadListener = new Thread(new Listen(resHandler));
        threadListener.start();
    }

   /* public void changeListenerHandler(ResHandlerInterface handler) {
        threadListener.interrupt();
        threadListener = new Thread(new Listen(handler));
        threadListener.start();
    }*/

    private class Listen implements Runnable {
        ResHandlerInterface resHandlerInterface;

        public Listen(ResHandlerInterface resHandler) {
            this.resHandlerInterface = resHandler;
        }

        public void run() {
            Message message = new Message();
            try {
                while (true) {
                    DatagramPacket response = new DatagramPacket(message.getBody(), message.getLength());
                    multicastSocket.receive(response);

                    Message message1 = new Message(response.getData());

                    // debug
                    //if (!message1.getType().equals(Message.KEEPALIVE))
                    //System.out.println("\nReceived:" + message1 + " \n");

                    resHandlerInterface.handler(message1);
                }
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
                message.setResStatus("SocketException");
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
                message.setResStatus("IOException");
            }
        }
    }
}
