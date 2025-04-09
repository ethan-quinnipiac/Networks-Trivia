package project2.Panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import project2.GameWindow;

public class StartPanel implements ActionListener {
    private GameWindow gameWindow;
    private JPanel panel;
    private JButton hostGame;
    private JButton joinGame;
    private JLabel pickOption;
    
    public StartPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        panel = new JPanel();

        pickOption = new JLabel("Pick an option.");
        pickOption.setBounds(100, 200, 350, 100);
        panel.add(pickOption);

        hostGame = new JButton("Host Game");
        hostGame.setBounds(10, 300, 100, 20);
        hostGame.addActionListener(this);
        panel.add(hostGame);

        joinGame = new JButton("Join Game");
		joinGame.setBounds(200, 300, 100, 20);
		joinGame.addActionListener(this);
		panel.add(joinGame);

        panel.setSize(400,400);
        panel.setBounds(50, 50, 400, 400);
		panel.setLayout(null);
		panel.setVisible(false);
    }

    public JPanel getPanel() {
        return this.panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = e.getActionCommand();

        switch (input) {
            case "Host Game":
                this.gameWindow.changePanel(GameWindow.SERVER_PANEL);
                this.gameWindow.getServerPanel().startAccepting();
                break;
            case "Join Game":
                this.gameWindow.changePanel(GameWindow.CLIENT_PANEL);
                this.gameWindow.getClientPanel().claimAnID();
                break;
            default:
                break;
        }
    }

}
