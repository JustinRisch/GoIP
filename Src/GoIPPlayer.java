
import java.awt.EventQueue;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.io.*;

import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.Dialog.ModalExclusionType;


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
	private JButton btnRollD;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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
				result+=decrypter[i+(decrypter.length)/2]; 
			} else { 
				result+=decrypter[i+decrypter.length/2+1]; 
				if (i==(decrypter.length/2)-1)
					result +=decrypter[(int)Math.floor(decrypter.length/2)];
			}
		}			
		if (decrypter.length<1)
			result = start;
		return result; 
	}

	public static String roll(String[] banana){
		try {
			String results = "";
			int sum = 0; 
			int D = 20; 
			int dnum = 0;
			Integer adder = 0; 
			for (int x = 1; x < banana.length; x++) 
			{
				if (banana[x].contains("+") || banana[x].contains("-")) {
					//continue;
					adder+=new Integer(banana[x]);
					continue;
				} 
				if (banana[x].contains("d"))
				{
					if (banana[x].split("d")[0]!=null && !banana[x].split("d")[0].equals(""))
						dnum = Integer.parseInt(banana[x].split("d")[0]); 
					else 
						dnum =0; 
					try {
						D = Integer.parseInt(banana[x].split("d")[1]); 
					} catch (Exception e){
						D = 20;
					}

				} else {
					if (banana[x]!=null && !banana[x].equals(""))
						dnum = Integer.parseInt(banana[x]); 
					if (banana[x+1]!=null && !banana[x+1].equals(""))
						D = Integer.parseInt(banana[x+1]); 
					x++;
				}
				for (int i = 0; i < dnum; i++) { 
					int roll = (int)Math.floor(Math.random()*D+1); 
					sum += roll; 
					results += roll;
					results += "("+D+") "; 
				}

			}
			if (banana.length<2)
			{
				int roll = (int)Math.floor(Math.random()*D+1); 
				sum += roll; 
				results += roll;
				results += "["+D+"] "; 
			}
			sum+=adder;
			return " rolled a "+sum +"(+"+adder+"): "+results;
		} catch (Exception e) { 
			return " got an Error - "+ e.getMessage(); //System.out.println(encrypt(Message)e.getMessage());	
		}
	}
	public static String statroll(){
		String result=""; 
		int[] rolls = new int[4];
		for (int i = 0; i<4; i++)
			rolls[i]=(int)(Math.random()*6)+1; 
		Arrays.sort(rolls);	
		result = "Total="+(rolls[3]+rolls[2]+rolls[1])+"; ["+rolls[3]+"] "+"["+rolls[2]+"] "+"["+rolls[1]+"] "+"["+rolls[0]+"]\n";
		return result; 
	}
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
		frmGoIPPlayer.setResizable(false);
		frmGoIPPlayer.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
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
		scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frmGoIPPlayer.getContentPane().add(scrollPane2);
		chatArea.setText("Type in the IP of your DM or use the dice bag to roll without connecting.\n");

		JLabel lblPlayerList = new JLabel("Player List");
		lblPlayerList.setBounds(437, -2, 98, 28);
		frmGoIPPlayer.getContentPane().add(lblPlayerList);

		btnRollD = new JButton("Dice Bag");
		btnRollD.setBounds(439, 226, 91, 23);
		btnRollD.setToolTipText("Do it. I dare you.");
		btnRollD.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final DiceBag db = new DiceBag();
				if (connected) {
					db.btnRoll.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String temp = "roll "+db.d100.getText().trim()+"d100 "+db.d20.getText().trim()+"d20 "+db.d12.getText().trim()+"d12 "+db.d10.getText().trim()+"d10 "+db.d8.getText().trim()+"d8 "+db.d6.getText().trim()+"d6 ";
							temp += db.d4.getText().trim()+"d4";
							if (!db.dc.getText().trim().equals(""))
								temp +=" "+db.cd.getText().trim()+"d"+db.dc.getText().trim();
							if (!db.add.getText().trim().equals(""))
								temp+=" +"+db.add.getText().trim();
							out.println(encrypt(temp));
						}
					});
				}else{
					db.statbutt.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							chatArea.append(statroll());
						}
					});
					db.btnRoll.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String temp = "roll "+db.d100.getText().trim()+"d100 "+db.d20.getText().trim()+"d20 "+db.d12.getText().trim()+"d12 "+db.d10.getText().trim()+"d10 "+db.d8.getText().trim()+"d8 "+db.d6.getText().trim()+"d6 ";
							temp += db.d4.getText().trim()+"d4";
							if (!db.dc.getText().trim().equals(""))
								temp +=" "+db.cd.getText().trim()+"d"+db.dc.getText().trim();
							if (!db.add.getText().trim().equals(""))
								temp+=" +"+db.add.getText().trim();
							chatArea.append("You"+roll(temp.split(" "))+"\n");
						}
					});
				}

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
		@Override
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
		@Override
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
}

