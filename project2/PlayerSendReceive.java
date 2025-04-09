package project2;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/*
 * Coded by Kyle Macdonald
 * Ran by the player to start the game
 * Prerequisite: Server must be running, questions txt file must be present. ID must be set
 * Output: Trivia game window with terminal as output
 */

public class PlayerSendReceive {

    private static final String IP = "127.0.0.1"; // Localhost for testing
    private static final int UDP_PORT = 5000;
    private static final int TCP_PORT = 6000;
    private static final int clientID = 2;

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    private static boolean answering = false;
    private static int finalAnswer = -1234;
    private static Question[] questions = QuestionMaker.makeQuestions();
    private static Player player = new Player(Integer.toString(clientID));
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        //Thread to receive messages using TCP
        executor.execute(() -> startTCPReceiver());

        //Thread to send messages using UDP
        executor.execute(() -> startUDPSender());

        //Thread starts the game
        executor.execute(() -> startGame());
    }


    private static void startGame(){
        List<Question> questionPool = Arrays.asList(questions);
        // Create the ClientWindow
        ClientWindow clientWindow = new ClientWindow();

        // Create the Game instance
        Game game = new Game(questionPool, clientWindow);
        game.addPlayer(player);
        game.nextQuestion();
        // Start the game
        try{
            while (true){
            clientWindow.submit.setEnabled(answering);
            clientWindow.score.setText("SCORE: " + player.getScore());
            finalAnswer = clientWindow.finalAnswer;
            Thread.sleep(1000);
            if(answering && !game.isAnsweringActive){
                game.startAnsweringTimer(player);
            }
            if(clientWindow.finalAnswer == 0){
                queue.add("poll");
                clientWindow.finalAnswer = -1;
            }
            if(queue.peek() == "next"){ //checks to see if next question is necessary
                queue.poll();
                game.nextQuestion();
            }else if(queue.peek() != null){ //skips x amount of questions based on game in progress
                if(queue.peek().split(" ")[0].equals("setquestion")){
                    int questionCount = Integer.parseInt(queue.poll().split(" ")[1]) - 1;
                    System.out.println("skipping questions...");
                    for(int i = 0; i < questionCount; i++){
                        game.nextQuestion();
                    }
                }
            }

            if(game.isOver){
                queue.add("score " + clientID + " " + player.getScore());
                Thread.sleep(9000);
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
                if(receivedArr.length != 1){
                if(receivedArr[1].equals(Integer.toString(clientID)) || receivedArr[0] == "total"){
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

    private static void startUDPSender() {
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(IP);

            while (true) {
                if(queue.isEmpty() == false && queue.peek().equals("next") == false && queue.peek().split(" ")[0].equals("setquestion") == false){
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
