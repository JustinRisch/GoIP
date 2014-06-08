import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;


public class DiceBag extends JFrame {

	private JPanel contentPane;
	private JTextField d100;
	private JTextField d20;
	private JTextField d12;
	private JTextField d10;
	private JTextField d8;
	private JTextField d6;
	private JTextField d4;
	private JTextField cd;
	private JTextField dc;
	private JTextField add;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiceBag frame = new DiceBag();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DiceBag() {
		setTitle("Dice Bag");
		setBounds(100, 100, 243, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		d100 = new JTextField();
		d100.setBounds(10, 11, 86, 20);
		contentPane.add(d100);
		d100.setColumns(10);
		
		JLabel lblD = new JLabel("d100");
		lblD.setBounds(103, 14, 46, 14);
		contentPane.add(lblD);
		
		JLabel lblD_1 = new JLabel("d20");
		lblD_1.setBounds(103, 45, 46, 14);
		contentPane.add(lblD_1);
		
		d20 = new JTextField();
		d20.setColumns(10);
		d20.setBounds(10, 42, 86, 20);
		contentPane.add(d20);
		
		JLabel lblD_2 = new JLabel("d12");
		lblD_2.setBounds(103, 73, 46, 14);
		contentPane.add(lblD_2);
		
		d12 = new JTextField();
		d12.setColumns(10);
		d12.setBounds(10, 70, 86, 20);
		contentPane.add(d12);
		
		JLabel lblD_3 = new JLabel("d10");
		lblD_3.setBounds(103, 101, 46, 14);
		contentPane.add(lblD_3);
		
		d10 = new JTextField();
		d10.setColumns(10);
		d10.setBounds(10, 98, 86, 20);
		contentPane.add(d10);
		
		JLabel lblD_4 = new JLabel("d8");
		lblD_4.setBounds(103, 129, 46, 14);
		contentPane.add(lblD_4);
		
		d8 = new JTextField();
		d8.setColumns(10);
		d8.setBounds(10, 126, 86, 20);
		contentPane.add(d8);
		
		JLabel lblD_5 = new JLabel("d6");
		lblD_5.setBounds(103, 157, 46, 14);
		contentPane.add(lblD_5);
		
		d6 = new JTextField();
		d6.setColumns(10);
		d6.setBounds(10, 154, 86, 20);
		contentPane.add(d6);
		
		JLabel lblD_6 = new JLabel("d4");
		lblD_6.setBounds(103, 185, 46, 14);
		contentPane.add(lblD_6);
		
		d4 = new JTextField();
		d4.setColumns(10);
		d4.setBounds(10, 182, 86, 20);
		contentPane.add(d4);
		
		cd = new JTextField();
		cd.setColumns(10);
		cd.setBounds(10, 210, 86, 20);
		contentPane.add(cd);
		
		JButton btnRoll = new JButton("Roll!");
		btnRoll.setBounds(5, 241, 91, 23);
		btnRoll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String temp = d100.getText()+"d100 "+d20.getText()+"d20 "+d12.getText()+"d12 "+d10.getText()+"d10 "+d8.getText()+"d8 "+d6.getText()+"d6 ";
				temp += d4.getText()+"d4 "+ cd.getText() +"d"+dc.getText()+" +"+add.getText();
				System.out.println(temp);
			
			}
		});
		contentPane.add(btnRoll);
		
		dc = new JTextField();
		dc.setBounds(139, 210, 91, 20);
		contentPane.add(dc);
		dc.setColumns(10);
		
		JLabel lblD_7 = new JLabel("d");
		lblD_7.setBounds(103, 210, 11, 14);
		contentPane.add(lblD_7);
		
		JButton btnNewButton = new JButton("+");
		btnNewButton.setFocusable(false);
		btnNewButton.setBounds(137, 10, 46, 23);
		contentPane.add(btnNewButton);
		
		JButton button = new JButton("-");
		button.setFocusable(false);
		button.setBounds(184, 10, 46, 23);
		contentPane.add(button);
		
		JButton button_1 = new JButton("+");
		button_1.setFocusable(false);
		button_1.setBounds(137, 39, 46, 23);
		contentPane.add(button_1);
		
		JButton button_2 = new JButton("-");
		button_2.setFocusable(false);
		button_2.setBounds(184, 39, 46, 23);
		contentPane.add(button_2);
		
		JButton button_3 = new JButton("+");
		button_3.setFocusable(false);
		button_3.setBounds(137, 70, 46, 23);
		contentPane.add(button_3);
		
		JButton button_4 = new JButton("-");
		button_4.setFocusable(false);
		button_4.setBounds(184, 70, 46, 23);
		contentPane.add(button_4);
		
		JButton button_5 = new JButton("+");
		button_5.setFocusable(false);
		button_5.setBounds(137, 98, 46, 23);
		contentPane.add(button_5);
		
		JButton button_6 = new JButton("-");
		button_6.setFocusable(false);
		button_6.setBounds(184, 98, 46, 23);
		contentPane.add(button_6);
		
		JButton button_7 = new JButton("+");
		button_7.setFocusable(false);
		button_7.setBounds(137, 126, 46, 23);
		contentPane.add(button_7);
		
		JButton button_8 = new JButton("-");
		button_8.setFocusable(false);
		button_8.setBounds(184, 126, 46, 23);
		contentPane.add(button_8);
		
		JButton button_9 = new JButton("+");
		button_9.setFocusable(false);
		button_9.setBounds(137, 154, 46, 23);
		contentPane.add(button_9);
		
		JButton button_10 = new JButton("-");
		button_10.setFocusable(false);
		button_10.setBounds(184, 154, 46, 23);
		contentPane.add(button_10);
		
		JButton button_11 = new JButton("+");
		button_11.setFocusable(false);
		button_11.setBounds(137, 182, 46, 23);
		contentPane.add(button_11);
		
		JButton button_12 = new JButton("-");
		button_12.setFocusable(false);
		button_12.setBounds(184, 182, 46, 23);
		contentPane.add(button_12);
		
		add = new JTextField();
		add.setBounds(139, 242, 86, 20);
		contentPane.add(add);
		add.setColumns(10);
		
		JLabel label = new JLabel("+");
		label.setBounds(123, 245, 11, 14);
		contentPane.add(label);
	}
}
