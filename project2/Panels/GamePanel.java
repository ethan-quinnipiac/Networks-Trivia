package project2.Panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import project2.Question;

/**
 * John Caceres
 * 
 * ClientWindow equivalent
 */
public class GamePanel implements ActionListener {
    private JPanel panel;
    private ClientLogic clientLogic;

    public JButton poll;
	public JButton submit;
	private JRadioButton options[];
	private ButtonGroup optionGroup;
	private JLabel question;
	public JLabel timer;
	public JLabel score;
	public TimerTask clock;

    public static int answer;
	public static int finalAnswer = -1;

    public GamePanel() {
        this.panel = new JPanel();

        this.question = new JLabel();
        this.panel.add(this.question);
        this.question.setBounds(10, 5, 350, 100);

        this.options = new JRadioButton[4];
		this.optionGroup = new ButtonGroup();
		for(int index=0; index<this.options.length; index++)
		{
			this.options[index] = new JRadioButton();  // represents an option
			// if a radio button is clicked, the event would be thrown to this class to handle
			this.options[index].addActionListener(this);
			this.options[index].setBounds(10, 110+(index*20), 350, 20);
			this.panel.add(this.options[index]);
			this.optionGroup.add(this.options[index]);
		}

        this.timer = new JLabel("TIMER");  // represents the countdown shown on the window
		this.timer.setBounds(250, 250, 100, 20);
		this.clock = new TimerCode(15);  // represents clocked task that should run after X seconds
		Timer t = new Timer();  // event generator
		t.schedule(this.clock, 0, 1000); // clock is called every second
		this.panel.add(this.timer);

        this.score = new JLabel("SCORE"); // represents the score
		this.score.setBounds(50, 250, 100, 20);
		this.panel.add(this.score);

        this.poll = new JButton("Poll");  // button that use clicks/ like a buzzer
		this.poll.setBounds(10, 300, 100, 20);
		this.poll.addActionListener(this);  // calls actionPerformed of this class
		this.panel.add(this.poll);

        this.submit = new JButton("Submit");  // button to submit their answer
		this.submit.setBounds(200, 300, 100, 20);
		this.submit.addActionListener(this);  // calls actionPerformed of this class
		this.panel.add(this.submit);
        
        this.panel.setSize(400, 400);
        this.panel.setBounds(50, 50, 400, 400);
        this.panel.setLayout(null);
        this.panel.setVisible(false);

        this.clientLogic = new ClientLogic(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("You clicked " + e.getActionCommand());
		
		// input refers to the radio button you selected or button you clicked
		String input = e.getActionCommand();
		if (input.equals("Poll")) {
			this.poll.setEnabled(false);
			finalAnswer = 0;
		} else if (input.equals("Submit")) {
			this.submit.setEnabled(false);
			finalAnswer = answer;
		} else {
			for (int i = 0; i < options.length; i++) {
				if (input.equals(options[i].getText())) {
					answer = i + 1;
					break;
				}
			}
		}
    }
    
    public void startGame(String clientID, Socket clientSocket) {
        this.clientLogic.startGameLogic(clientID, clientSocket);
    }

    public JPanel getPanel() {
        return this.panel;
    }
    
    // this class is responsible for running the timer on the window
	public class TimerCode extends TimerTask
	{
		private int duration;  // write setters and getters as you need
		public TimerCode(int duration)
		{
			this.duration = duration;
		}

		
		@Override
		public void run()
		{
			if(duration < 0)
			{
				timer.setText("Timer expired");
				panel.repaint();
				this.cancel();  // cancel the timed task
				return;
				// you can enable/disable your buttons for poll/submit here as needed
			}
			
			if(duration < 6)
				timer.setForeground(Color.red);
			else
				timer.setForeground(Color.black);
			
			timer.setText(duration+"");
			duration--;
			panel.repaint();
		}

		public void setTime(int time){
			this.duration = time;
		}
	}

	// this method updates the question and options on the window
	public void displayQuestion(Question questionObj) {
		// Update the question label
		question.setText(questionObj.getQuestionText());

		// Update the radio button options
		List<String> optionsList = questionObj.getOptions();
		for (int i = 0; i < options.length; i++) {
			options[i].setText(optionsList.get(i));
			options[i].setEnabled(true); // Enable the options for selection
		}

		// Reset the button states
		poll.setEnabled(true);
		submit.setEnabled(false);

		// Reset the timer (if needed)
		timer.setText("15"); // Example: Reset to 15 seconds for polling
	}
}
