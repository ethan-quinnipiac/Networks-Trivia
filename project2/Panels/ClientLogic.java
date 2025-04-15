package project2.Panels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import project2.Panels.GamePanel.TimerCode;
import project2.Player;
import project2.Question;
import project2.QuestionMaker;

/**
 * John Caceres
 * 
 * Modified off of Kyle's work to match game logic client-side.
 */

public class ClientLogic {
    private GamePanel gamePanel;
    
    private static final int UDP_PORT = 5000;
    private String clientID;
    private Socket clientSocket;
    private Player player;

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    private static boolean answering = false;
    private static int finalAnswer = -1234;
    private static Question[] questions = QuestionMaker.makeQuestions();

    public ClientLogic(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void startGameLogic(String clientID, Socket clientSocket) {
        this.clientID = clientID;
        this.player = new Player(clientID);
        this.clientSocket = clientSocket;
        ExecutorService executor = Executors.newFixedThreadPool(3);
        //Thread to receive messages using TCP
        executor.execute(() -> startTCPReceiver());
        //Thread to send messages using UDP
        executor.execute(() -> startUDPSender());
        //Thread starts the game
        executor.execute(() -> startGame());
    }

    private void startGame(){
        List<Question> questionPool = Arrays.asList(questions);

        // Create the Game instance
        PanelGame panelGame = new PanelGame(questionPool, this.gamePanel);
        panelGame.addPlayer(player);
        panelGame.nextQuestion();
        // Start the game
        try{
            while (true){
            gamePanel.submit.setEnabled(answering);
            gamePanel.score.setText("SCORE: " + player.getScore());
            finalAnswer = GamePanel.finalAnswer;
            Thread.sleep(1000);
            if(answering && !panelGame.isAnsweringActive){
                panelGame.startAnsweringTimer(player);
            }
            if(GamePanel.finalAnswer == 0){
                queue.add("poll");
                GamePanel.finalAnswer = -1;
            }
            if(queue.peek() == "next"){ //checks to see if next question is necessary
                queue.poll();
                panelGame.nextQuestion();
            }else if(queue.peek() != null){ //skips x amount of questions based on game in progress
                if(queue.peek().split(" ")[0].equals("setquestion")){
                    int questionCount = Integer.parseInt(queue.poll().split(" ")[1]) - 1;
                    System.out.println("skipping questions...");
                    for(int i = 0; i < questionCount; i++){
                        panelGame.nextQuestion();
                    }
                } else if (queue.peek().equals("wait")) {
                    queue.poll();
                    ((TimerCode) gamePanel.clock).setTime(99);
                }
            }

            if(PanelGame.isOver){
                queue.add("score " + clientID + " " + player.getScore());
                Thread.sleep(9000);
            }
        }
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        panelGame.startGame();
        
    }

    //assumes that all messages received are in the format of "command" "clientID"
    private void startTCPReceiver() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String received;
            while ((received = in.readLine()) != null) {
                String[] receivedArr = received.split(" ");
                if(receivedArr.length != 1 || receivedArr[0].equals("total")){
                if(receivedArr[1].equals(clientID) || receivedArr[0].equals("total")){
                    System.out.println("TCP Received: " + received);
                    if(receivedArr[0].equals("ack")){
                        answering = true;
                    }else if(receivedArr[0].equals("negative-ack")){
                        System.out.println("negative-ack");
                    }else if(receivedArr[0].equals("correct")){
                        System.out.println("correct! +10");
                        player.updateScore(10);
                        answering = false;
                        
                    }else if(receivedArr[0].equals("wrong")){
                        System.out.println("wrong! -10");
                        player.updateScore(-10);
                        answering = false;
                    }else if(receivedArr[0].equals("catchup")){
                        queue.offer("setquestion " + receivedArr[2]);
                    }

                    if(receivedArr[0].equals("total")){
                        System.out.println("GAME OVER\nSCORES");
                        for(int i = 0; i < receivedArr.length; i++){
                            System.out.println("PLAYER " + (i+1) + " " + receivedArr[i + 1]);
                        }
                    }

                    if (receivedArr[0].equals("wait")) {
                        answering = false;
                        queue.offer("wait");
                        System.out.println("waiting");
                    }
                }
            }
                if(receivedArr[0].equals("next")){
                    queue.offer("next");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startUDPSender() {
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(ClientPanel.HOST_IP.getHostIP());

            while (true) {
                if(queue.isEmpty() == false && queue.peek().equals("next") == false && queue.peek().split(" ")[0].equals("setquestion") == false && queue.peek().split(" ")[0].equals("wait") == false){
                    String command = queue.poll();
                    if(command.equals("poll")){
                        String message = "buzz " + clientID;
                        byte[] buffer = message.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, UDP_PORT);
                        udpSocket.send(packet);
                        System.out.println("UDP Sent: " + message);
                    }else if(command.split(" ")[0].equals("score")){
                        String message = "score " + clientID + " " + player.getScore();
                        byte[] buffer = message.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, UDP_PORT);
                        udpSocket.send(packet);
                    }
                    
                    else{
                        System.out.println("Unrecognized command " + command);
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
