package goip;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import dice.DiceBag;
import dice.DiceRoll;

import javax.swing.JDialog;
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

import Encryption.DecryptedWriter;
import Encryption.EncryptedReader;

public final class GoIPDM {

	// GUI variables
	private final static JFrame frame = new JFrame();
	private final static JTextField inputLine = new JTextField();
	private final static JTextArea chatArea = new JTextArea();
	private final static JTextArea listPlayers = new JTextArea();
	private final static JLabel lblPlayers = new JLabel("Players List");
	private final static JScrollPane scrollPane = new JScrollPane();
	private final static JScrollPane scrollPane2 = new JScrollPane();
	private final static JButton btnRoll = new JButton("Dice Bag");
	// Communication variables
	private static ClientConnecter clientListener;

	public static void main(String[] args) throws Exception {
		try {
			clientListener = new ClientConnecter();
			EventQueue.invokeLater(() -> {
				try {
					new GoIPDM();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

			});
			// begin listening for clients
			clientListener.start();
		} catch (IOException e) {

			JDialog jd = new JDialog();
			jd.setBounds(200, 200, 250, 150);
			jd.setTitle("Error Establishing Port");
			jd.setLocationRelativeTo(null);
			jd.add(new JLabel("GoIP DM already running?"), BorderLayout.CENTER);
			jd.setVisible(true);
			jd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			throw e;
		}

	}

	public static String help() { // the internal "readme" for the DM. Can't
		// send to player as it contains line
		// returns, and that screws up everything.

		String message = "Action - syntax - explanation\n"
				+ "Broadcasting - bc [message] - sends a message to all players.\n"
				+ "Message - msg (player) [message] - sends a message to a single player. \n"
				+ "Dice rolling - \nroll (by itself) = 1d20 roll\nroll d# = roll 1 dice of # sides.\nroll #d = roll # dice of 20 sides.\nroll #d# = roll # dice of # sides.\nroll # # = roll # dice of # sides.\n"
				+ "Loot Distribution - \nLoot [group of players space delimited]:[group of items],[group of players space delimited]:[group of items]... \n"
				+ "Example: 'Loot Justin Connor:1gp sword of smite, All:2 silver'\n the above would give justin and connor a sword of smite and 1 gp,\n then everyone gets an additional 2 silver\n";
		return message;
	}

	// given a connection to the clientConnector and a message, send that
	// message to
	public static void Message(ClientConnecter clients, String outter) {
		final StringBuilder Message = new StringBuilder("");
		String[] params = outter.split(" ");
		if (params[1].equalsIgnoreCase("DM"))
			return;

		try {
			// locates the socket of the intended recipient.
			ClientHandler x = clients.getClient(params[1]);
			// Easiest way to trim the user name and "msg" from the message.
			for (int i = 2; i < params.length; i++)
				Message.append(params[i] + " ");
			// send message down the socket's output stream. Probably should
			// find a way to access the variable associated with this.
			x.out.println(Message.toString());
		} catch (Exception e) {
			chatArea.append("Player not found: " + params[1]);

		}
	}

	// connects clients and keeps an ArrayList of all connected clients.
	// Great for broadcasting, messaging between active clients.
	static class ClientConnecter extends Thread {
		private static ServerSocket serverSocket = null;
		private final static ArrayList<Socket> listeners = new ArrayList<Socket>();
		private final static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

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
			final StringBuilder temp = new StringBuilder("");
			clients.stream().forEach(x -> temp.append(x.Name + "|"));

			clients.stream().forEach(
					x -> x.PlayerListWriter.println(temp.toString()));
		}

		public ClientConnecter() throws IOException {
			serverSocket = new ServerSocket(1813);
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
					clients.get(clients.size() - 1).start();

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
		try {
			ClientConnecter.clients.stream()
					.filter(x -> x.listener.isConnected())
					.filter(x -> !x.listener.isClosed())
					.forEach(x -> x.out.println("DM: " + Message));
		} catch (Exception e) {
		}
	}

	public void broadcast(String message) {
		ClientConnecter.clients.stream().filter(x -> x.listener.isBound())
				.filter(x -> x.listener.isConnected())
				.filter(x -> x.listener.isConnected())
				.forEach(x -> x.out.println("DM: " + message));
	}

	// refreshes the player list
	public static void refresh() {
		final StringBuilder newplayerlist = new StringBuilder("");

		clientListener.getClients().stream()
				.forEach(x -> newplayerlist.append(x.Name + "\n"));

		listPlayers.setText(newplayerlist.toString());
	}

	public static void kick(ClientHandler x) {
		x.out.println("You have been disconnected.");
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

		// it's a banana split! Well I thought it was funny.
		String[] banana = outter.split(" ");
		switch (banana[0].toLowerCase()) {
		case "checkip":
			chatArea.append("Looking for external IP...\n");
			new Thread(
					() -> {
						try { // try to find my IP via amazon's service.
							URL whatismyip = new URL(
									"http://checkip.amazonaws.com");
							BufferedReader in = new BufferedReader(
									new InputStreamReader(whatismyip
											.openStream()));
							String ip = in.readLine(); // you get the IP as a
														// String
							ClientConnecter.serverSocket.getInetAddress();
							frame.setTitle("GoIP DM - Ext:"
									+ ip
									+ " - LAN:"
									+ InetAddress.getLocalHost().toString()
											.split("/")[1]);
							in.close();
							chatArea.append("IP: " + ip + "\n");
						} catch (Exception e) {
							chatArea.append("Could not find IP.\n");
						}
					}).start();
			;
			break;
		case "cls":
			chatArea.setText(chatArea.getText().split("\n")[chatArea.getText()
					.split("\n").length - 1] + "\n");
			break;
		case "kick":
			ClientHandler noob = clientListener.getClient(banana[1]);
			kick(noob);// kicking target player
			break;
		case "info":
			clientListener.getClients().stream()
					.filter(x -> x.Name.equalsIgnoreCase(banana[1]))
					.forEach(x -> chatArea.append(x.IP + "__" + x.Name));
			break;
		case "refresh":
			refresh();
			break;
		case "msg":
			Message(clientListener, outter);
			chatArea.append(outter);
			break;
		case "statroll":
			DiceRoll.statroll();
			break;
		case "help":
			chatArea.append(help() + "\n");
			break;
		case "lt":
		case "loot":
			// example syntax of command: Loot player1 player2:Item1 Item2,
			// ALL: item3
			final StringBuilder mani = new StringBuilder("");
			for (int i = 1; i < banana.length; i++)
				// cuts off leading command (loot or lt)
				mani.append(banana[i] + " ");
			String[] params = mani.toString().split(",");
			// for (String param : params) {
			Arrays.stream(params)
					.forEach(
							param -> {
								String[] temper = param.split(":");
								String[] users = temper[0].split(" ");
								Arrays.stream(users)
										.map(user -> user.trim())
										.filter(user -> user != null
												&& !user.equals(""))
										.forEach(
												user -> {
													if (user.equalsIgnoreCase("all")) {
														ClientConnecter.clients
																.stream()
																.forEach(
																		x -> x.out
																				.print("Loot_ "
																						+ temper[1]));

													} else {
														Message(clientListener,
																"msg "
																		+ user
																		+ " Loot_ "
																		+ temper[1]);
													}
												});
							});

		case "showroll":
		case "sr":
			String result = DiceRoll.roll(banana);
			chatArea.append("You" + result + "\n");
			broadcast(("bc DM" + result).split(" "));
			break;
		case "roll":
		case "r":
			chatArea.append("You" + DiceRoll.roll(banana) + "\n");
			break;
		case "bc":
		default:
			broadcast(outter);
			chatArea.append("DM: "+inputLine.getText().replaceFirst("bc", "") + "\n");
			break;
		}

	}

	// an individual client's thread and associated objects/methods
	public static class ClientHandler extends Thread {
		private final Socket listener;
		private final Socket playerListSocket;
		public String Name;
		private String IP;
		private final DecryptedWriter PlayerListWriter;
		private final DecryptedWriter out;

		public DecryptedWriter getTextWriter() {
			return out;
		}

		public ClientHandler(Socket temp, Socket temp2) throws IOException { // Constructor
																				// with
			// player list
			// enabled
			this.listener = temp;
			out = new DecryptedWriter(listener.getOutputStream(), true);

			this.Name = this.listener.getInetAddress().toString()
					.replace("/", "");
			this.IP = this.Name;
			this.playerListSocket = temp2;
			PlayerListWriter = new DecryptedWriter(
					playerListSocket.getOutputStream(), true);
		}

		// player broadcasting without split string
		public void broadcast(String message) {
			ClientConnecter.clients.stream().filter(x -> x.listener.isBound())
					.filter(x -> x.listener.isConnected())
					.forEach(x -> x.out.println(this.Name + ": " + message));
		}

		@Override
		public void run() {
			try {

				if (clientListener.checkIPs(this.IP)
						&& !this.IP.equals("127.0.0.1"))
					throw new Exception("Duplicate LAN connection.");
				EncryptedReader input = new EncryptedReader(
						new InputStreamReader(listener.getInputStream()));

				String inLine;
				out.println("Connected!");
				out.println("Please change your username with setname [username]");
				out.println("You may also use roll xdy to roll x number of y sided dice!");
				out.println("msg [username] to message a player.");
				clientListener.sendList();
				chatArea.append(this.Name + " has connected." + "\n");
				refresh();

				while ((inLine = input.readLine()) != null) {
					String[] params = inLine.split(" ");
					// if they didn't make a roll, say what they typed
					if (!params[0].equalsIgnoreCase("roll")
							&& !params[0].equalsIgnoreCase("r"))
						chatArea.setText(chatArea.getText() + " " + Name + ": "
								+ inLine + "\n");
					// begin looking for command phrases.
					switch (params[0].toLowerCase()) {
					case "exit":
						input.close();
						listener.close();
						break;
					case "ping":
						out.println(System.currentTimeMillis() + "");
						break;
					case "roll":
					case "r":
						String result = DiceRoll.roll(params);
						out.println("You " + result);
						chatArea.append(Name + result + "\n");
						break;
					default:
						this.broadcast(inLine);
						chatArea.append(inputLine.getText());
						break;
					case "bc":
						this.broadcast(inLine.substring(2));
						chatArea.append(inputLine.getText());
						break;
					case "msg":
						final StringBuilder cheese = new StringBuilder(
								params[0] + " " + params[1] + " " + Name + ": ");
						for (int i = 2; i < params.length; i++)
							cheese.append(params[i] + " ");
						Message(GoIPDM.clientListener, cheese.toString());
						chatArea.append(inputLine.getText() + "\n");
						break;
					case "setname":
						String newName = params[1];
						// enforces unique names.

						long sharedNames = clientListener
								.getClients()
								.stream()
								.filter(tempClient -> tempClient.Name
										.equalsIgnoreCase(newName)).count();
						if (sharedNames > 0) {
							out.println("Could not change name: Username taken.");
						} else {
							chatArea.append(Name);
							Name = newName;
							out.println("Name changed to " + params[1]);
							final StringBuilder newplayerlist = new StringBuilder(
									"");
							clientListener
									.getClients()
									.stream()
									.forEach(
											t -> newplayerlist.append(t.Name
													+ "\n"));

							listPlayers.setText(newplayerlist.toString());
							clientListener.sendList();
							chatArea.append(" name changed to " + Name + "\n");
						}
						break;

					case "reset":
						kick(this); // a "reset" command is effectively the
						// same as kicking yourself.
						break;
					case "help":
						out.println("Roll x y - Roll x number of dice each with y sides. You can specify 2 or more types of dice as so: Roll a b y z where a and y are number of dice and b/z are number of sides per dice. A 1d6 + 2d4 attack would be Roll 1 6 2 4. Use setname (name) to change your username. ");
						break;
					}
				}
			} catch (IOException e) {
				chatArea.append(Name + " has disconnected. \n");
				out.close();
				clientListener.removeClient(this);
				refresh();
				clientListener.sendList();

			} catch (Exception e) {
				out.println("Server said no.");
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
		// when it closes...
		Runtime.getRuntime().addShutdownHook(
				new Thread(() -> {
					// kick everyone.

						while (ClientConnecter.clients.size() > 0) {
							ClientConnecter.clients.get(0).out
									.println("Server closing...");
							kick(ClientConnecter.clients.get(0));
						}

						// Needed to throw a run time exception to be able to
						// close
						// it...
						// don't ask. I don't know.

						int[] x = {};
						x[1] = 0;
						System.exit(1);
					}));
		frame.setResizable(false);
		frame.setBounds(100, 100, 542, 302);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		scrollPane.setBounds(10, 11, 409, 222);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);
		frame.getContentPane().add(scrollPane);

		scrollPane.setViewportView(chatArea);
		chatArea.setFocusable(false);
		chatArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		chatArea.setText("Quest has begun! Listening for players." + "\n");

		scrollPane2.setBounds(429, 24, 97, 209);
		frame.getContentPane().add(scrollPane2);

		listPlayers.setLineWrap(true);
		listPlayers.setFocusable(false);
		scrollPane2.setViewportView(listPlayers);
		listPlayers.setTabSize(3);
		listPlayers.setEditable(false);

		lblPlayers.setBounds(429, 11, 91, 14);
		frame.getContentPane().add(lblPlayers);

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

		btnRoll.setFocusable(false);
		btnRoll.addActionListener(e -> {
			final DiceBag db = new DiceBag();
			db.btnRoll.addActionListener(y -> {
				String temp = "roll " + db.j[0].getText().trim() + "d100 "
						+ db.j[1].getText().trim() + "d20 "
						+ db.j[2].getText().trim() + "d12 "
						+ db.j[3].getText().trim() + "d10 "
						+ db.j[4].getText().trim() + "d8 "
						+ db.j[5].getText().trim() + "d6 "
						+ db.j[6].getText().trim() + "d4";
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

	}

}
