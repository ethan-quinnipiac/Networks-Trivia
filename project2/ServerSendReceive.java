package project2;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerSendReceive {
    private static final String selfIP = "127.0.0.1";
    private static final String goalIP = "127.0.0.1";
    private static final int UDP_PORT = 5000;
    private static final int TCP_PORT = 6000;
    private static final int[] playerIDs = {1, 2, 3, 4};
    //list of clients, add more if need more
    private static final String[] clientIPs = {
        "127.0.0.1",
    };

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();


    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        //Run both UDP receiver and TCP sender in parallel
        executor.execute(() -> startUDPReceiver());
        executor.execute(() -> startTCPSendersToClients());
    }

    private static void startUDPReceiver() {
        try (DatagramSocket udpSocket = new DatagramSocket(UDP_PORT)) {
            byte[] buffer = new byte[1024];
            System.out.println("UDP Receiver started on port " + UDP_PORT);
            boolean hasSent = false;

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                String[] segmented = received.split(" ");
                if(segmented[0].equals("buzz") && hasSent == false){
                    System.out.println("received from " + segmented[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startTCPSendersToClients() {
        for (String clientIP : clientIPs) {
            new Thread(() -> {
                while (true) {
                    try (Socket socket = new Socket(clientIP, TCP_PORT);
                         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                        System.out.println("Server: Connected to client TCP at " + clientIP + ":" + TCP_PORT);
                        
                        while (true) {
                            out.println("hello from server");
                            System.out.println("Server: TCP Sent to " + clientIP + ": hello from server");
                            Thread.sleep(1000);
                        }

                    } catch (Exception e) {
                        System.out.println("Server: Could not connect to " + clientIP + ", retrying in 3s...");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ignored) {}
                    }
                }
            }).start();
        }
    }
}
