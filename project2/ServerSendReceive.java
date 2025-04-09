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


/*
 * Coded by Kyle Macdonald
 * Prerequisites: IPs set for the clients the server is set to connect to
 * Output: A server that manages correct answers, polling, and more between players.
 */

public class ServerSendReceive {
    private static final String selfIP = "127.0.0.1";
    private static final String goalIP = "127.0.0.1";
    private static final int UDP_PORT = 5000;
    private static final int TCP_PORT = 6000;
    private static final int[] playerIDs = {1, 2, 3, 4};
    private static final int playerCount = playerIDs.length;
    //list of clients, add more if need more
    private static final String[] clientIPs = {
        "127.0.0.1",
    };

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    private static int rightAnswer = 1;
    private static int questionCount = 2;
    private static int questionMax = 21;
    private static int[] scores = {1, 0, 1, 1};
    private static boolean inProgress = true;
    private static Question[] questions = QuestionMaker.makeQuestions();

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
                    queue.offer("ack " + segmented[1]);
                    hasSent = true;
                }else if(segmented[0].equals("buzz") && hasSent == true){
                    queue.offer("negative-ack " + segmented[1]);
                }else if(segmented[0].equals("answer")){
                    System.out.println("received answer");
                    if(segmented[2].equals(Integer.toString(rightAnswer))){
                        queue.offer("correct " + segmented[1]);
                        queue.offer("next");
                        hasSent = false;
                    }else{
                        queue.offer("wrong " + segmented[1]);
                        queue.offer("next");
                        hasSent = false;
                    }
                }else if(segmented[0].equals("score")){
                    scores[Integer.valueOf(segmented[1])-1] = Integer.valueOf(segmented[2]);
                    System.out.println("received score of player " + segmented[1]);
                    for(int i = 0; i < playerCount; i++){
                        if(scores[i] == 0){
                            break;
                        }

                        if(i == playerCount - 1){
                            queue.offer("total");
                        }
                    }
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

                        System.out.println("Connected to client " + clientIP);
                        if(inProgress){
                            out.println("catchup " + 2 + " " + questionCount);
                        }
                        
                        while (true) {
                            Thread.sleep(50);

                            if(queue.isEmpty() == false){
                                String step = queue.poll();
                                System.out.println("trying to send");
                                String[] stepSplit = step.split(" ");
                                if(stepSplit[0].equals("ack")){
                                    out.println("ack " + step.split(" ")[1]);
                                    System.out.println("sent out ack");
                                }else if(stepSplit[0].equals("negative-ack")){
                                    out.println("negative-ack " + step.split(" ")[1]);
                                    System.out.println("sent out negative-ack");
                                }else if(stepSplit[0].equals("correct")){
                                    out.println("correct " + stepSplit[1]);
                                    System.out.println("sent out right");
                                }else if(stepSplit[0].equals("wrong")){
                                    out.println("wrong " + stepSplit[1]);
                                    System.out.println("sent out wrong");
                                }else if(stepSplit[0].equals("next")){
                                    out.println("next " + (questionCount + 1));
                                    questionCount += 1;
                                    rightAnswer = questions[questionCount].getCorrectOptionIndex();
                                }else if(stepSplit[0].equals("total")){
                                    String toSend = "total";
                                    for(int i = 0; i < playerCount; i++){
                                        toSend += (" " + scores[i]);
                                    }
                                    out.println(toSend);
                                }
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("Could not connect to " + clientIP + ", retrying in 3s");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ignored) {}
                    }
                }
            }).start();
        }
    }
}
