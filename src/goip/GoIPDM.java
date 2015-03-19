package goip;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import dice.DiceBag;
import dice.DiceRoll;

import javax.swing.*;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;

import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

import Encryption.DecryptedWriter;
import Encryption.EncryptedReader;

public final class GoIPDM {

    // GUI variables
    private final static JFrame frame = new JFrame();
    private final static JPanel contentPane = new JPanel();
    private final static JTextField inputLine = new JTextField();
    public final static JTextArea chatArea = new JTextArea();
    private final static DefaultListModel<String> listModel = new DefaultListModel<String>();
    private final static JList<String> listPlayers = new JList<String>(
	    listModel);
    private final static JLabel lblPlayers = new JLabel("Players List");
    private final static JScrollPane scrollPane = new JScrollPane();
    private final static JScrollPane scrollPane2 = new JScrollPane();
    private final static JButton btnRoll = new JButton("Dice Bag");

    // Communication variables
    private static ClientConnecter clientListener;
    private static String lastsent = "";
    // DB
    public final static ArrayList<DiceBag> dblist = new ArrayList<DiceBag>();
    private final static JMenuBar menuBar = new PlayerMenu(dblist, true);

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
    public static void Message(String from, String to, String message) {
	if (to.equalsIgnoreCase("DM"))
	    return;

	try {
	    // send message down the socket's output stream.
	    ClientConnecter.getClient(to).out.println("(from " + from + "): "
		    + message);
	} catch (Exception e) {
	    chatArea.append("Player not found: " + to);

	}
    }

    // connects clients and keeps an ArrayList of all connected clients.
    // Great for broadcasting, messaging between active clients.
    static class ClientConnecter extends Thread {
	private static ServerSocket serverSocket = null;

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
	    temp.append("DM~");
	    clients.stream().forEach(x -> temp.append(x.Name + "~"));

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
		    // try to connect
		    clients.add(new ClientHandler(serverSocket.accept(),
			    serverSocket.accept()));

