
import java.awt.EventQueue;
import java.text.DecimalFormat;
import javax.swing.JFrame;
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

	/**
	 * Create the application.
	 */
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

		btnRollD = new JButton("Roll d20");
		btnRollD.setBounds(439, 226, 91, 23);
		btnRollD.setToolTipText("Do it. I dare you.");
		btnRollD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(encrypt("roll 1d20"));
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
}

