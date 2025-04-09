package project2.Panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ClientPanel implements ActionListener {
    private JPanel panel;
    private JLabel connectionStatus;

    private String clientID;
    private Thread TCPConnector;
    private Socket clientSocket;
    public static final HostIP HOST_IP = new HostIP();
    
    public ClientPanel() {
        this.panel = new JPanel();

        this.connectionStatus = new JLabel("Waiting to connect to host.");
        this.connectionStatus.setBounds(100, 200, 350, 100);
        this.panel.add(this.connectionStatus);

        this.panel.setSize(400,400);
        this.panel.setBounds(50, 50, 400, 400);
		this.panel.setLayout(null);
		this.panel.setVisible(false);


        this.TCPConnector = new Thread(() -> {
            while (this.clientSocket == null) {
                try {
                    this.clientSocket = new Socket(HOST_IP.getHostIP(), ServerPanel.TCP_PORT);
                    DataOutputStream output = new DataOutputStream(new BufferedOutputStream(this.clientSocket.getOutputStream()));
                    output.writeUTF(clientID);
                    output.flush();
                    this.connectionStatus.setText("Player " + clientID + " is good to go!");
                    this.panel.repaint();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public JPanel getPanel() {
        return this.panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
    public void claimAnID() {
        clientID = JOptionPane.showInputDialog("Please enter a name for yourself.");
        this.connectionStatus.setText("Waiting to connect Player " + clientID + " to host.");
        this.TCPConnector.start();
    }
}
