package connection;

import model.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Multicast() {
        this.clientPort = DEFAULT_PORT;
        initMulticast();
    }

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

    /*public String request(Message message) {
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
        } finally {
            if (multicastSocket != null)
                multicastSocket.close();
        }
    }*/

   /* public String response() {
        Message message = new Message();
        try {
            while (true) {
                DatagramPacket response = new DatagramPacket(message.getBody(), message.getLength());
                multicastSocket.receive(response);
                System.out.println("Received:" + new String(response.getData()));
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
            return "SocketException";
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            return "IOException";
        } finally {
            if (multicastSocket != null)
                multicastSocket.close();
            return "OK";
        }
    }*/

   /* public Message listen() {
        final Message message = new Message();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("teste");
                    while (true) {
                        DatagramPacket response = new DatagramPacket(message.getBody(), message.getLength());
                        multicastSocket.receive(response);
                        System.out.println("Received:" + new String(response.getData()));
                        message.setBody(response.getData());

                        //aqui é onde tenho que montar a mensagem novamente

                        message.setResStatus("OK");
                    }
                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                    message.setResStatus("SocketException");
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                    message.setResStatus("IOException");
                }
            }
        });
        thread.run();
        return message;
    }

    // com executors
    //
    public Message listen1() {
        final Message message = new Message();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<String> futureResult = executor.submit(new Callable<String>() {
            public String call() throws Exception {
                try {
                    System.out.println("teste");
                    //while (true) {
                        DatagramPacket response = new DatagramPacket(message.getBody(), message.getLength());
                        multicastSocket.receive(response);
                        System.out.println("Received:" + new String(response.getData()));
                        message.setBody(response.getData());
                        message.setResStatus("OK");
                        return new String(response.getData());
                    //}
                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                    message.setResStatus("SocketException");
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                    message.setResStatus("IOException");
                }
                return null;
            }
        });

        //Obtendo um resultado da execucão da Thread
        try {
            System.out.println("fubcibou> "+futureResult.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return message;
    }*/

    /*public void listenHandler(ResHandlerInterface resHandler) {
        final ResHandlerInterface resHandlerInterface = resHandler;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Message message = new Message();
                try {
                    while (true) {
                        DatagramPacket response = new DatagramPacket(message.getBody(), message.getLength());
                        multicastSocket.receive(response);
                        System.out.println("Received:" + new String(response.getData()));

                        //aqui é onde tenho que montar a mensagem novamente
                        Message message1 = new Message(response.getData());

                        //message.setBody(response.getData());
                        //message.setResStatus("OK");
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
        });
        thread.run();
    }*/

    public void listenHandler(ResHandlerInterface resHandler) {
        threadListener = new Thread(new Listen(resHandler));
        threadListener.start();
    }

    public void changeListenerHandler(ResHandlerInterface handler){
        threadListener.interrupt();
        threadListener = new Thread(new Listen(handler));
        threadListener.start();
    }

    private class Listen implements Runnable{
        ResHandlerInterface resHandlerInterface;

        public Listen(ResHandlerInterface resHandler){
            this.resHandlerInterface = resHandler;
        }
        public void run() {
            Message message = new Message();
            try {
                while (true) {
                    DatagramPacket response = new DatagramPacket(message.getBody(), message.getLength());
                    multicastSocket.receive(response);
                    //System.out.println("\n:::::::::::::: Received:" + new String(response.getData()) + " \n");

                    Message message1 = new Message(response.getData());

                    //message.setBody(response.getData());
                    //message.setResStatus("OK");
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


    public String leaveGroup() {
        try {
            multicastSocket.leaveGroup(groupAddr);
            return "OK";
        } catch (SocketException e) {
            System.out.println("leaveGroup Socket: " + e.getMessage());
            return "SocketException";
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            return "IOException";
        } finally {
            if (multicastSocket != null)
                multicastSocket.close();
        }
    }
}
