import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import java.awt.Font;


public class DiceBag extends JFrame {
	//auto generated this thing, or it would yell at me. 
	private static final long serialVersionUID = 7345177802546602894L;
	public JTextField d100;
	public JTextField d20;
	public JTextField d12;
	public JTextField d10;
	public JTextField d8;
	public JTextField d6;
	public JTextField d4;
	public JTextField cd;
	public JTextField dc;
	public JTextField add;
	public JButton btnRoll;
	public JButton statbutt;
	private JPanel contentPane;
	private JTextField NoteBox;
	public DiceBag() {
		setType(Type.POPUP);
		setResizable(false);


		this.setTitle("Dice Bag");
		this.setBounds(100, 100, 237, 337);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		contentPane.setLayout(null);

		d100 = new JTextField();
		d100.setHorizontalAlignment(SwingConstants.RIGHT);
		d100.setBounds(5, 32, 86, 20);
		contentPane.add(d100);
		d100.setColumns(10);

		JLabel lblD = new JLabel("d100");
		lblD.setBounds(98, 35, 46, 14);
		contentPane.add(lblD);

		JLabel lblD_1 = new JLabel("d20");
		lblD_1.setBounds(98, 66, 46, 14);
		contentPane.add(lblD_1);

		d20 = new JTextField();
		d20.setHorizontalAlignment(SwingConstants.RIGHT);
		d20.setColumns(10);
		d20.setBounds(5, 63, 86, 20);
		contentPane.add(d20);

		JLabel lblD_2 = new JLabel("d12");
		lblD_2.setBounds(98, 94, 46, 14);
		contentPane.add(lblD_2);

		d12 = new JTextField();
		d12.setHorizontalAlignment(SwingConstants.RIGHT);
		d12.setColumns(10);
		d12.setBounds(5, 91, 86, 20);
		contentPane.add(d12);

		JLabel lblD_3 = new JLabel("d10");
		lblD_3.setBounds(98, 122, 46, 14);
		contentPane.add(lblD_3);

		d10 = new JTextField();
		d10.setHorizontalAlignment(SwingConstants.RIGHT);
		d10.setColumns(10);
		d10.setBounds(5, 119, 86, 20);
		contentPane.add(d10);

		JLabel lblD_4 = new JLabel("d8");
		lblD_4.setBounds(98, 150, 46, 14);
		contentPane.add(lblD_4);

		d8 = new JTextField();
		d8.setHorizontalAlignment(SwingConstants.RIGHT);
		d8.setColumns(10);
		d8.setBounds(5, 147, 86, 20);
		contentPane.add(d8);

		JLabel lblD_5 = new JLabel("d6");
		lblD_5.setBounds(98, 178, 46, 14);
		contentPane.add(lblD_5);

		d6 = new JTextField();
		d6.setHorizontalAlignment(SwingConstants.RIGHT);
		d6.setColumns(10);
		d6.setBounds(5, 175, 86, 20);
		contentPane.add(d6);

		JLabel lblD_6 = new JLabel("d4");
		lblD_6.setBounds(98, 206, 46, 14);
		contentPane.add(lblD_6);

		d4 = new JTextField();
		d4.setHorizontalAlignment(SwingConstants.RIGHT);
		d4.setColumns(10);
		d4.setBounds(5, 203, 86, 20);
		contentPane.add(d4);

		cd = new JTextField();
		cd.setHorizontalAlignment(SwingConstants.RIGHT);
		cd.setColumns(10);
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

		JButton btnNewButton = new JButton("+");
		btnNewButton.setFocusable(false);
		btnNewButton.setBounds(132, 31, 46, 23);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d100.getText());
				} catch (Exception err){
					x = 0;
				}
				x++; 
				d100.setText(x.toString());
			}
		});
		contentPane.add(btnNewButton);
		JButton button = new JButton("-");
		button.setFocusable(false);
		button.setBounds(179, 31, 46, 23);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d100.getText());
				} catch (Exception err){
					x = 0;
				}
				x--; 
				d100.setText(x.toString());
			}
		});
		contentPane.add(button);

		JButton button_1 = new JButton("+");
		button_1.setFocusable(false);
		button_1.setBounds(132, 60, 46, 23);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d20.getText());
				} catch (Exception err){
					x = 0;
				}
				x++; 
				d20.setText(x.toString());
			}
		});
		contentPane.add(button_1);

		JButton button_2 = new JButton("-");
		button_2.setFocusable(false);
		button_2.setBounds(179, 60, 46, 23);
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d20.getText());
				} catch (Exception err){
					x = 0;
				}
				x--; 
				d20.setText(x.toString());
			}
		});
		contentPane.add(button_2);

		JButton button_3 = new JButton("+");
		button_3.setFocusable(false);
		button_3.setBounds(132, 91, 46, 23);
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d12.getText());
				} catch (Exception err){
					x = 0;
				}
				x++; 
				d12.setText(x.toString());
			}
		});
		contentPane.add(button_3);

		JButton button_4 = new JButton("-");
		button_4.setFocusable(false);
		button_4.setBounds(179, 91, 46, 23);
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d12.getText());
				} catch (Exception err){
					x = 0;
				}
				x--; 
				d12.setText(x.toString());
			}
		});
		contentPane.add(button_4);

		JButton button_5 = new JButton("+");
		button_5.setFocusable(false);
		button_5.setBounds(132, 119, 46, 23);
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d10.getText());
				} catch (Exception err){
					x = 0;
				}
				x++; 
				d10.setText(x.toString());
			}
		});
		contentPane.add(button_5);

		JButton button_6 = new JButton("-");
		button_6.setFocusable(false);
		button_6.setBounds(179, 119, 46, 23);
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d10.getText());
				} catch (Exception err){
					x = 0;
				}
				x--; 
				d10.setText(x.toString());
			}
		});
		contentPane.add(button_6);

		JButton button_7 = new JButton("+");
		button_7.setFocusable(false);
		button_7.setBounds(132, 147, 46, 23);
		button_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d8.getText());
				} catch (Exception err){
					x = 0;
				}
				x++; 
				d8.setText(x.toString());
			}
		});
		contentPane.add(button_7);

		JButton button_8 = new JButton("-");
		button_8.setFocusable(false);
		button_8.setBounds(179, 147, 46, 23);
		button_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d8.getText());
				} catch (Exception err){
					x = 0;
				}
				x--; 
				d8.setText(x.toString());
			}
		});
		contentPane.add(button_8);

		JButton button_9 = new JButton("+");
		button_9.setFocusable(false);
		button_9.setBounds(132, 175, 46, 23);
		button_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d6.getText());
				} catch (Exception err){
					x = 0;
				}
				x++; 
				d6.setText(x.toString());
			}
		});
		contentPane.add(button_9);

		JButton button_10 = new JButton("-");

		button_10.setFocusable(false);
		button_10.setBounds(179, 175, 46, 23);
		button_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d6.getText());
				} catch (Exception err){
					x = 0;
				}
				x--; 
				d6.setText(x.toString());
			}
		});
		contentPane.add(button_10);

		JButton button_11 = new JButton("+");
		button_11.setFocusable(false);
		button_11.setBounds(132, 203, 46, 23);
		button_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d4.getText());
				} catch (Exception err){
					x = 0;
				}
				x++; 
				d4.setText(x.toString());
			}
		});
		contentPane.add(button_11);

		JButton button_12 = new JButton("-");
		button_12.setFocusable(false);
		button_12.setBounds(179, 203, 46, 23);
		button_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer x; 
				try { 
					x = new Integer(d4.getText());
				} catch (Exception err){
					x = 0;
				}
				x--; 
				d4.setText(x.toString());
			}
		});
		contentPane.add(button_12);

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
}


