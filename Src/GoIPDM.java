import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;

import java.awt.Component;

import javax.swing.JScrollPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import javax.swing.JDesktopPane;

import java.awt.Font;

public class GoIPDM {
	public static JFrame frame;
	public static JTextField inputLine;
	public static JTextArea chatArea;
	public static JTextArea listPlayers;
	public static ClientConnecter clientListener = new ClientConnecter();

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				new GoIPDM();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});
		Thread heh = new Thread(clientListener);
		heh.start(); // begin listening for clients
	}

	public static String help() { // the internal "readme" for the DM. Can't
									// send to player as it contains line
									// returns, and that screws up everything.

		String message = "Action - syntax - explanation\n";
		message += "Broadcasting - bc [message] - sends a message to all players.\n";
		message += "Message - msg (player) [message] - sends a message to a single player. \n";
		message += "Dice rolling - \nroll (by itself) = 1d20 roll\nroll d# = roll 1 dice of # sides.\nroll #d = roll # dice of 20 sides.\nroll #d# = roll # dice of # sides.\nroll # # = roll # dice of # sides.\n";
		message += "Loot Distribution - \nLoot [group of players space delimited]:[group of items],[group of players space delimited]:[group of items]... \n";
		message += "Example: 'Loot Justin Connor:1gp sword of smite, All:2 silver'\n the above would give justin and connor a sword of smite and 1 gp,\n then everyone gets an additional 2 silver\n";
		return message;
	}

	// given a connection to the clientConnector and a message, send that
	// message to
	public static void Message(ClientConnecter clients, String outter) {
		StringBuilder Message = new StringBuilder("");
		String[] params = outter.split(" ");
		try {
			// locates the socket of the intended recipient.
			ClientHandler x = clients.getClient(params[1]);
			// Easiest way to trim the user name and "msg" from the message.
			for (int i = 2; i < params.length; i++)
				Message.append(params[i] + " ");
			// send message down the socket's output stream. Probably should
			// find a way to access the variable associated with this.
			x.out.println(Encryption.superEncrypt(Message.toString()));
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

		public ArrayList<ClientHandler> getClients() {
			return clients;
		}

		public boolean checkIPs(String IP) {
			// makes sure each client is only connected once.
			return clients.stream().filter(x -> IP.equals(x.IP)).count() > 1;

		}

		public void removeClient(ClientHandler x) {
			clients.remove(x);
		}

		public void sendList() {
			StringBuilder temp = new StringBuilder("");
			clients.stream().forEach(x -> temp.append(x.Name + "|"));

			clients.stream().forEach(
					x -> x.PlayerListWriter.println(Encryption.superEncrypt(temp
							.toString())));
		}

		public ClientConnecter() {
			try {
				serverSocket = new ServerSocket(1813);
			} catch (IOException e) {
				System.err.println("Can't listen on Port 1813");
				System.exit(1337);
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					listeners.add(serverSocket.accept()); // try to connect
															// client here
					clients.add(new ClientHandler(listeners.get(listeners
							.size() - 1), serverSocket.accept()));

					// Each client gets their own thread.
					Thread client = new Thread(clients.get(clients.size() - 1));
					client.start(); // starts said thread
				} catch (IOException e) {
					System.err.println("Can't Accept connection attempt");
				}
			}
		}

		public ClientHandler getClient(String x) {
			return clients.stream().filter(y -> y.Name.equalsIgnoreCase(x))
					.findFirst().orElse(null);
		}

		public Socket getSocket(String x) {

			return clients.stream().filter(y -> y.Name.equalsIgnoreCase(x))
					.findFirst().map(y -> y.listener).orElse(null);

		}
	}

	/**
	 * Create the application.
	 */
	public GoIPDM() {
		initialize();
	}

	// this one is for the DM to broadcast
	public static void broadcast(String[] params) {

		final String Message = String.join(" ", params).substring(2);

		ClientConnecter.listeners.stream().forEach(
				x -> {
					try {
						(new PrintWriter(x.getOutputStream(), true))
								.println(Encryption.superEncrypt("DM: " + Message));
					} catch (Exception e) {
					}
				});
	}

	// this is a player broadcasting
	public static void broadcast(String[] params, ClientHandler that) {

		final String Message = String.join(" ", params).substring(2);

		ClientConnecter.listeners
				.stream()
				.filter(x -> x != that.listener)
				.forEach(
						x -> {
							try {

								(new PrintWriter(x.getOutputStream(), true))
										.println(Encryption.superEncrypt(that.Name
												+ ": " + Message));
							} catch (Exception e) {

							}
						});

	}

	// refreshes the player list
	public static void refresh() {
		StringBuilder newplayerlist = new StringBuilder("");

		clientListener.getClients().stream()
				.forEach(x -> newplayerlist.append(x.Name + "\n"));

		listPlayers.setText(newplayerlist.toString());
	}

	public static void kick(ClientHandler x) {

		x.out.close();
		try {
			if (!x.listener.isClosed())
				x.listener.close();
		} catch (Exception e) {
		} // should never fail; only attempts to close if it's not closed.
		clientListener.removeClient(x);
		refresh();
		clientListener.sendList();
	}

	// looks for commands in the server-side chat
	public void Decipher(String outter) {
		try {
			// it's a banana split! Well I thought it was funny.
			String[] banana = outter.split(" ");
			if (banana[0].equalsIgnoreCase("cls")) {
				chatArea.setText(chatArea.getText().split("\n")[chatArea
						.getText().split("\n").length - 1] + "\n");
			} else if (banana[0].equalsIgnoreCase("kick")) {
				ClientHandler noob = clientListener.getClient(banana[1]);
				kick(noob);// kicking target player
			} else if (banana[0].equalsIgnoreCase("Info")) {
				clientListener.getClients().stream()
						.filter(x -> x.Name.equalsIgnoreCase(banana[1]))
						.forEach(x -> chatArea.append(x.IP + "__" + x.Name));
				;
			} else if (banana[0].equalsIgnoreCase("refresh")) {
				refresh();
			} else if (banana[0].equalsIgnoreCase("msg")) {
				Message(clientListener, outter);
				chatArea.append(inputLine.getText());
			} else if (banana[0].equalsIgnoreCase("statroll")) {
				DiceRoll.statroll();
			} else if (banana[0].equalsIgnoreCase("help")) {
				chatArea.append(help() + "\n");
			} else if (banana[0].equalsIgnoreCase("lt")
					|| banana[0].equalsIgnoreCase("loot")) {
				// example syntax of command: Loot player1 player2:Item1 Item2,
				// ALL: item3
				String mani = "";
				for (int i = 1; i < banana.length; i++)
					// cuts off leading command (loot or lt)
					mani += banana[i] + " ";
				String[] params = mani.split(",");
				for (String param : params) {
					String[] temper = param.split(":");
					String[] users = temper[0].split(" ");
					Arrays.stream(users)
							.map(user -> user.trim())
							.filter(user -> user != null && !user.equals(""))
							.forEach(
									user -> {
										user = user.trim();

										if (user.equalsIgnoreCase("all")) {

											ClientConnecter.listeners
													.stream()
													.forEach(
															x -> {
																try {
																	(new PrintWriter(
																			x.getOutputStream(),
																			true))
																			.println("Loot_ "
																					+ temper[1]);
																} catch (Exception e) {
																}
															});

										} else {
											Message(clientListener, "msg "
													+ user + " Loot_ "
													+ temper[1]);
										}
									});
				}
			} else if (banana[0].equalsIgnoreCase("bc")) {
				broadcast(banana);
				chatArea.append(inputLine.getText() + "\n");
			} else if (banana[0].equalsIgnoreCase("showroll")
					|| banana[0].equalsIgnoreCase("sr")) {
				String result = DiceRoll.roll(banana);
				chatArea.append("You" + result + "\n");
				broadcast(("bc DM" + result).split(" "));
			} else if (banana[0].equalsIgnoreCase("roll")
					|| banana[0].equalsIgnoreCase("r")) {
				chatArea.append("You" + DiceRoll.roll(banana) + "\n");
			} else {
				chatArea.append(outter + "\n");
			}
		} catch (Exception e) {
		}
	}

	// an individual client's thread and associated objects/methods
	public static class ClientHandler implements Runnable {
		private Socket listener;
		private Socket playerListSocket;
		public String Name;
		private String IP;
		private PrintWriter PlayerListWriter;
		private PrintWriter out;

		public ClientHandler() {
		} // Just for convention's sake

		// method to test a string to see if it has an integer interpretation
		public ClientHandler(Socket temp) { // no player list, just a socket for
											// text communications
			this.listener = temp;
			this.Name = this.listener.getInetAddress().toString()
					.replace("/", "");
			this.IP = this.Name;
		}

		public ClientHandler(Socket temp, Socket temp2) { // Constructor with
															// player list
															// enabled
			this.listener = temp;
			this.Name = this.listener.getInetAddress().toString()
					.replace("/", "");
			this.IP = this.Name;
			this.playerListSocket = temp2;
		}

		@Override
		public void run() {
			try {

				out = new PrintWriter(listener.getOutputStream(), true);
				if (clientListener.checkIPs(this.IP))
					throw new Exception("Duplicate LAN connection.");
				BufferedReader input = new BufferedReader(
						new InputStreamReader(listener.getInputStream()));
				PlayerListWriter = new PrintWriter(
						playerListSocket.getOutputStream(), true);
				String inLine;
				out.println(Encryption.superEncrypt("Connected!"));
				out.println(Encryption
						.superEncrypt("Please change your username with setname [username]"));
				out.println(Encryption
						.superEncrypt("You may also use roll xdy to roll x number of y sided dice!"));
				out.println(Encryption
						.superEncrypt("msg [username] to message a player."));
				clientListener.sendList();
				chatArea.append(this.Name + " has connected." + "\n");
				refresh();

				while ((inLine = input.readLine()) != null) {
					// first things first, superDecrypt the message
					inLine = Encryption.superDecrypt(inLine);
					String[] params = inLine.split(" ");
					// if they didn't make a roll, say what they typed
					if (!params[0].equalsIgnoreCase("roll")
							&& !params[0].equalsIgnoreCase("r"))
						chatArea.setText(chatArea.getText() + " " + Name + ": "
								+ inLine + "\n");
					// begin looking for command phrases.
					if (params[0].equalsIgnoreCase("exit")) {
						input.close();
						listener.close();
					} else {
						if (params[0].equalsIgnoreCase("ping")) {
							Date date = new Date();
							SimpleDateFormat sdf = new SimpleDateFormat(
									"h:mm:ss.SSSS");
							String formattedDate = sdf.format(date);
							out.println(Encryption.superEncrypt(formattedDate)); // 12/01/2011
							// 4:48:16
							// PM
						} else if (params[0].equalsIgnoreCase("roll")
								|| params[0].equalsIgnoreCase("r")) {
							String result = DiceRoll.roll(params);
							out.println(Encryption.superEncrypt("You " + result));
							chatArea.append(Name + result + "\n");
						} else if (params[0].equalsIgnoreCase("bc")) {
							broadcast(params, this);
							chatArea.append(inputLine.getText() + "\n");
						} else if (params[0].equalsIgnoreCase("msg")) {
							String cheese = params[0] + " " + params[1] + " "
									+ Name + ": ";
							for (int i = 2; i < params.length; i++)
								cheese += params[i] + " ";
							Message(GoIPDM.clientListener, cheese);
							chatArea.append(inputLine.getText() + "\n");
						} else if (params[0].equalsIgnoreCase("setname")) {
							String newName = params[1];
							// enforces unique names.

							long sharedNames = clientListener
									.getClients()
									.stream()
									.filter(tempClient -> tempClient.Name
											.equalsIgnoreCase(newName)).count();
							if (sharedNames > 0) {
								out.println(Encryption
										.superEncrypt("Could not change name: Username taken."));

							} else {
								chatArea.append(Name);
								Name = newName;
								out.println(Encryption
										.superEncrypt("Name changed to " + params[1]));
								StringBuilder newplayerlist = new StringBuilder(
										"");
								clientListener
										.getClients()
										.stream()
										.forEach(
												t -> newplayerlist
														.append(t.Name + "\n"));

								listPlayers.setText(newplayerlist.toString());
								clientListener.sendList();
								chatArea.append(" name changed to " + Name
										+ "\n");
							}
						} else if (params[0].equalsIgnoreCase("reset")) {
							kick(this); // a "reset" command is effectively the
										// same as kicking yourself.
						} else if (params[0].equalsIgnoreCase("help")) {
							out.println(Encryption
									.superEncrypt("Roll x y - Roll x number of dice each with y sides. You can specify 2 or more types of dice as so: Roll a b y z where a and y are number of dice and b/z are number of sides per dice. A 1d6 + 2d4 attack would be Roll 1 6 2 4. Use setname (name) to change your username. "));
						} else
							out.println(" "); // signals that the message was
												// recieved and allows the
												// client to send another
												// message if necessary --
												// handshaking really.
					}
				}
			} catch (IOException e) {
				chatArea.append(Name + " has disconnected. \n");
				out.close();
				clientListener.removeClient(this);
				refresh();
				clientListener.sendList();

			} catch (Exception e) {
				out.println(Encryption
						.superEncrypt("Only one connection is allowed per computer over a LAN."));
				out.close();
				chatArea.append(Name + " has disconnected. \n--"
						+ e.getMessage() + "--");
				clientListener.removeClient(this);
				refresh();
				clientListener.sendList();
			}
		}
	}

	// standard GUI creation method
	@SuppressWarnings("static-access")
	private void initialize() {
		frame = new JFrame();
		try { // try to find my IP via amazon's service. Should that service be
				// down or inaccessible...
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));
			String ip = in.readLine(); // you get the IP as a String
			frame.setTitle("GoIP DM - Ext:"
					+ ip
					+ " - LAN:"
					+ ClientConnecter.serverSocket.getInetAddress()
							.getLocalHost().toString().split("/")[1]);
		} catch (Exception e) {
			try { // Try to find my local IP address at least and show it in the
					// title bar.
				frame.setTitle("GoIP DM - LAN:"
						+ ClientConnecter.serverSocket.getInetAddress()
								.getLocalHost().toString().split("/")[1]);
			} catch (Exception g) {
				// There's really no reason that should not work... unless they
				// lack any sort of LAN connection.
				frame.setTitle("GoIP DM - Could not find local or external IP. That's some weird shit.");
			}
		}
		frame.setResizable(false);
		frame.setBounds(100, 100, 542, 302);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 409, 222);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);
		frame.getContentPane().add(scrollPane);

		chatArea = new JTextArea();
		scrollPane.setViewportView(chatArea);
		chatArea.setFocusable(false);
		chatArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		chatArea.setText("Quest has begun! Listening for players." + "\n");

		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setBounds(429, 24, 97, 209);
		frame.getContentPane().add(scrollPane2);

		listPlayers = new JTextArea();
		listPlayers.setLineWrap(true);
		listPlayers.setFocusable(false);
		scrollPane2.setViewportView(listPlayers);
		listPlayers.setTabSize(3);
		listPlayers.setEditable(false);

		final JLabel lblPlayers = new JLabel("Players List");
		lblPlayers.setFont(new Font("Palatino Linotype", Font.BOLD, 12));
		lblPlayers.setBounds(429, 11, 91, 14);
		frame.getContentPane().add(lblPlayers);

		inputLine = new JTextField();
		inputLine.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!inputLine.getText().equals(null)
							&& !inputLine.getText().equals(""))
						Decipher(inputLine.getText());
					inputLine.setText("");
				}

			}
		});
		inputLine.setBounds(10, 244, 409, 22);
		frame.getContentPane().add(inputLine);
		inputLine.setColumns(10);

		final JButton btnRoll = new JButton("Dice Bag");
		btnRoll.setFocusable(false);
		btnRoll.addActionListener(e -> {
			final DiceBag db = new DiceBag();
			db.btnRoll.addActionListener(y -> {
				String temp = "roll " + db.d100.getText().trim() + "d100 "
						+ db.d20.getText().trim() + "d20 "
						+ db.d12.getText().trim() + "d12 "
						+ db.d10.getText().trim() + "d10 "
						+ db.d8.getText().trim() + "d8 "
						+ db.d6.getText().trim() + "d6 "
						+ db.d4.getText().trim() + "d4";
				if (!db.dc.getText().trim().equals(""))
					temp += " " + db.cd.getText().trim() + "d"
							+ db.dc.getText().trim();
				if (!db.add.getText().trim().equals(""))
					temp += " +" + db.add.getText().trim();
				chatArea.append("You" + DiceRoll.roll(temp.split(" ")) + "\n");
			});
			db.statbutt.addActionListener(x -> chatArea.append(DiceRoll
					.statroll()));
			db.setVisible(true);
		});
		btnRoll.setBounds(429, 244, 97, 23);
		frame.getContentPane().add(btnRoll);

		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBounds(160, 21, 1, -21);
		frame.getContentPane().add(desktopPane);
	}

}
