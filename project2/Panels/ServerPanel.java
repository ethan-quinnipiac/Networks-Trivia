package project2.Panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ServerPanel implements ActionListener {
    public static final int TCP_PORT = 6000;
    private JPanel panel;
    private ServerSocket serverSocket;
    private Thread acceptingClientsThread;
    private boolean checkForClients;

    private JButton startGame;
    private JLabel playerList;
    private JLabel waiting;
    private HashMap<String, JButton> killSwitches;

    private HashMap<String, Socket> clientSockets;
    private ArrayList<String> clientIDs;

    private ServerLogic serverLogic;
    
    public ServerPanel() {
        checkForClients = true;
        panel = new JPanel();
        clientIDs = new ArrayList<>();
        clientSockets = new HashMap<>();
        // clientIDs.add("John");
        // clientIDs.add("Ethan");
        // clientIDs.add("Kyle");

        waiting = new JLabel("Waiting for players. Start when you are ready.");
        waiting.setBounds(100, 200, 350, 100);
        panel.add(waiting);

        startGame = new JButton("Start Game");
        startGame.setBounds(10, 300, 100, 20);
        startGame.addActionListener(this);
        panel.add(startGame);

        playerList = new JLabel(clientIDs.toString());
		playerList.setBounds(120, 300, 300, 20);
		panel.add(playerList);

        panel.setSize(400,400);
        panel.setBounds(50, 50, 400, 400);
		panel.setLayout(null);
		panel.setVisible(false);

        try {
            serverSocket = new ServerSocket(TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        acceptingClientsThread = new Thread(() -> {
            while (checkForClients) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    DataInputStream input = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                    String clientID = input.readUTF();
                    clientIDs.add(clientID);
                    clientSockets.put(clientID, clientSocket);
                    this.playerList.setText(clientIDs.toString());
                    this.panel.repaint();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        this.serverLogic = new ServerLogic(this);
    }

    public JPanel getPanel() {
        return this.panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Start game button
        if (e.getActionCommand().equals("Start Game")) {   
            this.startGame();
        }
        // Kill switch
        else {
            try {
                String clientID = e.getActionCommand().substring(5);
                this.clientSockets.get(clientID).close(); // Close the clients socket
                this.clientIDs.remove(clientID);
                playerList.setText(this.clientIDs.toString());
                this.clientSockets.remove(clientID);
                this.killSwitches.get(clientID).setVisible(false);
                this.killSwitches.remove(clientID);
                this.drawKillSwitches();
                this.panel.repaint();
            } catch (IOException except) {
                except.printStackTrace();
            }
        }
    }

    public void startAccepting() {
        this.acceptingClientsThread.start();
    }

    public void stopAccepting() {
        this.checkForClients = false;
    }

    public void startGame() {
        this.waiting.setVisible(false);
        this.startGame.setVisible(false);
        this.playerList.setBounds(20, 20, 300, 20);
        this.stopAccepting();
        this.drawKillSwitches();
        this.serverLogic.startGameLogic();
        String message = "Starting Game";
        for (String clientID : clientIDs) {
            try {
                DataOutputStream output = new DataOutputStream(new BufferedOutputStream(clientSockets.get(clientID).getOutputStream()));
                output.writeUTF(message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawKillSwitches() {
        if (killSwitches != null)
            for (String id: clientIDs) {
                this.killSwitches.get(id).setVisible(false);
            }
        killSwitches = new HashMap<>();
        int r = 0;
        int c = 0;
        for (String id : clientIDs) {
            JButton killSwitch = new JButton("Kill " + id);
            killSwitch.setBounds(20 + c * 100, 50 + r * 30, 100,30);
            killSwitch.addActionListener(this);
            panel.add(killSwitch);
            killSwitches.put(id, killSwitch);
            c = (c + 1) % 3;
            if (c == 0) {
                r++;
            }
        }
    }

    public ArrayList<String> getClientIDs() {
        return this.clientIDs;
    }

    public HashMap<String, Socket> getClientSockets() {
        return this.clientSockets;
    }
    
}
