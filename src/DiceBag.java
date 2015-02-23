import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DiceBag extends JFrame {
	// auto generated this thing, or it would yell at me.
	private static final long serialVersionUID = 7345177802546602894L;
	public JTextField[] j;

	public JTextField cd;
	public JTextField dc;
	public JTextField add;
	public JButton btnRoll;
	public JButton statbutt;
	private JPanel contentPane;
	private JTextField NoteBox;

	public DiceBag() {
		setResizable(false);
		this.setTitle("Dice Bag");
		this.setBounds(100, 100, 237, 337);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		contentPane.setLayout(null);

		j = new JTextField[7];
		String[] labels = {"d100", "d20","d12","d10","d8","d6","d4"};
		for (int i =0; i<7; i++) {
			j[i] = new JTextField();
			j[i].setHorizontalAlignment(SwingConstants.RIGHT);
			j[i].setBounds(5, 30 + i*28, 86, 20);
			j[i].setColumns(10);
			contentPane.add(j[i]);
			JButton button = new JButton("+");
			button.setFocusable(false);
			button.setBounds(132, 30 + 28 *i, 46, 23);
			button.addActionListener(new ButtonListener(j[i], 1));
			contentPane.add(button);

			button = new JButton("-");
			button.setFocusable(false);
			button.setBounds(179, 30 + 28 * i, 46, 23);
			button.addActionListener(new ButtonListener(j[i], -1));
			contentPane.add(button);
			JLabel lblD = new JLabel(labels[i]);
			lblD.setBounds(98, 30+28*i, 46, 14);
			contentPane.add(lblD);
		}

		cd = new JTextField(10);
		cd.setHorizontalAlignment(SwingConstants.RIGHT);
		cd.setBounds(5, 231, 86, 20);
		contentPane.add(cd);

		btnRoll = new JButton("Roll!");
		btnRoll.setFont(new Font("Arial Black", Font.BOLD, 11));
		btnRoll.setFocusCycleRoot(true);
		btnRoll.setBounds(5, 262, 91, 23);
		// the behavior of the roll button is added by the DM / Player programs.
		contentPane.add(btnRoll);
		contentPane.getRootPane().setDefaultButton(btnRoll);
		dc = new JTextField();
		dc.setHorizontalAlignment(SwingConstants.LEFT);
		dc.setBounds(134, 231, 91, 20);
		contentPane.add(dc);
		dc.setColumns(10);

		JLabel lblD_7 = new JLabel("d");
		lblD_7.setBounds(98, 231, 11, 14);
		contentPane.add(lblD_7);

		add = new JTextField();
		add.setHorizontalAlignment(SwingConstants.LEFT);
		add.setBounds(134, 263, 86, 20);
		contentPane.add(add);
		add.setColumns(10);

		JLabel label = new JLabel("+");
		label.setBounds(121, 266, 11, 14);
		contentPane.add(label);

		NoteBox = new JTextField();
		NoteBox.setText("Description of Bag");
		NoteBox.setHorizontalAlignment(SwingConstants.CENTER);
		NoteBox.setToolTipText("Put a description of the box here! (eg: Sword Damage)");
		NoteBox.setBounds(5, 1, 220, 20);
		contentPane.add(NoteBox);
		NoteBox.setColumns(10);

		statbutt = new JButton("Stat Roll (4d6 choose 3)");
		statbutt.setFocusable(false);
		statbutt.setBounds(5, 291, 215, 16);
		contentPane.add(statbutt);
	}

	class ButtonListener implements ActionListener {
		JTextField JTF;// where the button should get it's info from.
		int increment;// how much it should increment the number in that JTF

		public ButtonListener(JTextField JTF, int increment) {
			this.JTF = JTF;
			this.increment = increment;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Integer x;
			try {
				x = new Integer(JTF.getText());
			} catch (Exception err) {
				x = 0;
			}
			x += increment;
			if (x < 0)
				x = 0;
			JTF.setText(x.toString());

		}

	}
}
