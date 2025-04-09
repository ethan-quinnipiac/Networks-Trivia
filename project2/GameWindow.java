package project2;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import project2.Panels.ClientPanel;
import project2.Panels.GamePanel;
import project2.Panels.ServerPanel;
import project2.Panels.StartPanel;

public class GameWindow {
    private JFrame window;
    private JPanel cards;
    private CardLayout cardLayout;
    private ServerPanel serverPanelObject;
    private ClientPanel clientPanelObject;
    private GamePanel gamePanelObject;
    public static final String START_PANEL = "Start Panel";
    public static final String CLIENT_PANEL = "Client Panel";
    public static final String SERVER_PANEL = "Server Panel";
    public static final String GAME_PANEL = "Game Panel";
    
    public GameWindow() {
        JOptionPane.showMessageDialog(window, "Welcome to our trivia game!");
        window = new JFrame("Trivia!");

        cards = new JPanel(new CardLayout());
        cards.setSize(400,400);
        cards.setVisible(true);
        window.getContentPane().add(cards);
        cardLayout = (CardLayout) cards.getLayout();

        JPanel startPanel = new StartPanel(this).getPanel();
        this.clientPanelObject = new ClientPanel(this);
        JPanel clientPanel = clientPanelObject.getPanel();
        this.serverPanelObject = new ServerPanel();
        JPanel serverPanel = serverPanelObject.getPanel();
        this.gamePanelObject = new GamePanel();
        JPanel gamePanel = gamePanelObject.getPanel();

        cards.add(startPanel, START_PANEL);
        cards.add(clientPanel, CLIENT_PANEL);
        cards.add(serverPanel, SERVER_PANEL);
        cards.add(gamePanel, GAME_PANEL);

        window.setSize(400, 400);
        window.setBounds(50, 50, 400, 400);
		window.setLayout(null);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
        cardLayout.show(cards, START_PANEL);
    }

    public void changePanel(String staticName) {
        cardLayout.show(cards, staticName);
    }

    public ServerPanel getServerPanel() {
        return this.serverPanelObject;
    }

    public ClientPanel getClientPanel() {
        return this.clientPanelObject;
    }

    public GamePanel getGamePanel() {
        return this.gamePanelObject;
    }
}
