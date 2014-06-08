
import java.awt.EventQueue;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

import javax.swing.JLabel;
import javax.swing.JButton; 
import javax.swing.border.EmptyBorder;


public class GoIPPlayer {
	public static Thread listenUp; 
	public static PrintWriter out = null;
	public static GoIPPlayer window; 
	private static JFrame frmGoIPPlayer;
	private static JTextField inputLine;
	private static JTextArea chatArea;
	public static JTextArea listPlayers;
	private static boolean connected = false; 
	private static String IP = "";
	private static String lastSent = "";
	private static Socket transSocket; 
	private static Socket playerListSocket; 
	private static BufferedReader in; 
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new GoIPPlayer();
					frmGoIPPlayer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public GoIPPlayer() {
		initialize();
	}
	/*
	Note: this encryption is *not* meant to uphold to any real scrutiny. Rather, it's meant to deter passive searching. 
	Most DnD campaigns talk about doing illicit, graphic, or gore intensive activities. This is meant to prevent a "passerby" 
	from "overhearing" this *fictional* conversation, mistaking it for a real threat, and acting on it. Other than that, 
	it was a fun method to write. 
	 */
	static String encrypt(String start){
		String result = ""; 
		start= start.replace("\n"," ").trim();
		char[] alphabet = {1,2,3,4,5,6,7,8,9,0,'!','@','#','$','%','^','&','*','(',')','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		char[] temp = start.toCharArray();
		for (int i = 0; i<temp.length; i+=2)
		{
			char key = alphabet[(int)Math.floor(Math.random()*alphabet.length)];
			while ((temp[i]+Character.getNumericValue(key)) < 32 || (temp[i]+Character.getNumericValue(key))>126)
				key = alphabet[(int)Math.floor(Math.random()*alphabet.length)];
			result+= Character.toString((char)(temp[i]+Character.getNumericValue(key)));
			result+= key;
		}
		for (int i = 1; i<temp.length; i+=2)
		{
			char key = alphabet[(int)Math.floor(Math.random()*alphabet.length)];
			while ((temp[i]+Character.getNumericValue(key)) < 32 || (temp[i]+Character.getNumericValue(key))>125)
				key = alphabet[(int)Math.floor(Math.random()*alphabet.length)];
			result+= Character.toString((char)(temp[i]+Character.getNumericValue(key)));
			result+= key;
		}
		//System.out.println(result);
		return result; 
	}
	static String decrypt(String start){
		String result = ""; 
		char[] decrypter = start.toCharArray();
		{
			String temp = "";
			for (int i = 0; i < decrypter.length-1; i+=2)
			{
				temp+=Character.toString((char)(decrypter[i]-Character.getNumericValue(decrypter[i+1])));
			}
			decrypter = temp.toCharArray();
		}
		for (int i = 0; i< ((decrypter.length)/2); i++)
		{
			result+=decrypter[i]; 
			if (decrypter.length%2==0){
				result+=decrypter[i+(int)(decrypter.length)/2]; 
			} else { 
				result+=decrypter[i+(int)decrypter.length/2+1]; 
				if (i==(decrypter.length/2)-1)
					result +=decrypter[(int)Math.floor(decrypter.length/2)];
			}
		}			
		if (decrypter.length<1)
			result = start;
		return result; 
	}

	private JButton btnRollD;
	boolean makeconnection() 
	{
		try {  
			transSocket = null;
			in = null;
			transSocket = new Socket(IP, 1813); 
			playerListSocket = new Socket(IP, 1813); 	
			chatArea.setText(IP+" - ");
			out = new PrintWriter(transSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(transSocket.getInputStream()));
			Thread playerListThread = new Thread(new playerListener(new BufferedReader(new InputStreamReader(playerListSocket.getInputStream()))));
			listenUp = new Thread( new Ears(transSocket, in));
			playerListThread.start();
			listenUp.start();
			return true;
		}catch(UnknownHostException e){
			chatArea.setText("Host Unknown Exception.. yeah you should probably google that.");
			return false;
		} catch(IOException e) {
			chatArea.setText(IP+" FAILED: Make sure your DM has his ports open and his server on. Hit enter to retry or type in a new IP.");
			return false;
		}catch(Exception e) { 
			chatArea.setText("Well this is awkward. No idea why you can't connect.");
			return false;
		}	
	}


	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("static-access")
	//standard GUI initialization 
	void initialize()  {
		frmGoIPPlayer = new JFrame();
		frmGoIPPlayer.setTitle("GoIP Player");
		frmGoIPPlayer.setBounds(100, 100, 552, 282);
		frmGoIPPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGoIPPlayer.getContentPane().setLayout(null);

		inputLine = new JTextField();
		inputLine.setBounds(7, 227, 422, 20);
		frmGoIPPlayer.getContentPane().add(inputLine);
		inputLine.setColumns(10);
		inputLine.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)  { // when you hit enter
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					String input = inputLine.getText();
					//it appends what you wrote to the chat box 
					if (!input.contains("setname")) //avoid annoying double messages from commands and responses.  
					chatArea.append(inputLine.getText()+"\n");
					if(!input.equals(null) && !input.equals(""))	
					{	
						if (!connected)
						{
							if(input.equalsIgnoreCase("LAN")){
								chatArea.setText("Feature Removed due to strange complications. Type in IP");
							}else{ 
								IP = input;
								frmGoIPPlayer.setTitle("GoIP Player - " + IP);
								connected = makeconnection();
							}
						} else if (input.equalsIgnoreCase("reset")){
							try { 
								out.println("reset");
								//cleaning up connections before remaking them
								if (!transSocket.isClosed())
									transSocket.close();
								if (!playerListSocket.isClosed())
									playerListSocket.close();
								in.close();
								out.close();
								makeconnection();
							} catch (Exception q){
								chatArea.setText("Failed to reconnect.");
							}
						} else 
							out.println(encrypt(input.trim()));	
						lastSent = input;
							inputLine.setText("");

					}	
				}	
			} 
		});

		/*
		 *   message.setWrapStyleWord(true);
                    message.setLineWrap(true);
		 */
		listPlayers = new JTextArea();
		listPlayers.setTabSize(3);
		listPlayers.setEditable(false);
		listPlayers.setBounds(369, 34, 91, 141);
		JScrollPane scrollPane = new JScrollPane(listPlayers);
		scrollPane.setBounds(433, 23, 102, 200);
		frmGoIPPlayer.getContentPane().add(scrollPane);

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setBounds(10, 11, 422, 220);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);

		JScrollPane scrollPane2 = new JScrollPane(chatArea);
		scrollPane2.setBounds(7, 7, 422, 216);
		scrollPane2.setHorizontalScrollBarPolicy(scrollPane2.HORIZONTAL_SCROLLBAR_NEVER);
		frmGoIPPlayer.getContentPane().add(scrollPane2);
		chatArea.setText("Type in the IP of your Server:");

		JLabel lblPlayerList = new JLabel("Player List");
		lblPlayerList.setBounds(437, -2, 98, 28);
		frmGoIPPlayer.getContentPane().add(lblPlayerList);

		btnRollD = new JButton("Dice Bag");
		btnRollD.setBounds(439, 226, 91, 23);
		btnRollD.setToolTipText("Do it. I dare you.");
		btnRollD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DiceBag db = new DiceBag();
				db.setVisible(true);
			}
		});
		frmGoIPPlayer.getContentPane().add(btnRollD);
	}
	//handles all incoming messages. 
	static class Ears implements Runnable {
		private BufferedReader in;
		public Ears(){}
		public Ears (Socket listener, BufferedReader in) {  // the constructor that should always be used  
			this.in = in; 
		}
		public void run(){
			String fromServer = null;
			try {
				while((fromServer = in.readLine())!=null){	
					fromServer=decrypt(fromServer);
					if (!fromServer.trim().equals("") && !fromServer.trim().equals("\n")) //filter
					{
						if (!lastSent.equalsIgnoreCase("ping"))
							chatArea.append(fromServer+"\n");
						else { 
							String results = "";
							int i = 0; 
							Date date = new Date();									
							SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss.SSSS");
							String formattedDate = sdf.format(date);
							for (String x : formattedDate.split(":"))
							{
								DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
								results+= new Double(df2.format(Double.parseDouble(x)-Double.parseDouble(fromServer.split(":")[i]))).doubleValue()+":";
								i++; 
							}

							chatArea.append(results+"\n");
						}
					}
				}	
			}catch(Exception e) {
				chatArea.append("");
			} 
		}
	}
	//this thread handles incoming information for the player list object. Does nothing else. 
	static class playerListener implements Runnable {
		private BufferedReader in;
		public playerListener(){}
		public playerListener (BufferedReader in) {  // the constructor that should always be used  
			this.in = in; 
		}
		public void run(){
			String fromServer = null;
			try {
				while((fromServer = in.readLine())!=null){	
					if (!fromServer.trim().equals("") && !fromServer.trim().equals("\n")) //filter
					{
						fromServer = decrypt(fromServer);
						listPlayers.setText(fromServer.replace("|", "\n")); 
					}
				}	
			}catch(Exception e) {
				chatArea.append("");
			} 
		}
	}
	class DiceBag extends JFrame {

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
					String temp = "roll "+d100.getText().trim()+"d100 "+d20.getText().trim()+"d20 "+d12.getText().trim()+"d12 "+d10.getText().trim()+"d10 "+d8.getText().trim()+"d8 "+d6.getText().trim()+"d6 ";
					temp += d4.getText().trim()+"d4";
					if (!dc.getText().trim().equals(""))
						temp +=" "+cd.getText().trim()+"d"+dc.getText().trim();
					if (!add.getText().trim().equals(""))
						temp+=" +"+add.getText().trim();
					out.println(encrypt(temp));
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
}

