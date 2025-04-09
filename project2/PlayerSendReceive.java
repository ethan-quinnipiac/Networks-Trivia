package project2;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class PlayerSendReceive {

    private static final String IP = "127.0.0.1"; // Localhost for testing
    private static final int UDP_PORT = 5000;
    private static final int TCP_PORT = 6000;
    private static final int clientID = 2;

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    private static boolean answering = false;
    private static int finalAnswer = -1234;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Thread to receive messages using TCP
        executor.execute(() -> startTCPReceiver());

        // Thread to send messages using UDP
        executor.execute(() -> startUDPSender());

        executor.execute(() -> startGame());
    }


    private static void startGame(){
        List<Question> questionPool = Arrays.asList(
            new Question("What is the capital of France?", Arrays.asList("Berlin", "Madrid", "Paris", "Rome"), 2),
            new Question("What is 2 + 2?", Arrays.asList("3", "4", "5", "6"), 1)
        );

        // Create the ClientWindow
        ClientWindow clientWindow = new ClientWindow();

        // Create the Game instance
        Game game = new Game(questionPool, clientWindow);

        // Start the game
        try{
            while (true){
            finalAnswer = clientWindow.finalAnswer;
            Thread.sleep(1000);
            if(clientWindow.finalAnswer == 0){
                queue.add("poll");
                clientWindow.finalAnswer = -1;
            }
        }
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        game.startGame();
        
    }

    //assumes that all messages received are in the format of "command" "clientID"
    private static void startTCPReceiver() {
        try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
            System.out.println("TCP Receiver started. Listening on port " + TCP_PORT);
            Socket socket = serverSocket.accept();
            System.out.println("TCP Connection established.");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String received;
            while ((received = in.readLine()) != null) {
                String[] receivedArr = received.split(" ");
                if(receivedArr[1].equals(Integer.toString(clientID))){
                    System.out.println("TCP Received: " + received);
                    if(receivedArr[0].equals("ack")){
                        answering = true;
                    }else if(receivedArr[0].equals("negative-ack")){
                        System.out.println("negative-ack");
                    }else if(receivedArr[0].equals("correct")){
                        System.out.println("correct! +10");
                        answering = false;
                        
                    }else if(receivedArr[0].equals("wrong")){
                        System.out.println("wrong! -10");
                        answering = false;
                    }
                }
                if(receivedArr[0].equals("next")){

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startUDPSender() {
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(IP);

            while (true) {
                if(queue.isEmpty() == false){
                    String command = queue.poll();
                    if(command.equals("poll")){
                        String message = "buzz " + clientID;
                        byte[] buffer = message.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, UDP_PORT);
                        udpSocket.send(packet);
                        System.out.println("UDP Sent: " + message);
                    }else{
                        System.out.println("Unrecognized command");
                    }
                    
                }
                if(answering && finalAnswer > 0 && finalAnswer < 5){
                    String message = "answer " + clientID + " " + finalAnswer;
                    byte[] buffer = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, UDP_PORT);
                    udpSocket.send(packet);
                    System.out.println("UDP Sent: " + message);
                    answering = false;
                }

                
                Thread.sleep(50); // send every second
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
