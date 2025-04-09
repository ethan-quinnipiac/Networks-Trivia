package project2;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.*;

public class ClientWindow implements ActionListener
{
	public JButton poll;
	public JButton submit;
	private JRadioButton options[];
	private ButtonGroup optionGroup;
	private JLabel question;
	public JLabel timer;
	public JLabel score;
	public TimerTask clock;
	
	private JFrame window;
	
	private static SecureRandom random = new SecureRandom();
	
	public static int answer;
	public static int finalAnswer = -1;

	
	// write setters and getters as you need
	
	public ClientWindow()
	{
		JOptionPane.showMessageDialog(window, "This is a trivia game");
		
		window = new JFrame("Trivia");
		question = new JLabel("Q1. This is a sample question"); // represents the question
		window.add(question);
		question.setBounds(10, 5, 350, 100);;
		
		options = new JRadioButton[4];
		optionGroup = new ButtonGroup();
		for(int index=0; index<options.length; index++)
		{
			options[index] = new JRadioButton("Option " + (index+1));  // represents an option
			// if a radio button is clicked, the event would be thrown to this class to handle
			options[index].addActionListener(this);
			options[index].setBounds(10, 110+(index*20), 350, 20);
			window.add(options[index]);
			optionGroup.add(options[index]);
		}

		timer = new JLabel("TIMER");  // represents the countdown shown on the window
		timer.setBounds(250, 250, 100, 20);
		clock = new TimerCode(15);  // represents clocked task that should run after X seconds
		Timer t = new Timer();  // event generator
		t.schedule(clock, 0, 1000); // clock is called every second
		window.add(timer);
		
		
		score = new JLabel("SCORE"); // represents the score
		score.setBounds(50, 250, 100, 20);
		window.add(score);

		poll = new JButton("Poll");  // button that use clicks/ like a buzzer
		poll.setBounds(10, 300, 100, 20);
		poll.addActionListener(this);  // calls actionPerformed of this class
		window.add(poll);
		
		submit = new JButton("Submit");  // button to submit their answer
		submit.setBounds(200, 300, 100, 20);
		submit.addActionListener(this);  // calls actionPerformed of this class
		window.add(submit);
		
		
		window.setSize(400,400);
		window.setBounds(50, 50, 400, 400);
		window.setLayout(null);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
	}

	// this method is called when you check/uncheck any radio button
	// this method is called when you press either of the buttons- submit/poll
	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("You clicked " + e.getActionCommand());
		
		// input refers to the radio button you selected or button you clicked
		String input = e.getActionCommand();
		if (input.equals("Poll")) {
			// Your code here
			// TODO Finalize polling behavior
			poll.setEnabled(false);
			finalAnswer = 0;
		} else if (input.equals("Submit")) {
			// TODO Finalize answer-submitting behavior
			submit.setEnabled(false);
			finalAnswer = answer;
		} else {
			for (int i = 0; i < options.length; i++) {
				if (input.equals(options[i].getText())) {
					answer = i + 1;
					break;
				}
			}
		}
		
		// test code below to demo enable/disable components
		// DELETE THE CODE BELOW FROM HERE***
		// if(poll.isEnabled())
		// {
		// 	poll.setEnabled(false);
		// 	submit.setEnabled(true);
		// }
		// else
		// {
		// 	poll.setEnabled(true);
		// 	submit.setEnabled(false);
		// }
		
		// question.setText("Q2. This is another test problem " + random.nextInt());
		
		// you can also enable disable radio buttons
//		options[random.nextInt(4)].setEnabled(false);
//		options[random.nextInt(4)].setEnabled(true);
		// TILL HERE ***
		
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
				window.repaint();
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
			window.repaint();
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
