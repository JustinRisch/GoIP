
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.JScrollPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class GoIPDM {
	public static JFrame frmGoipDmServer;
	public static JTextField inputLine;
	public static JTextArea chatArea;
	public static JTextArea listPlayers;
	public static ClientConnecter clientListener = new ClientConnecter();
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new GoIPDM();
					frmGoipDmServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Thread heh = new Thread(clientListener); 
		heh.start(); // begin listening for clients
	}

	public static String help(){ // the internal "readme" for the DM. Can't send to player as it contains line returns, and that screws up everything. 
		String message = "Action - syntax - explanation\n";
		message+= "Broadcasting - bc [message] - sends a message to all players.\n";
		message+= "Message - msg (player) [message] - sends a message to a single player. \n";
		message+= "Dice rolling - \nroll (by itself) = 1d20 roll\nroll d# = roll 1 dice of # sides.\nroll #d = roll # dice of 20 sides.\nroll #d# = roll # dice of # sides.\nroll # # = roll # dice of # sides.\n";
		message+= "Loot Distribution - \nLoot [group of players space delimited]:[group of items],[group of players space delimited]:[group of items]... \n";
		message+= "Example: 'Loot Justin Connor:1gp sword of smite, All:2 silver'\n the above would give justin and connor a sword of smite and 1 gp,\n then everyone gets an additional 2 silver\n";  
		return message; 
	}
	//given a connection to the clientConnector and a message, send that message to 
	public static void Message(ClientConnecter clients, String outter){
		String Message="";
		String[] params = outter.split(" ");
		try { 
			//locates the socket of the intended recipient. 
			ClientHandler x = clients.getClient(params[1]);
			//Easiest way to trim the user name and "msg" from the message. 
			for (int i = 2; i < params.length; i++)
				Message+= params[i]+" ";
			Message=encrypt(Message);
			//send message down the socket's output stream. Probably should find a way to access the variable associated with this. 
			x.out.println(Message);
		} catch (Exception e) {
			chatArea.append("Player not found: " + params[1]);

		}
	}

	// connects clients and keeps an ArrayList of all connected clients.
	// Great for broadcasting, messaging between active clients. 
	static class ClientConnecter implements Runnable {
		private static ServerSocket serverSocket = null;
		private static ArrayList<Socket> listeners = new ArrayList<Socket>();
		private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>(); 
		public ArrayList<ClientHandler> getClients(){
			return clients; 
		}
		public void removeClient(ClientHandler x){
			clients.remove(x);
		}
		public void sendList(){
			String temp ="";
			for (ClientHandler x : clients)
				temp += x.Name+"|";
			for (ClientHandler x : clients)
				x.PlayerListWriter.println(encrypt(temp));
		}
		public ClientConnecter (){
			try{
				serverSocket = new ServerSocket(1813);
			}catch(IOException e){
				System.err.println("Can't listen on Port 1813"); //if it can't listen on the port selected, it will say so here. Try changing it in the line above. 
				System.exit(1337);
			}
		}
		public void run (){
			while(true) { 
				try{
					listeners.add(serverSocket.accept()); //try to connect client here  
					clients.add(new ClientHandler(listeners.get(listeners.size()-1), serverSocket.accept()));
					Thread client = new Thread (clients.get(clients.size()-1));			//gives each client their own thread 
					client.start(); 													//starts said thread 
				}catch(IOException e){
					System.err.println("Can't Accept connection attempt");				//if there's an IO error, print this 
				}
			}	
		}
		public ClientHandler getClient(String x){
			for ( ClientHandler y : clients) 
				if (y.Name.equalsIgnoreCase(x))
					return y;
			return null; 
		}
		public Socket getSocket(String x){
			for ( ClientHandler y : clients) 
				if (y.Name.equalsIgnoreCase(x))
					return y.listener;
			return null; 
		}
	}
	/**
	 * Create the application.
	 */
	public GoIPDM() {
		initialize();
	}
	public static String roll(String[] banana){
		try {
			String results = "";
			int sum = 0; 
			int D = 20; 
			int dnum = 1;

			for (int x = 1; x < banana.length; x++) 
			{
				if (banana[x].contains("d"))
				{
					if (banana[x].split("d")[0]!=null && !banana[x].split("d")[0].equals(""))
						dnum = (int)Integer.parseInt(banana[x].split("d")[0]); 
					try {
						D = (int)Integer.parseInt(banana[x].split("d")[1]); 
					} catch (Exception e){
						D = 20;
					}

				} else {
					if (banana[x]!=null && !banana[x].equals(""))
						dnum = (int)Integer.parseInt(banana[x]); 
					if (banana[x+1]!=null && !banana[x+1].equals(""))
						D = (int)Integer.parseInt(banana[x+1]); 
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
			return " rolled a "+sum +": "+results;
		} catch (Exception e) { 
			return "Error Rolling"; //System.out.println(encrypt(Message)e.getMessage());	
		}
	}
	static String makeString(String[] arr){
		StringBuilder builder = new StringBuilder();
		for(String s : arr) {
			builder.append(s+" ");
		}
		return builder.toString();
	}
	// this one is for the DM to broadcast
	public static void broadcast(String[] params)
	{
		try { 
			String Message = makeString(params);
			Message = Message.substring(2);
			for (Socket x : ClientConnecter.listeners) 
				(new PrintWriter(x.getOutputStream(), true)).println(encrypt(Message));
		} catch (Exception e){}
	}
	// this is a player broadcasting
	public static void broadcast(String[] params, ClientHandler that)
	{
		try { 
			String Message = makeString(params);
			Message = Message.substring(2);
			for (Socket x : ClientConnecter.listeners) 
				if (x!=that.listener)
					(new PrintWriter(x.getOutputStream(), true)).println(encrypt(that.Name +": "+ Message));
		} catch (Exception e){}
	}
	// refreshes the player list
	public static void refresh(){
		String newplayerlist = "";
		for (ClientHandler x : clientListener.getClients())
			newplayerlist += x.Name + "\n";
		listPlayers.setText(newplayerlist);
	}
	public static void kick(ClientHandler x){
		
		x.out.close();
		try{
		if(!x.listener.isClosed())
			x.listener.close(); 
		} catch (Exception e){} // should never fail; only attempts to close if it's not closed.
		clientListener.removeClient(x);
		refresh(); 
		clientListener.sendList(); 
	}
	//looks for commands in the server-side chat
	public void Decipher (String outter) {
		try {
			//it's a banana split! Well I thought it was funny. 
			String[] banana = outter.split(" ");
			if (banana[0].equalsIgnoreCase("cls")){
				chatArea.setText(chatArea.getText().split("\n")[chatArea.getText().split("\n").length-1]+"\n");
			} else if (banana[0].equalsIgnoreCase("kick")){
				ClientHandler noob = clientListener.getClient(banana[1]);
				kick(noob);//kicking target player
			} else if (banana[0].equalsIgnoreCase("Info")){
				for (ClientHandler x : clientListener.getClients())
					if (x.Name.equalsIgnoreCase(banana[1]))
						chatArea.append(x.IP+"__"+x.Name);
			} else if (banana[0].equalsIgnoreCase("refresh")){
				refresh();
			} else if (banana[0].equalsIgnoreCase("msg")){
				Message(clientListener, outter); 
				chatArea.append(inputLine.getText());
			} else if (banana[0].equalsIgnoreCase("help")) {
				chatArea.append(help()+"\n");
			} else if (banana[0].equalsIgnoreCase("lt") || banana[0].equalsIgnoreCase("loot")) {
				// example syntax of command: Loot player1 player2:Item1 Item2, ALL: item3
				String mani = "";
				for (int i = 1; i <banana.length; i ++)  // cuts off leading command (loot or lt)
					mani += banana[i]+" "; 
				String[] params = mani.split(","); 
				for (String param : params)
				{
					String[] temper = param.split(":");
					String[] users = temper[0].split(" "); 
					for (String user : users) {
						user = user.trim();
						if (user == null || user.equals("") || user.equals(" "))
							continue; 
						if (user.equalsIgnoreCase("all"))
							for (Socket x : ClientConnecter.listeners) 
								(new PrintWriter(x.getOutputStream(), true)).println("Loot_ "+temper[1]);
						else 
							Message(clientListener, "msg "+user+" Loot_ "+temper[1]); 
					}
				}
			} else if (banana[0].equalsIgnoreCase("bc")) {
				broadcast(banana);
				chatArea.append(inputLine.getText()+"\n");
			} else if (banana[0].equalsIgnoreCase("showroll") || banana[0].equalsIgnoreCase("sr")) {
				String result = roll(banana);
				chatArea.append("You"+result+"\n");
				broadcast(("bc DM"+result).split(" "));
			} else if (banana[0].equalsIgnoreCase("roll") || banana[0].equalsIgnoreCase("r")) {
				chatArea.append("You" +roll(banana)+"\n");
			} else { 
				chatArea.append(outter+"\n");
			}
		} catch (Exception e) {}
	}
	//an individual client's thread and associated objects/methods
	public static class ClientHandler implements Runnable { 
		private Socket listener; 
		private Socket playerListSocket; 
		public String Name; 
		private String IP;
		private PrintWriter PlayerListWriter; 
		private PrintWriter out; 
		public ClientHandler () {} // Just for convention's sake 
		// method to test a string to see if it has an integer interpretation 
		public ClientHandler (Socket temp) {  // no player list, just a socket for text communications
			this.listener= temp;  
			this.Name = this.listener.getInetAddress().toString().replace("/","");
			this.IP = this.Name;
		}
		public ClientHandler (Socket temp, Socket temp2) {  // Constructor with player list enabled
			this.listener= temp;  
			this.Name = this.listener.getInetAddress().toString().replace("/","");
			this.IP = this.Name;
			this.playerListSocket = temp2; 
		}

		public void run () { 
			try{	

				out = new PrintWriter(listener.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(listener.getInputStream()));
				PlayerListWriter = new PrintWriter(playerListSocket.getOutputStream(), true);
				String inLine;
				out.println(encrypt("Connected!"));
				out.println(encrypt("Please change your username with setname [username]"));
				out.println(encrypt("You may also use roll xdy to roll x number of y sided dice!"));
				out.println(encrypt("msg [username] to message a player."));
				clientListener.sendList();
				chatArea.append(this.Name + " has connected."+"\n");
				refresh();
				while((inLine = input.readLine()) != null){
					//first things first, decrypt the message
					inLine = decrypt(inLine);
					String[] params = inLine.split(" "); 
					// if they didn't make a roll, say what they typed
					if (!params[0].equalsIgnoreCase("roll")&& !params[0].equalsIgnoreCase("r"))
						chatArea.setText(chatArea.getText() + " "+ Name+": " + inLine+"\n");
					//begin looking for command phrases.
					if(params[0].equalsIgnoreCase("exit")){
						input.close();
						listener.close();
					} else { 			
						if (params[0].equalsIgnoreCase("ping")){
							Date date = new Date();
							SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss.SSSS");
							String formattedDate = sdf.format(date);
							out.println(encrypt(formattedDate)); // 12/01/2011 4:48:16 PM
						} else if (params[0] .equalsIgnoreCase("roll") || params[0] .equalsIgnoreCase("r")) {
							String result = roll(params);
							out.println(encrypt("You "+result)); 
							chatArea.append(Name+result + "\n");
						} else if (params[0].equalsIgnoreCase("bc")) {
							broadcast(params,this);
							chatArea.append(inputLine.getText()+"\n");
						}
						else if (params[0].equalsIgnoreCase("msg"))
						{
							String cheese = params[0]+" "+params[1]+" "+Name+": ";
							for (int i = 2; i < params.length; i++)
								cheese+= params[i]+" ";
							Message(GoIPDM.clientListener, cheese); 
							chatArea.append(inputLine.getText()+"\n");
						} 
						else if (params[0].equalsIgnoreCase("setname")) {
							String newName = params[1]; 
							//enforces unique names. 
							boolean taken = false; 
							for (ClientHandler x : clientListener.getClients())
								if (x.Name.equalsIgnoreCase(newName))
								{
									out.println(encrypt("Could not change name: Username taken."));
									taken = true; 
								}
							if (!taken){ 
								chatArea.append(Name); 
								Name = newName; 
								out.println(encrypt("Name changed to " + params[1]));
								String newplayerlist = "";
								for (ClientHandler x : clientListener.getClients())
									newplayerlist += x.Name + "\n";
								String[] temperpedic = newplayerlist.split("\n");
								newplayerlist="";
								for (String y : temperpedic)
									newplayerlist +=y+"\n";
								listPlayers.setText(newplayerlist);
								clientListener.sendList();
								chatArea.append(" name changed to "+Name+"\n"); 
							}
						} else if (params[0].equalsIgnoreCase("reset")) {
							kick(this); // a "reset" command is effectively the same as kicking yourself. 
						} else if (params[0].equalsIgnoreCase("help")) {
							out.println(encrypt("Roll x y - Roll x number of dice each with y sides. You can specify 2 or more types of dice as so: Roll a b y z where a and y are number of dice and b/z are number of sides per dice. A 1d6 + 2d4 attack would be Roll 1 6 2 4. Use setname (name) to change your username. "));
						} else
							out.println(" "); // signals that the message was recieved and allows the client to send another message if necessary -- handshaking really. 
					}
				}
			}catch(IOException e){
				chatArea.append(Name + " has disconnected. \n");
				out.close();
				clientListener.removeClient(this);
				refresh();
				clientListener.sendList(); 
			}
		}
	}
	//standard GUI creation method
	@SuppressWarnings("static-access")
	private void initialize() {
		frmGoipDmServer = new JFrame();
		try { 					// try to find my IP via amazon's service. Should that service be down or inaccessible...
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine(); //you get the IP as a String
			frmGoipDmServer.setTitle("GoIP DM - Ext:"+ ip + " - LAN:" + ClientConnecter.serverSocket.getInetAddress().getLocalHost().toString().split("/")[1]);
		} catch (Exception e){ 
			try {				// Try to find my local IP address at least and show it in the title bar. 
				frmGoipDmServer.setTitle("GoIP DM - LAN:"+ ClientConnecter.serverSocket.getInetAddress().getLocalHost().toString().split("/")[1]);
			} catch (Exception g){
				//There's really no reason that should not work. 
				frmGoipDmServer.setTitle("GoIP DM - Could not find local or external IP. That's some weird shit."); 
			}
		}		
		frmGoipDmServer.setResizable(false);
		frmGoipDmServer.setBounds(100, 100, 469, 238);
		frmGoipDmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGoipDmServer.getContentPane().setLayout(null);		

		chatArea = new JTextArea();
		chatArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		chatArea.setBounds(10, 11, 349, 173);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(chatArea);
		scrollPane.setBounds(10, 11, 349, 173);
		scrollPane.setHorizontalScrollBarPolicy(scrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frmGoipDmServer.getContentPane().add(scrollPane);
		frmGoipDmServer.getContentPane().add(scrollPane);

		listPlayers = new JTextArea();
		listPlayers.setTabSize(3);
		listPlayers.setEditable(false);
		listPlayers.setBounds(369, 34, 91, 141);
		JScrollPane scrollPane2 = new JScrollPane(listPlayers);
		scrollPane2.setBounds(369, 34, 91, 141);
		frmGoipDmServer.getContentPane().add(scrollPane2);

		final JLabel lblPlayers = new JLabel("Players List");
		lblPlayers.setBounds(369, 17, 97, 14);
		frmGoipDmServer.getContentPane().add(lblPlayers);

		inputLine = new JTextField();
		inputLine.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if(!inputLine.getText().equals(null) && !inputLine.getText().equals(""))
						Decipher(inputLine.getText());	
					inputLine.setText("");
				}	

			}
		});
		inputLine.setBounds(10, 187, 349, 20);
		frmGoipDmServer.getContentPane().add(inputLine);
		inputLine.setColumns(10);

		final JButton btnRoll = new JButton("Roll d20");
		btnRoll.setToolTipText("Do it. I dare you.");
		btnRoll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatArea.append("You"+ GoIPDM.roll("roll".split(" "))+"\n");
			}
		});
		btnRoll.setBounds(369, 186, 91, 23);
		frmGoipDmServer.getContentPane().add(btnRoll);
		chatArea.setText("Quest has begun! Listening for players."+"\n");
	}
	/*
		Note: this encryption is *not* meant to uphold to any real scrutiny. Rather, it's meant to deter passive searching. 
		Most DnD campaigns talk about doing illicit, graphic, or gore intensive activities. This is meant to prevent a "passerby" 
		from "overhearing" this *fictional* conversation, mistaking it for a real threat, and acting on it. Other than that, 
		it was a fun method to write. 
	 */
	public static String encrypt(String start){
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
			while ((temp[i]+Character.getNumericValue(key)) < 32 || (temp[i]+Character.getNumericValue(key))>126)
				key = alphabet[(int)Math.floor(Math.random()*alphabet.length)];
			result+= Character.toString((char)(temp[i]+Character.getNumericValue(key)));
			result+= key;
		}
		//System.out.println(result);
		return result; 
	}
	public static String decrypt(String start){
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
}
