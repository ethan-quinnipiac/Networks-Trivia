package project2;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class PlayerSendReceive {

    private static final String IP = "127.0.0.1"; // Localhost for testing
    private static final int UDP_PORT = 5000;
    private static final int TCP_PORT = 6000;
    private static final int clientID = 1;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Thread to receive messages using TCP
        executor.execute(() -> startTCPReceiver());

        // Thread to send messages using UDP
        executor.execute(() -> startUDPSender());

        executor.execute(() -> startGame());
    }


    private static void startGame(){
        ClientWindow window = new ClientWindow();
    }


    private static void startTCPReceiver() {
        try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
            System.out.println("TCP Receiver started. Listening on port " + TCP_PORT);
            Socket socket = serverSocket.accept();
            System.out.println("TCP Connection established.");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String received;
            while ((received = in.readLine()) != null) {
                System.out.println("TCP Received: " + received);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startUDPSender() {
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(IP);

            while (true) {
                String message = "hello " + clientID;
                byte[] buffer = message.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, UDP_PORT);
                udpSocket.send(packet);

                System.out.println("UDP Sent: " + message);
                Thread.sleep(1000); // send every second
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