		    // Each client gets their own thread.
		    clients.get(clients.size() - 1).start();

		} catch (IOException e) {
		    System.err.println("Can't Accept connection attempt");
		}
	    }
	}

	public static ClientHandler getClient(String x) {
	    return clients.stream().filter(y -> y.Name.equalsIgnoreCase(x))
		    .findFirst().orElse(null);
	}

    }

    /**
     * Create the application.
     */
    public GoIPDM() {
	initialize();
    }

    // this one is for the DM to broadcast

    static void broadcast(String message) {
	int begin;
	if (message.length() > 2
		&& message.substring(0, 2).equalsIgnoreCase("bc"))
	    begin = 3;
	else
	    begin = 0;
	ClientConnecter.clients.stream().filter(x -> x.listener.isBound())
		.filter(x -> x.listener.isConnected())
		.forEach(x -> x.out.println("DM: " + message.substring(begin)));
    }

    // refreshes the player list
    public static void refresh() {
	final StringBuilder newplayerlist = new StringBuilder("");

	clientListener.getClients().stream()
		.forEach(x -> newplayerlist.append(x.Name + "\n"));
	listModel.removeAllElements();
	Arrays.stream(newplayerlist.toString().split("\n")).forEach(

	user -> listModel.addElement(user + "\n"));

    }

    private static void kick(ClientHandler x) {
	clientListener.removeClient(x);
	x.out.println("You have been disconnected.");
	x.out.println((String) null); // signals the listener to stop listening.
	x.out.close();
	try {
	    if (!x.listener.isClosed())
		x.listener.close();
	} catch (Exception e) {
	} // should never fail; only attempts to close if it's not closed.

	refresh();
	clientListener.sendList();
	GoIPDM.broadcast(x.Name + " has been kicked.");
    }

    // looks for commands in the server-side chat
    private void Decipher(String outter) {

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
	    ClientHandler noob = ClientConnecter.getClient(banana[1]);
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
	    StringBuilder message = new StringBuilder();
	    String[] a = outter.split(" ");
	    for (int x = 2; x < a.length; x++)
		message.append(a[x] + " ");
	    Message("DM", a[1], message.toString());
	    chatArea.append("To " + a[1] + message.toString());
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
							Message("LOOT",
								user,
								" Loot_ "
									+ temper[1]);
						    }
						});
			    });

	case "showroll":
	case "sr":
	    String result = DiceRoll.roll(outter, "rolled-");
	    chatArea.append(result + "\n");
	    broadcast(result);
	    break;
	case "roll":
	case "r":
	    chatArea.append(DiceRoll.roll(outter, "") + "\n");
	    break;
	case "bc":
	default:
	    GoIPDM.broadcast(outter);
	    chatArea.append("DM: " + outter + "\n");
	    break;
	}

    }

    // an individual client's thread and associated objects/methods
    static class ClientHandler extends Thread {
	private final Socket listener;
	private final Socket playerListSocket;
	public String Name;
	private final String IP;
	private final DecryptedWriter PlayerListWriter;
	private final DecryptedWriter out;
	private EncryptedReader input;

	// Constructor with player list enabled
	public ClientHandler(Socket temp, Socket temp2) throws IOException {
	    this.listener = temp;
	    out = new DecryptedWriter(listener.getOutputStream(), true);

	    this.Name = this.listener.getInetAddress().toString()
		    .replace("/", "").trim();
	    this.IP = this.Name;
	    this.playerListSocket = temp2;
	    PlayerListWriter = new DecryptedWriter(
		    playerListSocket.getOutputStream(), true);

	}

	private void interpret(String inLine) throws IOException {
	    String[] params = inLine.split(" ");
	    // if they didn't make a roll, say what they typed
	    if (!params[0].equalsIgnoreCase("roll")
		    && !params[0].equalsIgnoreCase("r")
		    && !params[0].equalsIgnoreCase("msg"))
		chatArea.setText(chatArea.getText() + " " + Name + ": "
			+ inLine + "\n");
	    if (inLine.contains("~")) {
		return;
	    }
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
		String result = DiceRoll.roll(inLine, this.Name);
		out.println(result);
		chatArea.append(this.Name + result.replace(this.Name, "")
			+ "\n");
		break;
	    default:
	    case "bc":
		this.broadcast(inLine);
		chatArea.append(inputLine.getText());
		break;
	    case "msg":
		final StringBuilder cheese = new StringBuilder();
		for (int i = 2; i < params.length; i++)
		    cheese.append(params[i] + " ");
		Message(this.Name, params[1], cheese.toString());
		if (params[1].equals("DM"))
		    chatArea.append("(" + this.Name + "): " + cheese.toString()
			    + "\n");
		else
		    chatArea.append("(" + this.Name + " to " + params[1]
			    + "): " + cheese.toString() + "\n");
		break;
	    case "setname":
		String newName = params[1];
		// enforces unique names.
		long sharedNames = clientListener.getClients().stream()
			.filter(c -> c.Name.equalsIgnoreCase(newName)).count();
		if (sharedNames > 0) {
		    out.println("Could not change name: Username taken.");
		} else {
		    chatArea.append(Name);
		    Name = newName;
		    out.println("Name changed to " + params[1] + ".");
		    refresh();
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

	// player broadcasting without split string
	private void broadcast(String message) {
	    int begin;
	    if (message.length() > 2
		    && message.substring(0, 2).equalsIgnoreCase("bc"))
		begin = 3;
	    else
		begin = 0;
	    ClientConnecter.clients
		    .stream()
		    .filter(x -> x.listener.isBound())
		    .filter(x -> x.listener.isConnected())
		    .forEach(
			    x -> x.out.println(this.Name + ": "
				    + message.substring(begin)));
	}

	@Override
	public void run() {
	    try {

		if (clientListener.checkIPs(this.IP)
			&& !this.IP.equals("127.0.0.1"))
		    throw new Exception("Duplicate LAN connection.");

		input = new EncryptedReader(new InputStreamReader(
			listener.getInputStream()));
		out.println("Connected! "
			+ "Please change your username with setname [username]. "
			+ "You may also use roll xdy to roll x number of y sided dice! "
			+ "msg [username] to message a player.");

		clientListener.sendList();
		chatArea.append(this.Name + " has connected." + "\n");
		refresh();

		input.lines().forEach(e -> {
		    try {
			if (e != null)
			    interpret(e);
		    } catch (IOException err) {
			chatArea.append(Name + " has disconnected.\n");
			out.close();
			clientListener.removeClient(this);
			refresh();
			clientListener.sendList();
		    }
		});

	    } catch (Exception e) {
		out.println("Server said no: " + e.getMessage());
		out.close();
		chatArea.append(Name + " has disconnected.\n--"
			+ e.getMessage() + "--\n");

		clientListener.removeClient(this);
		refresh();
		clientListener.sendList();
	    }
	}
    }

    public static void setBehavior(DiceBag db) {
	db.setButtonBehavior(y -> {
	    GoIPDM.chatArea.append(db.localRoll() + "\n");
	    if (db.getShowRoll())
		GoIPDM.broadcast(" rolled a " + db.localRoll().substring(3));
	});
	db.setStatButtonBehavior(z -> GoIPDM.chatArea.append(DiceRoll
		.statroll() + "\n"));
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
	    frame.setTitle("GoIP DM - Could not find local IP.");
	}

	// when it closes...
	Thread closer = new Thread(() -> {
	    broadcast("Server closing...");
	    broadcast((String) null);
	    System.exit(0);
	});

	Runtime.getRuntime().addShutdownHook(closer);
	frame.setResizable(false);
	frame.setBounds(100, 100, 540, 320);
	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	contentPane.setLayout(null);

	scrollPane.setBounds(10, 11, 409, 222);
	scrollPane
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	contentPane.add(scrollPane);

	scrollPane.setViewportView(chatArea);
	((DefaultCaret) chatArea.getCaret())
		.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	chatArea.setFocusable(false);
	chatArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
	chatArea.setEditable(false);
	chatArea.setWrapStyleWord(true);
	chatArea.setLineWrap(true);
	chatArea.setText("Quest has begun! Listening for players." + "\n");

	scrollPane2.setBounds(429, 24, 97, 209);
	contentPane.add(scrollPane2);

	listPlayers.setFocusable(false);
	scrollPane2.setViewportView(listPlayers);
	listPlayers
		.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	listPlayers.addListSelectionListener(e -> {
	    if (listPlayers.getSelectedValue() != null)
		inputLine.setText("msg " + listPlayers.getSelectedValue());
	    listPlayers.clearSelection();
	    inputLine.requestFocusInWindow();
	    inputLine.setCaretPosition(inputLine.getText().length());
	});
	listPlayers.setVisibleRowCount(-1);

	lblPlayers.setBounds(429, 11, 91, 14);
	contentPane.add(lblPlayers);

	inputLine.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
		    inputLine.setText(lastsent);
		    break;
		case KeyEvent.VK_ENTER:
		    String text = inputLine.getText().trim();
		    if (text != null && !text.equals(""))
			Decipher(text);
		    lastsent = text;
		    inputLine.setText("");
		    break;
		}
	    }
	});
	inputLine.setBounds(10, 244, 409, 22);
	contentPane.add(inputLine);
	inputLine.setColumns(10);

	btnRoll.setFocusable(false);
	btnRoll.addActionListener(e -> {
	    final DiceBag db = new DiceBag("DM");
	    setBehavior(db);
	    db.setVisible(true);
	});
	btnRoll.setBounds(429, 244, 97, 23);
	contentPane.add(btnRoll);
	frame.add(menuBar, BorderLayout.NORTH);
	frame.add(contentPane, BorderLayout.CENTER);

    }
}
