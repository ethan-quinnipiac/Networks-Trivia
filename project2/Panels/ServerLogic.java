package project2.Panels;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import project2.Question;
import project2.QuestionMaker;

/**
 * John Caceres
 * 
 * Modified off of Kyle's work to match panel format.
 */
public class ServerLogic {
    private static final int UDP_PORT = 5000;
    private ServerPanel serverPanel;
    private int playerCount;
    private ArrayList<String> clientIDs;
    private HashMap<String, Socket> clientSockets;

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    
    private static int questionCount = 1;
    private static int[] scores = {1, 0, 1, 1};
    private static boolean inProgress = true;
    private static Question[] questions = QuestionMaker.makeQuestions();
    private static int rightAnswer = questions[0].getCorrectOptionIndex() + 1;

    public ServerLogic(ServerPanel serverPanel) {
        this.serverPanel = serverPanel;
        this.clientIDs = new ArrayList<>(this.serverPanel.getClientIDs());
        this.clientSockets = new HashMap<>(this.serverPanel.getClientSockets());
        this.playerCount = this.clientIDs.size();
    }

    private void startUDPReceiver() {
        try (DatagramSocket udpSocket = new DatagramSocket(UDP_PORT)) {
            byte[] buffer = new byte[1024];
            System.out.println("UDP Receiver started on port " + UDP_PORT);
            boolean hasSent = false;

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                String[] segmented = received.split(" ");
                //Checks for received messages from all clients. Every client message is formatted with "command" "clientID"
                if(segmented[0].equals("buzz") && hasSent == false){
                    System.out.println("received from " + segmented[1]);
                    queue.offer("ack " + segmented[1]);
                    for (String clientID : clientIDs) {
                        if (!clientID.equals(segmented[1])) {
                            queue.offer("wait" + clientID);
                        }
                    }
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
                    for(int i = 0; i < this.playerCount; i++){
                        if(scores[i] == 0){
                            break;
                        }

                        if(i == this.playerCount - 1){
                            queue.offer("total");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTCPSendersToClients() {
        for (String clientID : clientIDs) {
            new Thread(() -> {
                while (true) {
                    try (PrintWriter out = new PrintWriter(clientSockets.get(clientID).getOutputStream(), true)) {
                        
                        //Catches client up to most recent question if they're joining mid game
                        if(inProgress){
                            out.println("catchup " + 2 + " " + questionCount);
                        }
                        
                        while (true) {
                            Thread.sleep(50);

                            //checks if the queue is empty, then performs the prerequisite step based on what's at the head
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
                                    rightAnswer = questions[questionCount].getCorrectOptionIndex() + 1;
                                }else if(stepSplit[0].equals("total")){
                                    String toSend = "total";
                                    for(int i = 0; i < playerCount; i++){
                                        toSend += (" " + scores[i]);
                                    }
                                    out.println(toSend);
                                } else if (stepSplit[0].equals("wait")) {
                                    out.println("wait" + stepSplit[1]);
                                }
                            }
                        }

                    } catch (Exception e) {
                        //Tries reconnecting if no client is found, tries when client disconnects too
                        System.out.println("Could not connect to " + clientID + ", retrying in 3s");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ignored) {}
                    }
                }
            }).start();
        }
    }

    public void startGameLogic() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        //Run both UDP receiver and TCP sender in parallel
        executor.execute(() -> startUDPReceiver());
        executor.execute(() -> startTCPSendersToClients());
    }
}
